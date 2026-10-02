package com.example.input

import android.content.Context
import android.util.Log
import com.example.model.GamepadButtonType
import com.example.model.GamepadControlItem
import com.example.model.GamepadProfile
import com.example.model.GamepadState
import com.example.model.InputKeyDefinition
import com.example.model.InputMode
import com.example.service.FlexiPadAccessibilityService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap

/**
 * Unified Gamepad Input Manager:
 * Coordinates the full input pipeline:
 * DOWN -> HOLD -> UP -> RELEASE
 * Dispatches simultaneously to:
 * 1) Android Accessibility Service (genuine on-screen touch injection into GForce / games)
 * 2) Shizuku / Root / ADB Shell (system keyevents)
 * 3) Bluetooth HID Composite Device (physical hardware gamepad to PC / Host)
 * 4) High-speed UDP stream (Moonlight / ViGEm / Cloud server)
 * 5) Haptic tactile feedback engine
 */
class GamepadInputManager(private val context: Context) {
    private val TAG = "GamepadInputManager"

    val bluetoothDriver = BluetoothHidDriver(context)
    val udpDriver = UdpNetworkDriver()
    val adbDriver = AdbShizukuDriver(context)
    val hapticDriver = HapticDriver(context)

    private val _currentState = MutableStateFlow(GamepadState())
    val currentState: StateFlow<GamepadState> = _currentState.asStateFlow()

    private val _recentLogs = MutableStateFlow<List<String>>(emptyList())
    val recentLogs: StateFlow<List<String>> = _recentLogs.asStateFlow()

    private var activeProfile: GamepadProfile? = null

    // Track active touch pointer IDs assigned to each control for touch gesture injection
    private val controlPointerMap = ConcurrentHashMap<String, Int>()
    private var nextPointerId = 1

    private fun addLog(log: String) {
        val current = _recentLogs.value.toMutableList()
        if (current.size >= 15) current.removeAt(0)
        current.add("[${System.currentTimeMillis() % 100000}] $log")
        _recentLogs.value = current
    }

    fun setProfile(profile: GamepadProfile) {
        activeProfile = profile
        hapticDriver.isEnabled = profile.hapticFeedback
        when (profile.inputMode) {
            InputMode.BLUETOOTH_HID -> {
                bluetoothDriver.start()
            }
            InputMode.UDP_NETWORK -> {
                udpDriver.start()
            }
            InputMode.SHIZUKU_ADB -> {
                adbDriver.checkStatus()
            }
            InputMode.OVERLAY_HUD -> {
                // Overlay HUD uses Accessibility + Local Dispatch
            }
        }
    }

    /**
     * Updates an analog joystick with independent X/Y axes.
     * Fully supports BOTH Left (LS) and Right (RS) joysticks simultaneously!
     */
    fun updateJoystick(isLeft: Boolean, rawX: Float, rawY: Float) {
        val clampedX = rawX.coerceIn(-1f, 1f)
        val clampedY = rawY.coerceIn(-1f, 1f)
        val previous = _currentState.value

        if (isLeft && previous.leftStickX == clampedX && previous.leftStickY == clampedY) {
            return
        }
        if (!isLeft && previous.rightStickX == clampedX && previous.rightStickY == clampedY) {
            return
        }

        val updated = if (isLeft) {
            previous.copy(leftStickX = clampedX, leftStickY = clampedY, timestamp = System.currentTimeMillis())
        } else {
            previous.copy(rightStickX = clampedX, rightStickY = clampedY, timestamp = System.currentTimeMillis())
        }
        _currentState.value = updated
        dispatchToActiveDrivers(updated)

        // Inject touch motion via AccessibilityService if active
        val a11y = FlexiPadAccessibilityService.instance
        if (a11y != null) {
            val controlId = if (isLeft) "joystick_left" else "joystick_right"
            val item = activeProfile?.controls?.firstOrNull {
                it.type == (if (isLeft) GamepadButtonType.JOYSTICK_LEFT else GamepadButtonType.JOYSTICK_RIGHT)
            }

            if (item != null) {
                val metrics = context.resources.displayMetrics
                val centerX = item.xPercent * metrics.widthPixels
                val centerY = item.yPercent * metrics.heightPixels
                val maxRadiusPx = (item.sizeDp * metrics.density) * 0.42f
                val targetX = centerX + (clampedX * maxRadiusPx)
                val targetY = centerY + (clampedY * maxRadiusPx)

                val isNeutral = kotlin.math.abs(clampedX) < 0.02f && kotlin.math.abs(clampedY) < 0.02f

                if (isNeutral) {
                    val activePointer = controlPointerMap.remove(controlId)
                    if (activePointer != null) {
                        a11y.injectTouchUp(activePointer, centerX, centerY)
                        addLog("${if (isLeft) "LS" else "RS"} UP -> Neutral (0, 0)")
                    }
                } else {
                    val existingPointer = controlPointerMap[controlId]
                    if (existingPointer == null) {
                        val newPointer = nextPointerId++
                        controlPointerMap[controlId] = newPointer
                        a11y.injectTouchDown(newPointer, targetX, targetY)
                        addLog("${if (isLeft) "LS" else "RS"} DOWN -> ($targetX, $targetY)")
                    } else {
                        a11y.injectTouchMove(existingPointer, targetX, targetY)
                    }
                }
            }
        }
    }

    /**
     * Updates an analog trigger (0f to 1f)
     */
    fun updateTrigger(isLeft: Boolean, value: Float) {
        val clamped = value.coerceIn(0f, 1f)
        val previous = _currentState.value
        val prevVal = if (isLeft) previous.l2Trigger else previous.r2Trigger
        if (prevVal == clamped) {
            return
        }

        val updated = if (isLeft) {
            previous.copy(l2Trigger = clamped, timestamp = System.currentTimeMillis())
        } else {
            previous.copy(r2Trigger = clamped, timestamp = System.currentTimeMillis())
        }

        if (clamped > 0.8f && prevVal <= 0.8f) {
            hapticDriver.heavyClickFeedback()
        }

        _currentState.value = updated
        dispatchToActiveDrivers(updated)

        val triggerBtn = if (isLeft) GamepadButtonType.BUTTON_L2 else GamepadButtonType.BUTTON_R2
        val isPressed = clamped > 0.5f
        val wasPressed = prevVal > 0.5f
        if (isPressed != wasPressed) {
            setButtonPressed(triggerBtn, isPressed)
        }
    }

    /**
     * Set a button pressed state (true = DOWN / HOLD, false = UP / RELEASE)
     */
    fun setButtonPressed(button: GamepadButtonType, isPressed: Boolean) {
        val current = _currentState.value.pressedButtons.toMutableSet()
        val changed = if (isPressed) {
            current.add(button)
        } else {
            current.remove(button)
        }

        if (changed) {
            if (isPressed) {
                hapticDriver.clickFeedback()
            }
            adbDriver.sendButtonState(button, isPressed)
            val updated = _currentState.value.copy(
                pressedButtons = current,
                timestamp = System.currentTimeMillis()
            )
            _currentState.value = updated
            dispatchToActiveDrivers(updated)
            addLog("BUTTON ${button.defaultLabel} -> ${if (isPressed) "DOWN" else "UP"}")
        }
    }

    /**
     * Handles press/release for on-screen controls, with direct screen touch injection via AccessibilityService
     */
    fun setCustomControlPressed(item: GamepadControlItem, isPressed: Boolean) {
        val metrics = context.resources.displayMetrics
        val screenX = item.xPercent * metrics.widthPixels
        val screenY = item.yPercent * metrics.heightPixels

        // 1. Accessibility Service System Touch Injection (DOWN -> HOLD -> UP)
        val a11y = FlexiPadAccessibilityService.instance
        if (a11y != null) {
            if (isPressed) {
                val pointerId = nextPointerId++
                controlPointerMap[item.id] = pointerId
                a11y.injectTouchDown(pointerId, screenX, screenY)
                addLog("TOUCH [${item.label}] DOWN at (${screenX.toInt()}, ${screenY.toInt()})")
            } else {
                val pointerId = controlPointerMap.remove(item.id)
                if (pointerId != null) {
                    a11y.injectTouchUp(pointerId, screenX, screenY)
                    addLog("TOUCH [${item.label}] UP")
                }
            }
        }

        // 2. Hardware / Keycode Injection
        val def = item.getResolvedKeyDefinition()
        if (def != null) {
            if (def.isGamepadInput && def.gamepadButtonType != null) {
                setButtonPressed(def.gamepadButtonType, isPressed)
            } else {
                if (isPressed) {
                    hapticDriver.clickFeedback()
                }
                adbDriver.sendKeyDefinitionState(def, isPressed)
                if (def.hidUsageId != 0) {
                    bluetoothDriver.sendKeyboardKey(def.hidUsageId, isPressed)
                }
                addLog("KEY [${def.label}] -> ${if (isPressed) "DOWN" else "UP"}")
            }
        } else {
            setButtonPressed(item.type, isPressed)
        }
    }

    private fun dispatchToActiveDrivers(state: GamepadState) {
        val mode = activeProfile?.inputMode ?: InputMode.OVERLAY_HUD
        when (mode) {
            InputMode.BLUETOOTH_HID -> {
                bluetoothDriver.sendGamepadState(state)
            }
            InputMode.UDP_NETWORK -> {
                udpDriver.sendState(state)
            }
            else -> {
                // If Bluetooth HID is connected, dispatch to it as well so PC / GForce receives genuine hardware gamepad events
                if (bluetoothDriver.isConnected.value) {
                    bluetoothDriver.sendGamepadState(state)
                }
            }
        }
    }

    /**
     * Safely releases all active buttons and touches to prevent sticky keys
     */
    fun releaseAll() {
        controlPointerMap.clear()
        FlexiPadAccessibilityService.instance?.releaseAll()
        adbDriver.releaseAll()
        val cleared = GamepadState()
        _currentState.value = cleared
        dispatchToActiveDrivers(cleared)
        addLog("ALL INPUTS RELEASED")
    }

    fun destroy() {
        releaseAll()
        bluetoothDriver.stop()
        udpDriver.stop()
    }
}
