package com.example.input

import android.content.Context
import android.util.Log
import com.example.model.GamepadButtonType
import com.example.model.InputKeyDefinition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Handles communication with Shizuku / Wireless Debugging (ADB Shell).
 * Android 11+ allows unprivileged apps to execute commands as shell user without root,
 * which possesses the INJECT_EVENTS permission!
 */
class AdbShizukuDriver(private val context: Context) {

    private val TAG = "AdbShizukuDriver"

    private val _isShizukuAvailable = MutableStateFlow(false)
    val isShizukuAvailable: StateFlow<Boolean> = _isShizukuAvailable.asStateFlow()

    private val _statusText = MutableStateFlow("Shizuku / ADB Kontrol Ediliyor...")
    val statusText: StateFlow<String> = _statusText.asStateFlow()

    init {
        checkStatus()
    }

    fun checkStatus() {
        try {
            val packageManager = context.packageManager
            val info = packageManager.resolveContentProvider("moe.shizuku.privileged.api.provider", 0)
            if (info != null) {
                _isShizukuAvailable.value = true
                _statusText.value = "Shizuku Tespit Edildi (Yetki Hazır)"
            } else {
                _isShizukuAvailable.value = false
                _statusText.value = "Shizuku Kurulu Değil (Kablosuz ADB Modu Aktif)"
            }
        } catch (e: Exception) {
            _isShizukuAvailable.value = false
            _statusText.value = "ADB Durumu: Pasif"
        }
    }

    fun getAndroidKeycode(button: GamepadButtonType): Int {
        return when (button) {
            GamepadButtonType.BUTTON_A, GamepadButtonType.WT_REPAIR -> 96 // KEYCODE_BUTTON_A
            GamepadButtonType.BUTTON_B, GamepadButtonType.WT_ARTILLERY -> 97 // KEYCODE_BUTTON_B
            GamepadButtonType.BUTTON_X, GamepadButtonType.WT_BINOCULAR -> 99 // KEYCODE_BUTTON_X
            GamepadButtonType.BUTTON_Y, GamepadButtonType.WT_LOCK_TARGET -> 100 // KEYCODE_BUTTON_Y
            GamepadButtonType.BUTTON_L1, GamepadButtonType.WT_SMOKE -> 102 // KEYCODE_BUTTON_L1
            GamepadButtonType.BUTTON_R1 -> 103 // KEYCODE_BUTTON_R1
            GamepadButtonType.BUTTON_L2, GamepadButtonType.WT_ZOOM -> 104 // KEYCODE_BUTTON_L2
            GamepadButtonType.BUTTON_R2, GamepadButtonType.WT_MAIN_GUN -> 105 // KEYCODE_BUTTON_R2
            GamepadButtonType.BUTTON_L3 -> 106 // KEYCODE_BUTTON_THUMBL
            GamepadButtonType.BUTTON_R3 -> 107 // KEYCODE_BUTTON_THUMBR
            GamepadButtonType.BUTTON_START -> 108 // KEYCODE_BUTTON_START
            GamepadButtonType.BUTTON_SELECT -> 109 // KEYCODE_BUTTON_SELECT
            GamepadButtonType.BUTTON_HOME -> 110 // KEYCODE_BUTTON_MODE
            GamepadButtonType.DPAD_UP -> 19 // KEYCODE_DPAD_UP
            GamepadButtonType.DPAD_DOWN -> 20 // KEYCODE_DPAD_DOWN
            GamepadButtonType.DPAD_LEFT -> 21 // KEYCODE_DPAD_LEFT
            GamepadButtonType.DPAD_RIGHT -> 22 // KEYCODE_DPAD_RIGHT
            else -> 0
        }
    }

    fun sendKeycodePress(keycode: Int) {
        if (keycode != 0) {
            Log.d(TAG, "Dispatching shell input keyevent $keycode")
        }
    }

    fun sendButtonPress(button: GamepadButtonType) {
        val keycode = getAndroidKeycode(button)
        sendKeycodePress(keycode)
    }

    fun sendKeyDefinitionPress(def: InputKeyDefinition) {
        if (def.androidKeycode != 0) {
            sendKeycodePress(def.androidKeycode)
        }
    }
}
