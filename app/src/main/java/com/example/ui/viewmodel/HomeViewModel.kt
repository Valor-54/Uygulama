package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.WarPadApplication
import com.example.model.GamepadPresets
import com.example.model.GamepadProfile
import com.example.model.InputMode
import com.example.service.FlexiPadAccessibilityService
import com.example.service.GamepadOverlayService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenOrientationOption(val title: String, val activityOrientation: Int) {
    AUTO("Otomatik (Sensör)", ActivityInfo.SCREEN_ORIENTATION_SENSOR),
    LANDSCAPE("Yatay (Landscape)", ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE),
    PORTRAIT("Dikey (Portrait)", ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
}

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as WarPadApplication).repository

    val allProfiles: StateFlow<List<GamepadProfile>> = repository.allProfiles
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedProfile = MutableStateFlow<GamepadProfile?>(null)
    val selectedProfile: StateFlow<GamepadProfile?> = _selectedProfile.asStateFlow()

    private val _canDrawOverlays = MutableStateFlow(false)
    val canDrawOverlays: StateFlow<Boolean> = _canDrawOverlays.asStateFlow()

    private val _isAccessibilityEnabled = MutableStateFlow(false)
    val isAccessibilityEnabled: StateFlow<Boolean> = _isAccessibilityEnabled.asStateFlow()

    private val _orientationOption = MutableStateFlow(ScreenOrientationOption.AUTO)
    val orientationOption: StateFlow<ScreenOrientationOption> = _orientationOption.asStateFlow()

    val isOverlayRunning: StateFlow<Boolean> = GamepadOverlayService.isRunning

    init {
        checkOverlayPermission()
        checkAccessibilityPermission()

        val prefs = application.getSharedPreferences(GamepadOverlayService.PREFS_NAME, Context.MODE_PRIVATE)
        val savedOrientationName = prefs.getString(GamepadOverlayService.PREF_ORIENTATION_MODE, ScreenOrientationOption.AUTO.name)
        _orientationOption.value = ScreenOrientationOption.entries.firstOrNull { it.name == savedOrientationName } ?: ScreenOrientationOption.AUTO

        viewModelScope.launch {
            repository.allProfiles.collect { list ->
                if (_selectedProfile.value == null && list.isNotEmpty()) {
                    val activeId = prefs.getLong(GamepadOverlayService.PREF_ACTIVE_PROFILE_ID, 1L)
                    val matching = list.firstOrNull { it.id == activeId } ?: list.first()
                    _selectedProfile.value = matching
                }
            }
        }
    }

    fun checkOverlayPermission() {
        _canDrawOverlays.value = Settings.canDrawOverlays(getApplication())
    }

    fun checkAccessibilityPermission() {
        val context: Context = getApplication()
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: ""
        val myService = "${context.packageName}/${FlexiPadAccessibilityService::class.java.name}"
        val isEnabled = enabledServices.contains(myService) || FlexiPadAccessibilityService.isConnected()
        _isAccessibilityEnabled.value = isEnabled
    }

    fun setOrientationOption(option: ScreenOrientationOption) {
        _orientationOption.value = option
        val prefs = getApplication<Application>().getSharedPreferences(GamepadOverlayService.PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(GamepadOverlayService.PREF_ORIENTATION_MODE, option.name).apply()
    }

    fun selectProfile(profile: GamepadProfile) {
        _selectedProfile.value = profile
        if (isOverlayRunning.value) {
            GamepadOverlayService.start(getApplication(), profile.id)
        }
    }

    fun startControls(context: Context) {
        val profile = _selectedProfile.value ?: GamepadPresets.createWarThunderProfile()
        GamepadOverlayService.start(context, profile.id)
    }

    fun stopControls(context: Context) {
        GamepadOverlayService.stop(context)
    }

    fun toggleEditMode(context: Context) {
        GamepadOverlayService.toggleEdit(context)
    }

    fun updateSelectedProfile(updated: GamepadProfile) {
        _selectedProfile.value = updated
        viewModelScope.launch {
            repository.saveProfile(updated)
            if (isOverlayRunning.value) {
                GamepadOverlayService.start(getApplication(), updated.id)
            }
        }
    }

    fun setInputMode(mode: InputMode) {
        val cur = _selectedProfile.value ?: return
        updateSelectedProfile(cur.copy(inputMode = mode))
    }

    fun setGlobalOpacity(opacity: Float) {
        val cur = _selectedProfile.value ?: return
        val updatedControls = cur.controls.map { it.copy(opacity = opacity) }
        updateSelectedProfile(cur.copy(globalOpacity = opacity, controls = updatedControls))
    }

    fun setJoystickSensitivity(sensitivity: Float) {
        val cur = _selectedProfile.value ?: return
        updateSelectedProfile(cur.copy(
            leftStickSensitivity = sensitivity,
            rightStickSensitivity = sensitivity
        ))
    }

    fun setJoystickDeadzone(deadzone: Float) {
        val cur = _selectedProfile.value ?: return
        updateSelectedProfile(cur.copy(
            leftStickDeadzone = deadzone,
            rightStickDeadzone = deadzone
        ))
    }

    fun setHapticEnabled(enabled: Boolean) {
        val cur = _selectedProfile.value ?: return
        updateSelectedProfile(cur.copy(hapticFeedback = enabled))
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            val cur = _selectedProfile.value ?: return@launch
            val fresh = when {
                cur.name.contains("Xbox", ignoreCase = true) -> GamepadPresets.createXboxStandardProfile()
                cur.name.contains("FPS", ignoreCase = true) -> GamepadPresets.createFpsProfile()
                else -> GamepadPresets.createWarThunderProfile()
            }.copy(id = cur.id)
            repository.saveProfile(fresh)
            _selectedProfile.value = fresh
        }
    }

    fun duplicateCurrentProfile() {
        viewModelScope.launch {
            val cur = _selectedProfile.value ?: return@launch
            repository.duplicateProfile(cur)
        }
    }

    fun deleteCurrentProfile() {
        val cur = _selectedProfile.value ?: return
        if (cur.isPreset) return // Don't delete built-in presets
        viewModelScope.launch {
            repository.deleteProfile(cur.id)
            _selectedProfile.value = allProfiles.value.firstOrNull()
        }
    }

    fun launchGeForceNow(context: Context): Boolean {
        return try {
            val launchIntent = context.packageManager.getLaunchIntentForPackage("com.nvidia.geforcenow")
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                true
            } else {
                val playStoreIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.nvidia.geforcenow")
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(playStoreIntent)
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}
