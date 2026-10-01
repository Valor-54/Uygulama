package com.example.model

/**
 * All supported gamepad buttons, triggers, joysticks and action shortcuts.
 */
enum class GamepadButtonType(
    val defaultLabel: String,
    val description: String,
    val isAnalog: Boolean = false
) {
    // Left & Right Joysticks
    JOYSTICK_LEFT("LS", "Sol Analog Çubuk (Hareket / Yön)", isAnalog = true),
    JOYSTICK_RIGHT("RS", "Sağ Analog Çubuk (Kamera / Nişan)", isAnalog = true),

    // Thumbstick clicks (L3 / R3)
    BUTTON_L3("L3", "Sol Çubuk Tıklama (L3)"),
    BUTTON_R3("R3", "Sağ Çubuk Tıklama (R3)"),

    // Face Buttons
    BUTTON_A("A", "A Butonu (Seç / Onayla / Zıpla)"),
    BUTTON_B("B", "B Butonu (Geri / İptal / Çömelme)"),
    BUTTON_X("X", "X Butonu (Kullan / Eylem X)"),
    BUTTON_Y("Y", "Y Butonu (Silah Değiştir / Eylem Y)"),

    // Shoulder Bumpers (L1, R1)
    BUTTON_L1("LB", "Sol Omuz Tuşu (LB / L1 - Sis / Telsiz)"),
    BUTTON_R1("RB", "Sağ Omuz Tuşu (RB / R1 - Makineli Tüfek)"),

    // Analog Triggers (L2, R2)
    BUTTON_L2("LT", "Sol Tetik (LT / L2 - Nişan / Yakınlaştır)", isAnalog = true),
    BUTTON_R2("RT", "Sağ Tetik (RT / R2 - Ana Top Ateşi)", isAnalog = true),

    // D-Pad (Directional Pad)
    DPAD("D-PAD", "Yön Tuşları (D-Pad)"),
    DPAD_UP("▲", "D-Pad Yukarı (Vites Yükselt)"),
    DPAD_DOWN("▼", "D-Pad Aşağı (Geri Vites)"),
    DPAD_LEFT("◄", "D-Pad Sol (Mühimmat 1)"),
    DPAD_RIGHT("►", "D-Pad Sağ (Mühimmat 2)"),

    // Navigation & System
    BUTTON_START("START", "Menü / Start"),
    BUTTON_SELECT("BACK", "Görünüm / Select"),
    BUTTON_HOME("GUIDE", "Xbox / Home Tuşu"),

    // Specialized Game Shortcuts
    WT_MAIN_GUN("Ateş", "Oyun: Ana Ateş / Eylem (RT)"),
    WT_ZOOM("Dürbün", "Oyun: Nişangah / Yakınlaştırma (LT)"),
    WT_BINOCULAR("Gözlem", "Oyun: Serbest Kamera / Bakış (X)"),
    WT_SMOKE("Sis", "Oyun: Sis / Savunma (L1)"),
    WT_REPAIR("Tamir", "Oyun: Aksiyon / Tamir (A)"),
    WT_LOCK_TARGET("Kilit", "Oyun: Hedef Takibi / Mesafe (Y)"),
    WT_ARTILLERY("Topçu", "Oyun: Destek / Yetenek (B)")
}
