package com.example.input

import android.content.Context
import com.example.model.GamepadButtonType
import com.example.model.GamepadControlItem
import com.example.model.GamepadProfile
import com.example.model.GamepadState
import com.example.model.InputKeyDefinition
import com.example.model.InputMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sqrt

class GamepadInputManager(context: Context) {

    val bluetoothDriver = BluetoothHidDriver(context)
    val udpDriver = UdpNetworkDriver()
    val adbDriver = AdbShizukuDriver(context)
    val hapticDriver = HapticDriver(context)

    private val _currentState = MutableStateFlow(GamepadState())
    val currentState: StateFlow<GamepadState> = _currentState.asStateFlow()

    private var activeProfile: GamepadProfile? = null

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
                // HUD mode
            }
        }
    }

    /**
     * Updates an analog joystick with deadzone and sensitivity curves
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
    }

    /**
     * Set a button pressed state (true = down, false = up)
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
                adbDriver.sendButtonPress(button)
            }
            val updated = _currentState.value.copy(
                pressedButtons = current,
                timestamp = System.currentTimeMillis()
            )
            _currentState.value = updated
            dispatchToActiveDrivers(updated)
        }
    }

    /**
     * Handles press/release for any on-screen button with its custom key binding
     */
    fun setCustomControlPressed(item: GamepadControlItem, isPressed: Boolean) {
        val def = item.getResolvedKeyDefinition()
        if (def != null) {
            if (def.isGamepadInput && def.gamepadButtonType != null) {
                setButtonPressed(def.gamepadButtonType, isPressed)
            } else {
                if (isPressed) {
                    hapticDriver.clickFeedback()
                    adbDriver.sendKeyDefinitionPress(def)
                }
                if (def.hidUsageId != 0) {
                    bluetoothDriver.sendKeyboardKey(def.hidUsageId, isPressed)
                }
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
                // Overlay HUD or ADB handled on press
            }
        }
    }

    private fun applyDeadzoneAndSensitivity(
        x: Float,
        y: Float,
        deadzone: Float,
        sensitivity: Float
    ): Pair<Float, Float> {
        val magnitude = sqrt((x * x + y * y).toDouble()).toFloat()
        if (magnitude < deadzone) {
            return Pair(0f, 0f)
        }

        // Scaled radial deadzone
        val scaledMagnitude = ((magnitude - deadzone) / (1.0f - deadzone)).coerceIn(0f, 1f) * sensitivity
        val normalizedX = (x / magnitude) * scaledMagnitude
        val normalizedY = (y / magnitude) * scaledMagnitude

        return Pair(
            normalizedX.coerceIn(-1f, 1f),
            normalizedY.coerceIn(-1f, 1f)
        )
    }

    fun releaseAll() {
        val cleared = GamepadState()
        _currentState.value = cleared
        dispatchToActiveDrivers(cleared)
    }

    fun destroy() {
        releaseAll()
        bluetoothDriver.stop()
        udpDriver.stop()
    }
}
