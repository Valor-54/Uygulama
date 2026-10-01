package com.example.model

/**
 * Pre-configured layout presets for popular mobile and cloud gaming scenarios.
 */
object GamepadPresets {

    /**
     * Default Gaming Profile:
     * Dual-zone layout optimized for cloud gaming and Android action games.
     * Leaves the center clear for camera aiming and open field view.
     */
    fun createWarThunderProfile(): GamepadProfile {
        val controls = listOf(
            // --- LEFT ZONE (Movement & Direction) ---
            GamepadControlItem(
                id = "wt_left_stick",
                type = GamepadButtonType.JOYSTICK_LEFT,
                label = "LS",
                subLabel = "Yön / Hareket",
                xPercent = 0.14f,
                yPercent = 0.72f,
                sizeDp = 140f,
                opacity = 0.65f,
                colorHex = 0xFFFF9800, // Tactical Amber
                zone = ControlZone.LEFT
            ),
            GamepadControlItem(
                id = "wt_dpad",
                type = GamepadButtonType.DPAD,
                label = "D-PAD",
                subLabel = "Yön Tuşları",
                xPercent = 0.14f,
                yPercent = 0.32f,
                sizeDp = 115f,
                opacity = 0.60f,
                colorHex = 0xFF4CAF50,
                zone = ControlZone.LEFT
            ),
            GamepadControlItem(
                id = "wt_l1_smoke",
                type = GamepadButtonType.BUTTON_L1,
                label = "LB",
                subLabel = "Sol Omuz",
                xPercent = 0.07f,
                yPercent = 0.12f,
                sizeDp = 62f,
                opacity = 0.70f,
                colorHex = 0xFF9E9E9E,
                zone = ControlZone.LEFT
            ),
            GamepadControlItem(
                id = "wt_l2_zoom",
                type = GamepadButtonType.BUTTON_L2,
                label = "LT",
                subLabel = "Nişan / Zoom",
                xPercent = 0.20f,
                yPercent = 0.12f,
                sizeDp = 68f,
                opacity = 0.75f,
                colorHex = 0xFF2196F3,
                zone = ControlZone.LEFT
            ),

            // --- RIGHT ZONE (Firepower & Action Commands) ---
            GamepadControlItem(
                id = "wt_r2_fire",
                type = GamepadButtonType.BUTTON_R2,
                label = "RT",
                subLabel = "Ateş Et",
                xPercent = 0.90f,
                yPercent = 0.68f,
                sizeDp = 82f,
                opacity = 0.85f,
                colorHex = 0xFFF44336, // Combat Red
                zone = ControlZone.RIGHT
            ),
            GamepadControlItem(
                id = "wt_r1_mg",
                type = GamepadButtonType.BUTTON_R1,
                label = "RB",
                subLabel = "Sağ Omuz",
                xPercent = 0.90f,
                yPercent = 0.44f,
                sizeDp = 66f,
                opacity = 0.75f,
                colorHex = 0xFFFF5722,
                zone = ControlZone.RIGHT
            ),
            GamepadControlItem(
                id = "wt_btn_x_bino",
                type = GamepadButtonType.BUTTON_X,
                label = "X",
                subLabel = "Eylem X",
                xPercent = 0.78f,
                yPercent = 0.50f,
                sizeDp = 58f,
                opacity = 0.70f,
                colorHex = 0xFF29B6F6,
                zone = ControlZone.RIGHT
            ),
            GamepadControlItem(
                id = "wt_btn_y_lock",
                type = GamepadButtonType.BUTTON_Y,
                label = "Y",
                subLabel = "Eylem Y",
                xPercent = 0.80f,
                yPercent = 0.32f,
                sizeDp = 58f,
                opacity = 0.70f,
                colorHex = 0xFFFFCA28,
                zone = ControlZone.RIGHT
            ),
            GamepadControlItem(
                id = "wt_btn_a_repair",
                type = GamepadButtonType.BUTTON_A,
                label = "A",
                subLabel = "Seç / A",
                xPercent = 0.78f,
                yPercent = 0.75f,
                sizeDp = 58f,
                opacity = 0.70f,
                colorHex = 0xFF66BB6A,
                zone = ControlZone.RIGHT
            ),
            GamepadControlItem(
                id = "wt_btn_b_artillery",
                type = GamepadButtonType.BUTTON_B,
                label = "B",
                subLabel = "İptal / B",
                xPercent = 0.90f,
                yPercent = 0.88f,
                sizeDp = 58f,
                opacity = 0.70f,
                colorHex = 0xFFEF5350,
                zone = ControlZone.RIGHT
            ),

            // Center Top (Menu / Map / Stats)
            GamepadControlItem(
                id = "wt_select_map",
                type = GamepadButtonType.BUTTON_SELECT,
                label = "HARİTA",
                subLabel = "Harita / Select",
                xPercent = 0.44f,
                yPercent = 0.08f,
                sizeDp = 50f,
                opacity = 0.55f,
                colorHex = 0xFF78909C,
                zone = ControlZone.CENTER
            ),
            GamepadControlItem(
                id = "wt_start_menu",
                type = GamepadButtonType.BUTTON_START,
                label = "MENÜ",
                subLabel = "Start",
                xPercent = 0.56f,
                yPercent = 0.08f,
                sizeDp = 50f,
                opacity = 0.55f,
                colorHex = 0xFF78909C,
                zone = ControlZone.CENTER
            )
        )

        return GamepadProfile(
            id = 1L,
            name = "Oyun Kontrolü (Varsayılan)",
            description = "Mobil ve bulut oyunları için optimize edilmiş çift bölgeli dokunmatik kontrol düzeni.",
            isPreset = true,
            controls = controls,
            globalOpacity = 0.70f,
            hapticFeedback = true,
            leftStickDeadzone = 0.04f,
            leftStickSensitivity = 1.0f,
            rightStickDeadzone = 0.04f,
            rightStickSensitivity = 1.0f,
            centerAimFreeZone = true
        )
    }

    /**
     * Standard Xbox Style Gamepad profile with complete dual analog sticks,
     * diamond face buttons (A, B, X, Y), bumpers, triggers, and D-Pad.
     */
    fun createXboxStandardProfile(): GamepadProfile {
        val controls = listOf(
            // Left Stick & D-Pad
            GamepadControlItem(
                id = "xb_ls",
                type = GamepadButtonType.JOYSTICK_LEFT,
                label = "LS",
                subLabel = "Sol Analog",
                xPercent = 0.15f,
                yPercent = 0.42f,
                sizeDp = 135f,
                opacity = 0.70f,
                colorHex = 0xFF3DDC84,
                isVisible = true,
                zone = ControlZone.LEFT
            ),
            GamepadControlItem(
                id = "xb_dpad",
                type = GamepadButtonType.DPAD,
                label = "D-PAD",
                subLabel = "Yön Tuşları",
                xPercent = 0.26f,
                yPercent = 0.75f,
                sizeDp = 115f,
                opacity = 0.65f,
                colorHex = 0xFF9E9E9E,
                isVisible = true,
                zone = ControlZone.LEFT
            ),
            GamepadControlItem(
                id = "xb_lb",
                type = GamepadButtonType.BUTTON_L1,
                label = "LB",
                subLabel = "Sol Bumper",
                xPercent = 0.12f,
                yPercent = 0.14f,
                sizeDp = 65f,
                opacity = 0.75f,
                colorHex = 0xFF607D8B,
                isVisible = true,
                zone = ControlZone.LEFT
            ),
            GamepadControlItem(
                id = "xb_lt",
                type = GamepadButtonType.BUTTON_L2,
                label = "LT",
                subLabel = "Sol Tetik",
                xPercent = 0.24f,
                yPercent = 0.14f,
                sizeDp = 68f,
                opacity = 0.75f,
                colorHex = 0xFF455A64,
                isVisible = true,
                zone = ControlZone.LEFT
            ),
            GamepadControlItem(
                id = "xb_l3",
                type = GamepadButtonType.BUTTON_L3,
                label = "L3",
                subLabel = "Sol Tık",
                xPercent = 0.05f,
                yPercent = 0.42f,
                sizeDp = 48f,
                opacity = 0.60f,
                colorHex = 0xFF78909C,
                isVisible = true,
                zone = ControlZone.LEFT
            ),

            // Right Stick & Face Buttons (A, B, X, Y)
            GamepadControlItem(
                id = "xb_rs",
                type = GamepadButtonType.JOYSTICK_RIGHT,
                label = "RS",
                subLabel = "Sağ Analog",
                xPercent = 0.74f,
                yPercent = 0.72f,
                sizeDp = 135f,
                opacity = 0.70f,
                colorHex = 0xFF3DDC84,
                isVisible = true,
                zone = ControlZone.RIGHT
            ),
            GamepadControlItem(
                id = "xb_btn_y",
                type = GamepadButtonType.BUTTON_Y,
                label = "Y",
                subLabel = "Üst",
                xPercent = 0.85f,
                yPercent = 0.35f,
                sizeDp = 58f,
                opacity = 0.75f,
                colorHex = 0xFFFFCA28,
                isVisible = true,
                zone = ControlZone.RIGHT
            ),
            GamepadControlItem(
                id = "xb_btn_x",
                type = GamepadButtonType.BUTTON_X,
                label = "X",
                subLabel = "Sol",
                xPercent = 0.77f,
                yPercent = 0.45f,
                sizeDp = 58f,
                opacity = 0.75f,
                colorHex = 0xFF29B6F6,
                isVisible = true,
                zone = ControlZone.RIGHT
            ),
            GamepadControlItem(
                id = "xb_btn_b",
                type = GamepadButtonType.BUTTON_B,
                label = "B",
                subLabel = "Sağ",
                xPercent = 0.93f,
                yPercent = 0.45f,
                sizeDp = 58f,
                opacity = 0.75f,
                colorHex = 0xFFEF5350,
                isVisible = true,
                zone = ControlZone.RIGHT
            ),
            GamepadControlItem(
                id = "xb_btn_a",
                type = GamepadButtonType.BUTTON_A,
                label = "A",
                subLabel = "Alt",
                xPercent = 0.85f,
                yPercent = 0.55f,
                sizeDp = 58f,
                opacity = 0.75f,
                colorHex = 0xFF66BB6A,
                isVisible = true,
                zone = ControlZone.RIGHT
            ),
            GamepadControlItem(
                id = "xb_rb",
                type = GamepadButtonType.BUTTON_R1,
                label = "RB",
                subLabel = "Sağ Bumper",
                xPercent = 0.88f,
                yPercent = 0.14f,
                sizeDp = 65f,
                opacity = 0.75f,
                colorHex = 0xFF607D8B,
                isVisible = true,
                zone = ControlZone.RIGHT
            ),
            GamepadControlItem(
                id = "xb_rt",
                type = GamepadButtonType.BUTTON_R2,
                label = "RT",
                subLabel = "Sağ Tetik",
                xPercent = 0.76f,
                yPercent = 0.14f,
                sizeDp = 68f,
                opacity = 0.75f,
                colorHex = 0xFF455A64,
                isVisible = true,
                zone = ControlZone.RIGHT
            ),
            GamepadControlItem(
                id = "xb_r3",
                type = GamepadButtonType.BUTTON_R3,
                label = "R3",
                subLabel = "Sağ Tık",
                xPercent = 0.95f,
                yPercent = 0.72f,
                sizeDp = 48f,
                opacity = 0.60f,
                colorHex = 0xFF78909C,
                isVisible = true,
                zone = ControlZone.RIGHT
            ),

            // Center
            GamepadControlItem(
                id = "xb_back",
                type = GamepadButtonType.BUTTON_SELECT,
                label = "VIEW",
                subLabel = "Back",
                xPercent = 0.42f,
                yPercent = 0.16f,
                sizeDp = 50f,
                opacity = 0.60f,
                colorHex = 0xFF546E7A,
                isVisible = true,
                zone = ControlZone.CENTER
            ),
            GamepadControlItem(
                id = "xb_home",
                type = GamepadButtonType.BUTTON_HOME,
                label = "XBOX",
                subLabel = "Guide",
                xPercent = 0.50f,
                yPercent = 0.12f,
                sizeDp = 52f,
                opacity = 0.75f,
                colorHex = 0xFF2E7D32,
                isVisible = true,
                zone = ControlZone.CENTER
            ),
            GamepadControlItem(
                id = "xb_start",
                type = GamepadButtonType.BUTTON_START,
                label = "MENU",
                subLabel = "Start",
                xPercent = 0.58f,
                yPercent = 0.16f,
                sizeDp = 50f,
                opacity = 0.60f,
                colorHex = 0xFF546E7A,
                isVisible = true,
                zone = ControlZone.CENTER
            )
        )

        return GamepadProfile(
            id = 2L,
            name = "Xbox Standart Gamepad",
            description = "Eksiksiz klasik Xbox düzeni. Çift analog, tetikler ve tam D-Pad.",
            isPreset = true,
            controls = controls,
            globalOpacity = 0.70f,
            hapticFeedback = true,
            leftStickDeadzone = 0.15f,
            leftStickSensitivity = 1.0f,
            rightStickDeadzone = 0.15f,
            rightStickSensitivity = 1.0f,
            centerAimFreeZone = false
        )
    }

    /**
     * FPS & Fast Shooter profile with enlarged shoot/aim triggers and jump/crouch.
     */
    fun createFpsProfile(): GamepadProfile {
        val controls = listOf(
            GamepadControlItem(
                id = "fps_ls",
                type = GamepadButtonType.JOYSTICK_LEFT,
                label = "LS",
                subLabel = "Koşma",
                xPercent = 0.15f,
                yPercent = 0.70f,
                sizeDp = 140f,
                opacity = 0.65f,
                colorHex = 0xFF00E676,
                isVisible = true,
                zone = ControlZone.LEFT
            ),
            GamepadControlItem(
                id = "fps_lt",
                type = GamepadButtonType.BUTTON_L2,
                label = "LT",
                subLabel = "Dürbün Aç (ADS)",
                xPercent = 0.18f,
                yPercent = 0.25f,
                sizeDp = 76f,
                opacity = 0.80f,
                colorHex = 0xFF00B0FF,
                isVisible = true,
                zone = ControlZone.LEFT
            ),
            GamepadControlItem(
                id = "fps_rt",
                type = GamepadButtonType.BUTTON_R2,
                label = "RT",
                subLabel = "ATEŞ ET",
                xPercent = 0.86f,
                yPercent = 0.65f,
                sizeDp = 85f,
                opacity = 0.85f,
                colorHex = 0xFFFF1744,
                isVisible = true,
                zone = ControlZone.RIGHT
            ),
            GamepadControlItem(
                id = "fps_a",
                type = GamepadButtonType.BUTTON_A,
                label = "Zıpla",
                subLabel = "A",
                xPercent = 0.86f,
                yPercent = 0.42f,
                sizeDp = 64f,
                opacity = 0.75f,
                colorHex = 0xFFFFD600,
                isVisible = true,
                zone = ControlZone.RIGHT
            ),
            GamepadControlItem(
                id = "fps_b",
                type = GamepadButtonType.BUTTON_B,
                label = "Çömel",
                subLabel = "B",
                xPercent = 0.94f,
                yPercent = 0.52f,
                sizeDp = 60f,
                opacity = 0.70f,
                colorHex = 0xFFFF9100,
                isVisible = true,
                zone = ControlZone.RIGHT
            ),
            GamepadControlItem(
                id = "fps_x",
                type = GamepadButtonType.BUTTON_X,
                label = "Doldur",
                subLabel = "X",
                xPercent = 0.76f,
                yPercent = 0.52f,
                sizeDp = 60f,
                opacity = 0.70f,
                colorHex = 0xFF00E5FF,
                isVisible = true,
                zone = ControlZone.RIGHT
            ),
            GamepadControlItem(
                id = "fps_y",
                type = GamepadButtonType.BUTTON_Y,
                label = "Silah Değiş",
                subLabel = "Y",
                xPercent = 0.84f,
                yPercent = 0.28f,
                sizeDp = 60f,
                opacity = 0.70f,
                colorHex = 0xFFE040FB,
                isVisible = true,
                zone = ControlZone.RIGHT
            ),
            GamepadControlItem(
                id = "fps_l1",
                type = GamepadButtonType.BUTTON_L1,
                label = "Bomba",
                subLabel = "LB",
                xPercent = 0.08f,
                yPercent = 0.25f,
                sizeDp = 60f,
                opacity = 0.70f,
                colorHex = 0xFF76FF03,
                isVisible = true,
                zone = ControlZone.LEFT
            )
        )

        return GamepadProfile(
            id = 3L,
            name = "FPS / Nişancı Özel",
            description = "Birinci şahıs nişancı oyunları için geniş tetikler ve hızlı aksiyon tuşları.",
            isPreset = true,
            controls = controls,
            globalOpacity = 0.70f,
            hapticFeedback = true,
            centerAimFreeZone = true
        )
    }
}
