package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.WarPadApplication
import com.example.model.GamepadPresets
import com.example.model.GamepadProfile
import com.example.model.InputMode
import com.example.service.GamepadOverlayService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

    val isOverlayRunning: StateFlow<Boolean> = GamepadOverlayService.isRunning

    init {
        checkOverlayPermission()
        viewModelScope.launch {
            repository.allProfiles.collect { list ->
                if (_selectedProfile.value == null && list.isNotEmpty()) {
                    // Default to primary layout
                    val primary = list.firstOrNull { it.name.contains("Oyun Kontrolü", ignoreCase = true) } ?: list.first()
                    _selectedProfile.value = primary
                }
            }
        }
    }

    fun checkOverlayPermission() {
        _canDrawOverlays.value = Settings.canDrawOverlays(getApplication())
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
                cur.name.contains("Oyun", ignoreCase = true) -> GamepadPresets.createWarThunderProfile()
                cur.name.contains("Xbox", ignoreCase = true) -> GamepadPresets.createXboxStandardProfile()
                else -> GamepadPresets.createFpsProfile()
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
                // Open in Play Store
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
