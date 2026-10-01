package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ControlJsonParser
import com.example.model.GamepadButtonType
import com.example.model.GamepadPresets
import com.example.model.GamepadState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("WarPad Pro", appName)
    }

    @Test
    fun `war thunder preset has tactical buttons and free center`() {
        val wtProfile = GamepadPresets.createWarThunderProfile()
        assertNotNull(wtProfile)
        assertTrue(wtProfile.centerAimFreeZone)

        // Verify key War Thunder buttons exist
        val hasMainGun = wtProfile.controls.any { it.type == GamepadButtonType.BUTTON_R2 }
        val hasZoom = wtProfile.controls.any { it.type == GamepadButtonType.BUTTON_L2 }
        val hasLeftStick = wtProfile.controls.any { it.type == GamepadButtonType.JOYSTICK_LEFT }

        assertTrue("Main Gun (RT) must exist", hasMainGun)
        assertTrue("Zoom / Scope (LT) must exist", hasZoom)
        assertTrue("Left Stick for tank driving must exist", hasLeftStick)
    }

    @Test
    fun `gamepad state button pressed check`() {
        val state = GamepadState(
            leftStickX = 0.5f,
            leftStickY = -0.5f,
            pressedButtons = setOf(GamepadButtonType.BUTTON_A, GamepadButtonType.BUTTON_R2)
        )

        assertTrue(state.isPressed(GamepadButtonType.BUTTON_A))
        assertTrue(state.isPressed(GamepadButtonType.BUTTON_R2))
        assertFalse(state.isPressed(GamepadButtonType.BUTTON_B))
        assertTrue(state.isLeftStickActive)
    }

    @Test
    fun `control json serialization roundtrip`() {
        val wt = GamepadPresets.createWarThunderProfile()
        val json = ControlJsonParser.toJson(wt.controls)
        val deserialized = ControlJsonParser.fromJson(json)

        assertEquals(wt.controls.size, deserialized.size)
        assertEquals(wt.controls[0].id, deserialized[0].id)
        assertEquals(wt.controls[0].type, deserialized[0].type)
    }

    @Test
    fun `key registry contains keyboard and gamepad definitions`() {
        val allKeys = com.example.model.KeyRegistry.ALL_KEYS
        assertTrue("KeyRegistry should have extensive key catalogue", allKeys.size > 50)

        assertNotNull("Space key should exist", com.example.model.KeyRegistry.findById("KB_SPACE"))
        assertNotNull("Enter key should exist", com.example.model.KeyRegistry.findById("KB_ENTER"))
        assertNotNull("W (WASD) should exist", com.example.model.KeyRegistry.findById("KB_W"))
        assertNotNull("F1 should exist", com.example.model.KeyRegistry.findById("KB_F1"))
        assertNotNull("Arrow Up should exist", com.example.model.KeyRegistry.findById("KB_ARROW_UP"))
        assertNotNull("D-PAD should exist", com.example.model.KeyRegistry.findById("GP_DPAD"))
        assertNotNull("War Thunder Fire should exist", com.example.model.KeyRegistry.findById("WT_FIRE"))
    }

    @Test
    fun `gamepad control item resolves custom key binding`() {
        val itemWithBinding = com.example.model.GamepadControlItem(
            id = "custom_test",
            type = GamepadButtonType.BUTTON_A,
            label = "SPACE",
            xPercent = 0.5f,
            yPercent = 0.5f,
            boundKeyId = "KB_SPACE"
        )
        val resolved = itemWithBinding.getResolvedKeyDefinition()
        assertNotNull(resolved)
        assertEquals(android.view.KeyEvent.KEYCODE_SPACE, resolved?.androidKeycode)
    }

    @Test
    fun `profile retains dynamic joystick center and travel distance`() {
        val profile = com.example.model.GamepadProfile(
            name = "Test Profile",
            description = "Testing dynamic center",
            leftStickMaxTravel = 0.85f,
            dynamicJoystickCenter = true
        )
        assertEquals(0.85f, profile.leftStickMaxTravel, 0.001f)
        assertTrue(profile.dynamicJoystickCenter)
    }

    @Test
    fun `overlay touch pass through allows touches on empty areas for normal buttons`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val profile = GamepadPresets.createWarThunderProfile()
        val dummyView = android.view.View(context).apply {
            layout(0, 0, 1080, 2400)
        }

        var isEdit = false
        var isVisible = true

        val touchManager = com.example.service.OverlayTouchManager(
            overlayView = dummyView,
            getCurrentProfile = { profile },
            isEditMode = { isEdit },
            isHudVisible = { isVisible }
        )

        // Center of the screen (Aim area / where HomeScreen cards and buttons are located)
        val centerHit = touchManager.isPointInsideTouchableControl(540f, 1200f)
        assertFalse("Center screen touches must pass through to normal buttons", centerHit)

        // Upper-middle area (where Düzenle, Kontrolleri Başlat, and Sliders are located)
        val upperMiddleHit = touchManager.isPointInsideTouchableControl(540f, 400f)
        assertFalse("Upper-middle screen touches must pass through to app UI", upperMiddleHit)

        // Touch on left joystick (xPercent ~ 0.15, yPercent ~ 0.70 on 1080x2400)
        val joystick = profile.controls.first { it.type == GamepadButtonType.JOYSTICK_LEFT }
        val jsX = 1080f * joystick.xPercent
        val jsY = 2400f * joystick.yPercent
        val joystickHit = touchManager.isPointInsideTouchableControl(jsX, jsY)
        assertTrue("Touches on virtual joystick must be intercepted by overlay", joystickHit)

        // Floating quick bubble (at top-start: x <= 80dp, y <= 80dp)
        val bubbleHit = touchManager.isPointInsideTouchableControl(30f, 30f)
        assertTrue("Touches on quick bubble must be intercepted", bubbleHit)

        // When HUD is hidden, all screen except bubble passes through
        isVisible = false
        assertFalse(touchManager.isPointInsideTouchableControl(jsX, jsY))
        assertTrue(touchManager.isPointInsideTouchableControl(30f, 30f))
    }

    @Test
    fun `joystick deflection calculation produces correct normalized axes`() {
        val sizePx = 300f
        val baseCenter = androidx.compose.ui.geometry.Offset(sizePx / 2f, sizePx / 2f)
        val maxTravel = (sizePx / 2f) * 0.72f
        val deadzone = 0.08f
        val sensitivity = 1.0f

        // Touch directly to the right by 80px (beyond deadzone, within maxTravel)
        val touchPos = baseCenter + androidx.compose.ui.geometry.Offset(80f, 0f)
        val delta = touchPos - baseCenter
        val dist = delta.getDistance()
        val normDist = (dist / maxTravel).coerceIn(0f, 1f)

        assertTrue("Distance should be greater than deadzone", normDist > deadzone)

        val angle = kotlin.math.atan2(delta.y, delta.x)
        val rescaled = ((normDist - deadzone) / (1f - deadzone)).coerceIn(0f, 1f) * sensitivity
        val normX = (kotlin.math.cos(angle) * rescaled).coerceIn(-1f, 1f)
        val normY = (kotlin.math.sin(angle) * rescaled).coerceIn(-1f, 1f)

        assertTrue("Normalized X should be positive for rightward deflection", normX > 0.6f)
        assertEquals(0f, normY, 0.001f)
    }
}
