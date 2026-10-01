package com.example.model

import android.view.KeyEvent

enum class KeyCategory(val title: String) {
    GAMEPAD("Gamepad Tuşları"),
    KEYBOARD_BASIC("Klavye Temel"),
    KEYBOARD_LETTERS("Harfler (A-Z, WASD)"),
    KEYBOARD_NUMBERS("Sayılar & Fonksiyon (F1-F12)"),
    KEYBOARD_ARROWS("Yön Tuşları & Numpad"),
    GAME_ACTIONS("Oyun Aksiyonları"),
    CUSTOM("Özel Tuş")
}

data class InputKeyDefinition(
    val id: String,
    val label: String,
    val subLabel: String = "",
    val category: KeyCategory,
    val androidKeycode: Int = 0,
    val hidUsageId: Int = 0,
    val isGamepadInput: Boolean = false,
    val gamepadButtonType: GamepadButtonType? = null
)

object KeyRegistry {

    val ALL_KEYS: List<InputKeyDefinition> = buildList {
        // --- GAMEPAD KEYS ---
        add(InputKeyDefinition("GP_A", "A", "Alt Tuş / Seç", KeyCategory.GAMEPAD, KeyEvent.KEYCODE_BUTTON_A, 1, true, GamepadButtonType.BUTTON_A))
        add(InputKeyDefinition("GP_B", "B", "Sağ Tuş / İptal", KeyCategory.GAMEPAD, KeyEvent.KEYCODE_BUTTON_B, 2, true, GamepadButtonType.BUTTON_B))
        add(InputKeyDefinition("GP_X", "X", "Sol Tuş / Doldur", KeyCategory.GAMEPAD, KeyEvent.KEYCODE_BUTTON_X, 3, true, GamepadButtonType.BUTTON_X))
        add(InputKeyDefinition("GP_Y", "Y", "Üst Tuş / Silah Değiş", KeyCategory.GAMEPAD, KeyEvent.KEYCODE_BUTTON_Y, 4, true, GamepadButtonType.BUTTON_Y))
        add(InputKeyDefinition("GP_LB", "LB", "Sol Omuz (L1)", KeyCategory.GAMEPAD, KeyEvent.KEYCODE_BUTTON_L1, 5, true, GamepadButtonType.BUTTON_L1))
        add(InputKeyDefinition("GP_RB", "RB", "Sağ Omuz (R1)", KeyCategory.GAMEPAD, KeyEvent.KEYCODE_BUTTON_R1, 6, true, GamepadButtonType.BUTTON_R1))
        add(InputKeyDefinition("GP_LT", "LT", "Sol Tetik (L2)", KeyCategory.GAMEPAD, KeyEvent.KEYCODE_BUTTON_L2, 7, true, GamepadButtonType.BUTTON_L2))
        add(InputKeyDefinition("GP_RT", "RT", "Sağ Tetik (R2)", KeyCategory.GAMEPAD, KeyEvent.KEYCODE_BUTTON_R2, 8, true, GamepadButtonType.BUTTON_R2))
        add(InputKeyDefinition("GP_L3", "L3", "Sol Çubuk Tık", KeyCategory.GAMEPAD, KeyEvent.KEYCODE_BUTTON_THUMBL, 9, true, GamepadButtonType.BUTTON_L3))
        add(InputKeyDefinition("GP_R3", "R3", "Sağ Çubuk Tık", KeyCategory.GAMEPAD, KeyEvent.KEYCODE_BUTTON_THUMBR, 10, true, GamepadButtonType.BUTTON_R3))
        add(InputKeyDefinition("GP_DPAD_UP", "▲", "D-Pad Yukarı", KeyCategory.GAMEPAD, KeyEvent.KEYCODE_DPAD_UP, 11, true, GamepadButtonType.DPAD_UP))
        add(InputKeyDefinition("GP_DPAD_DOWN", "▼", "D-Pad Aşağı", KeyCategory.GAMEPAD, KeyEvent.KEYCODE_DPAD_DOWN, 12, true, GamepadButtonType.DPAD_DOWN))
        add(InputKeyDefinition("GP_DPAD_LEFT", "◄", "D-Pad Sol", KeyCategory.GAMEPAD, KeyEvent.KEYCODE_DPAD_LEFT, 13, true, GamepadButtonType.DPAD_LEFT))
        add(InputKeyDefinition("GP_DPAD_RIGHT", "►", "D-Pad Sağ", KeyCategory.GAMEPAD, KeyEvent.KEYCODE_DPAD_RIGHT, 14, true, GamepadButtonType.DPAD_RIGHT))
        add(InputKeyDefinition("GP_DPAD", "D-PAD", "4 Yönlü D-Pad Pedi", KeyCategory.GAMEPAD, 0, 0, true, GamepadButtonType.DPAD))
        add(InputKeyDefinition("GP_START", "START", "Menü", KeyCategory.GAMEPAD, KeyEvent.KEYCODE_BUTTON_START, 15, true, GamepadButtonType.BUTTON_START))
        add(InputKeyDefinition("GP_SELECT", "BACK", "Görünüm / Select", KeyCategory.GAMEPAD, KeyEvent.KEYCODE_BUTTON_SELECT, 16, true, GamepadButtonType.BUTTON_SELECT))
        add(InputKeyDefinition("GP_HOME", "HOME", "Xbox Guide", KeyCategory.GAMEPAD, KeyEvent.KEYCODE_BUTTON_MODE, 17, true, GamepadButtonType.BUTTON_HOME))
        add(InputKeyDefinition("GP_JOYSTICK_LEFT", "LS", "Sol Analog Joystick", KeyCategory.GAMEPAD, 0, 0, true, GamepadButtonType.JOYSTICK_LEFT))
        add(InputKeyDefinition("GP_JOYSTICK_RIGHT", "RS", "Sağ Analog Joystick", KeyCategory.GAMEPAD, 0, 0, true, GamepadButtonType.JOYSTICK_RIGHT))

        // --- KEYBOARD BASIC ---
        add(InputKeyDefinition("KB_SPACE", "SPACE", "Boşluk / Zıpla", KeyCategory.KEYBOARD_BASIC, KeyEvent.KEYCODE_SPACE, 0x2C))
        add(InputKeyDefinition("KB_ENTER", "ENTER", "Giriş / Onayla", KeyCategory.KEYBOARD_BASIC, KeyEvent.KEYCODE_ENTER, 0x28))
        add(InputKeyDefinition("KB_SHIFT", "SHIFT", "Koşma / Yavaşla", KeyCategory.KEYBOARD_BASIC, KeyEvent.KEYCODE_SHIFT_LEFT, 0xE1))
        add(InputKeyDefinition("KB_CTRL", "CTRL", "Çömelme / Kontrol", KeyCategory.KEYBOARD_BASIC, KeyEvent.KEYCODE_CTRL_LEFT, 0xE0))
        add(InputKeyDefinition("KB_ALT", "ALT", "Serbest Bakış", KeyCategory.KEYBOARD_BASIC, KeyEvent.KEYCODE_ALT_LEFT, 0xE2))
        add(InputKeyDefinition("KB_TAB", "TAB", "Skor Tablosu", KeyCategory.KEYBOARD_BASIC, KeyEvent.KEYCODE_TAB, 0x2B))
        add(InputKeyDefinition("KB_ESC", "ESC", "Çıkış / Menü", KeyCategory.KEYBOARD_BASIC, KeyEvent.KEYCODE_ESCAPE, 0x29))
        add(InputKeyDefinition("KB_BACKSPACE", "BACKSPACE", "Geri Silme", KeyCategory.KEYBOARD_BASIC, KeyEvent.KEYCODE_DEL, 0x2A))
        add(InputKeyDefinition("KB_DELETE", "DELETE", "Sil", KeyCategory.KEYBOARD_BASIC, KeyEvent.KEYCODE_FORWARD_DEL, 0x4C))

        // --- KEYBOARD LETTERS (A-Z & WASD) ---
        add(InputKeyDefinition("KB_W", "W", "İleri (Gaz)", KeyCategory.KEYBOARD_LETTERS, KeyEvent.KEYCODE_W, 0x1A))
        add(InputKeyDefinition("KB_A", "A", "Sol Manevra", KeyCategory.KEYBOARD_LETTERS, KeyEvent.KEYCODE_A, 0x04))
        add(InputKeyDefinition("KB_S", "S", "Geri (Fren)", KeyCategory.KEYBOARD_LETTERS, KeyEvent.KEYCODE_S, 0x16))
        add(InputKeyDefinition("KB_D", "D", "Sağ Manevra", KeyCategory.KEYBOARD_LETTERS, KeyEvent.KEYCODE_D, 0x07))
        add(InputKeyDefinition("KB_Q", "Q", "Eğim Sol", KeyCategory.KEYBOARD_LETTERS, KeyEvent.KEYCODE_Q, 0x14))
        add(InputKeyDefinition("KB_E", "E", "Eğim Sağ / Kullan", KeyCategory.KEYBOARD_LETTERS, KeyEvent.KEYCODE_E, 0x08))
        add(InputKeyDefinition("KB_R", "R", "Doldur (Reload)", KeyCategory.KEYBOARD_LETTERS, KeyEvent.KEYCODE_R, 0x15))
        add(InputKeyDefinition("KB_F", "F", "Kapakçık / Flaps", KeyCategory.KEYBOARD_LETTERS, KeyEvent.KEYCODE_F, 0x09))
        add(InputKeyDefinition("KB_C", "C", "Serbest Kamera", KeyCategory.KEYBOARD_LETTERS, KeyEvent.KEYCODE_C, 0x06))
        add(InputKeyDefinition("KB_V", "V", "Kamera Görünümü", KeyCategory.KEYBOARD_LETTERS, KeyEvent.KEYCODE_V, 0x19))
        add(InputKeyDefinition("KB_Z", "Z", "Dürbün / Zoom", KeyCategory.KEYBOARD_LETTERS, KeyEvent.KEYCODE_Z, 0x1D))
        add(InputKeyDefinition("KB_X", "X", "Röntgen / X-Ray", KeyCategory.KEYBOARD_LETTERS, KeyEvent.KEYCODE_X, 0x1B))
        add(InputKeyDefinition("KB_G", "G", "Sis / İniş Takımı", KeyCategory.KEYBOARD_LETTERS, KeyEvent.KEYCODE_G, 0x0A))
        add(InputKeyDefinition("KB_B", "B", "Hava Freni", KeyCategory.KEYBOARD_LETTERS, KeyEvent.KEYCODE_B, 0x05))
        add(InputKeyDefinition("KB_M", "M", "Harita", KeyCategory.KEYBOARD_LETTERS, KeyEvent.KEYCODE_M, 0x10))
        add(InputKeyDefinition("KB_T", "T", "Telsiz Komutu", KeyCategory.KEYBOARD_LETTERS, KeyEvent.KEYCODE_T, 0x17))
        add(InputKeyDefinition("KB_Y", "Y", "Sohbet", KeyCategory.KEYBOARD_LETTERS, KeyEvent.KEYCODE_Y, 0x1C))
        add(InputKeyDefinition("KB_U", "U", "Bomba Görünümü", KeyCategory.KEYBOARD_LETTERS, KeyEvent.KEYCODE_U, 0x18))
        add(InputKeyDefinition("KB_I", "I", "Motor Aç/Kapa", KeyCategory.KEYBOARD_LETTERS, KeyEvent.KEYCODE_I, 0x0C))
        add(InputKeyDefinition("KB_O", "O", "Hedef Takibi", KeyCategory.KEYBOARD_LETTERS, KeyEvent.KEYCODE_O, 0x12))
        add(InputKeyDefinition("KB_P", "P", "Fren / Park", KeyCategory.KEYBOARD_LETTERS, KeyEvent.KEYCODE_P, 0x13))
        add(InputKeyDefinition("KB_H", "H", "Korna / Far", KeyCategory.KEYBOARD_LETTERS, KeyEvent.KEYCODE_H, 0x0B))
        add(InputKeyDefinition("KB_J", "J", "Araçtan Çıkış", KeyCategory.KEYBOARD_LETTERS, KeyEvent.KEYCODE_J, 0x0D))
        add(InputKeyDefinition("KB_K", "K", "Menzil Ölçer", KeyCategory.KEYBOARD_LETTERS, KeyEvent.KEYCODE_K, 0x0E))
        add(InputKeyDefinition("KB_L", "L", "Gece Görüşü / NVD", KeyCategory.KEYBOARD_LETTERS, KeyEvent.KEYCODE_L, 0x0F))
        add(InputKeyDefinition("KB_N", "N", "Radar / Arama", KeyCategory.KEYBOARD_LETTERS, KeyEvent.KEYCODE_N, 0x11))

        // --- KEYBOARD NUMBERS & F1-F12 ---
        add(InputKeyDefinition("KB_1", "1", "Ana Silah / APCR", KeyCategory.KEYBOARD_NUMBERS, KeyEvent.KEYCODE_1, 0x1E))
        add(InputKeyDefinition("KB_2", "2", "İkincil Silah / HE", KeyCategory.KEYBOARD_NUMBERS, KeyEvent.KEYCODE_2, 0x1F))
        add(InputKeyDefinition("KB_3", "3", "Makineli Tüfek / APDS", KeyCategory.KEYBOARD_NUMBERS, KeyEvent.KEYCODE_3, 0x20))
        add(InputKeyDefinition("KB_4", "4", "Sis Bombası", KeyCategory.KEYBOARD_NUMBERS, KeyEvent.KEYCODE_4, 0x21))
        add(InputKeyDefinition("KB_5", "5", "Topçu Desteği", KeyCategory.KEYBOARD_NUMBERS, KeyEvent.KEYCODE_5, 0x22))
        add(InputKeyDefinition("KB_6", "6", "Yangın Söndürücü", KeyCategory.KEYBOARD_NUMBERS, KeyEvent.KEYCODE_6, 0x23))
        add(InputKeyDefinition("KB_7", "7", "Palet Tamiri", KeyCategory.KEYBOARD_NUMBERS, KeyEvent.KEYCODE_7, 0x24))
        add(InputKeyDefinition("KB_8", "8", "Yardım İste", KeyCategory.KEYBOARD_NUMBERS, KeyEvent.KEYCODE_8, 0x25))
        add(InputKeyDefinition("KB_9", "9", "Çekme Halatı", KeyCategory.KEYBOARD_NUMBERS, KeyEvent.KEYCODE_9, 0x26))
        add(InputKeyDefinition("KB_0", "0", "Menzil Ayarı", KeyCategory.KEYBOARD_NUMBERS, KeyEvent.KEYCODE_0, 0x27))

        // F1-F12
        for (i in 1..12) {
            val keycode = KeyEvent.KEYCODE_F1 + (i - 1)
            val hid = 0x3A + (i - 1)
            add(InputKeyDefinition("KB_F$i", "F$i", "Fonksiyon Tuşu $i", KeyCategory.KEYBOARD_NUMBERS, keycode, hid))
        }

        // --- ARROWS & NUMPAD ---
        add(InputKeyDefinition("KB_ARROW_UP", "↑", "Yukarı Ok", KeyCategory.KEYBOARD_ARROWS, KeyEvent.KEYCODE_DPAD_UP, 0x52))
        add(InputKeyDefinition("KB_ARROW_DOWN", "↓", "Aşağı Ok", KeyCategory.KEYBOARD_ARROWS, KeyEvent.KEYCODE_DPAD_DOWN, 0x51))
        add(InputKeyDefinition("KB_ARROW_LEFT", "←", "Sol Ok", KeyCategory.KEYBOARD_ARROWS, KeyEvent.KEYCODE_DPAD_LEFT, 0x50))
        add(InputKeyDefinition("KB_ARROW_RIGHT", "→", "Sağ Ok", KeyCategory.KEYBOARD_ARROWS, KeyEvent.KEYCODE_DPAD_RIGHT, 0x4F))
        for (i in 0..9) {
            val keycode = KeyEvent.KEYCODE_NUMPAD_0 + i
            val hid = if (i == 0) 0x62 else (0x59 + (i - 1))
            add(InputKeyDefinition("KB_NUM_$i", "Num $i", "Numpad $i", KeyCategory.KEYBOARD_ARROWS, keycode, hid))
        }
        add(InputKeyDefinition("KB_NUM_ENTER", "Num Enter", "Numpad Giriş", KeyCategory.KEYBOARD_ARROWS, KeyEvent.KEYCODE_NUMPAD_ENTER, 0x58))
        add(InputKeyDefinition("KB_NUM_PLUS", "Num +", "Numpad Artı", KeyCategory.KEYBOARD_ARROWS, KeyEvent.KEYCODE_NUMPAD_ADD, 0x57))
        add(InputKeyDefinition("KB_NUM_MINUS", "Num -", "Numpad Eksi", KeyCategory.KEYBOARD_ARROWS, KeyEvent.KEYCODE_NUMPAD_SUBTRACT, 0x56))

        // --- GAME COMBAT & TACTICAL SHORTCUTS ---
        add(InputKeyDefinition("WT_FIRE", "ATEŞ", "Oyun: Ana Ateş / Eylem", KeyCategory.GAME_ACTIONS, KeyEvent.KEYCODE_BUTTON_R2, 8, true, GamepadButtonType.BUTTON_R2))
        add(InputKeyDefinition("WT_ZOOM", "DÜRBÜN", "Oyun: Nişangah / Yakınlaştır", KeyCategory.GAME_ACTIONS, KeyEvent.KEYCODE_BUTTON_L2, 7, true, GamepadButtonType.BUTTON_L2))
        add(InputKeyDefinition("WT_BINOCULAR", "GÖZLEM", "Oyun: Serbest Kamera / Bakış", KeyCategory.GAME_ACTIONS, KeyEvent.KEYCODE_BUTTON_X, 3, true, GamepadButtonType.BUTTON_X))
        add(InputKeyDefinition("WT_SMOKE", "SİS", "Oyun: Sis / Savunma", KeyCategory.GAME_ACTIONS, KeyEvent.KEYCODE_BUTTON_L1, 5, true, GamepadButtonType.BUTTON_L1))
        add(InputKeyDefinition("WT_REPAIR", "TAMİR", "Oyun: Aksiyon / Tamir", KeyCategory.GAME_ACTIONS, KeyEvent.KEYCODE_BUTTON_A, 1, true, GamepadButtonType.BUTTON_A))
        add(InputKeyDefinition("WT_LOCK", "KİLİT", "Oyun: Hedef Kilidi / Takip", KeyCategory.GAME_ACTIONS, KeyEvent.KEYCODE_BUTTON_Y, 4, true, GamepadButtonType.BUTTON_Y))
        add(InputKeyDefinition("WT_ARTILLERY", "TOPÇU", "Oyun: Destek / Yetenek", KeyCategory.GAME_ACTIONS, KeyEvent.KEYCODE_BUTTON_B, 2, true, GamepadButtonType.BUTTON_B))
    }

    fun findById(id: String): InputKeyDefinition? = ALL_KEYS.firstOrNull { it.id == id }

    fun findByGamepadType(type: GamepadButtonType): InputKeyDefinition? = ALL_KEYS.firstOrNull { it.gamepadButtonType == type }
}
