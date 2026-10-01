package com.example.data

import android.content.Context
import com.example.model.GamepadPresets
import com.example.model.GamepadProfile
import com.example.model.InputMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GamepadRepository(private val dao: GamepadProfileDao) {

    val allProfiles: Flow<List<GamepadProfile>> = dao.getAllProfiles().map { entities ->
        entities.map { it.toModel() }
    }

    suspend fun ensureDefaultPresets() = withContext(Dispatchers.IO) {
        if (dao.getCount() == 0) {
            val wt = GamepadPresets.createWarThunderProfile()
            val xb = GamepadPresets.createXboxStandardProfile()
            val fps = GamepadPresets.createFpsProfile()

            dao.insertProfile(wt.toEntity())
            dao.insertProfile(xb.toEntity())
            dao.insertProfile(fps.toEntity())
        }
    }

    suspend fun getProfile(id: Long): GamepadProfile? = withContext(Dispatchers.IO) {
        dao.getProfileById(id)?.toModel()
    }

    suspend fun saveProfile(profile: GamepadProfile): Long = withContext(Dispatchers.IO) {
        val entity = profile.toEntity()
        if (profile.id == 0L) {
            dao.insertProfile(entity)
        } else {
            dao.updateProfile(entity)
            profile.id
        }
    }

    suspend fun deleteProfile(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteProfileById(id)
    }

    suspend fun duplicateProfile(profile: GamepadProfile): Long = withContext(Dispatchers.IO) {
        val copy = profile.copy(
            id = 0L,
            name = "${profile.name} (Kopya)",
            isPreset = false,
            updatedAt = System.currentTimeMillis()
        )
        dao.insertProfile(copy.toEntity())
    }

    companion object {
        fun create(context: Context): GamepadRepository {
            val db = AppDatabase.getDatabase(context)
            return GamepadRepository(db.gamepadProfileDao())
        }
    }
}

fun GamepadProfileEntity.toModel(): GamepadProfile {
    val mode = try {
        InputMode.valueOf(inputMode)
    } catch (e: Exception) {
        InputMode.OVERLAY_HUD
    }

    return GamepadProfile(
        id = id,
        name = name,
        description = description,
        isPreset = isPreset,
        controls = ControlJsonParser.fromJson(controlsJson),
        globalOpacity = globalOpacity,
        hapticFeedback = hapticFeedback,
        leftStickDeadzone = leftStickDeadzone,
        leftStickSensitivity = leftStickSensitivity,
        leftStickMaxTravel = leftStickMaxTravel,
        rightStickDeadzone = rightStickDeadzone,
        rightStickSensitivity = rightStickSensitivity,
        rightStickMaxTravel = rightStickMaxTravel,
        dynamicJoystickCenter = dynamicJoystickCenter,
        inputMode = mode,
        centerAimFreeZone = centerAimFreeZone,
        updatedAt = updatedAt
    )
}

fun GamepadProfile.toEntity(): GamepadProfileEntity {
    return GamepadProfileEntity(
        id = id,
        name = name,
        description = description,
        isPreset = isPreset,
        controlsJson = ControlJsonParser.toJson(controls),
        globalOpacity = globalOpacity,
        hapticFeedback = hapticFeedback,
        leftStickDeadzone = leftStickDeadzone,
        leftStickSensitivity = leftStickSensitivity,
        leftStickMaxTravel = leftStickMaxTravel,
        rightStickDeadzone = rightStickDeadzone,
        rightStickSensitivity = rightStickSensitivity,
        rightStickMaxTravel = rightStickMaxTravel,
        dynamicJoystickCenter = dynamicJoystickCenter,
        inputMode = inputMode.name,
        centerAimFreeZone = centerAimFreeZone,
        updatedAt = updatedAt
    )
}
