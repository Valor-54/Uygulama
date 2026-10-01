package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.input.GamepadInputManager
import com.example.model.GamepadButtonType
import com.example.ui.overlay.VirtualButton
import com.example.ui.overlay.VirtualDpad
import com.example.ui.overlay.VirtualJoystick
import com.example.ui.overlay.VirtualTrigger
import com.example.ui.theme.CombatRed
import com.example.ui.theme.ElectricGreen
import com.example.ui.theme.ScopeCyan
import com.example.ui.theme.StealthBackground
import com.example.ui.theme.StealthCard
import com.example.ui.theme.TacticalAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamepadTesterScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val inputManager = remember { GamepadInputManager(context) }
    val state by inputManager.currentState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Canlı Gamepad Giriş Test Cihazı", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("tester_back_btn")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = StealthBackground,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = StealthBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Status Monitor Box
            Card(
                colors = CardDefaults.cardColors(containerColor = StealthCard),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Anlık Giriş Telemetrisi (XInput / HID)",
                        fontWeight = FontWeight.Bold,
                        color = TacticalAmber,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Sol Çubuk (LS):", color = Color.Gray, fontSize = 12.sp)
                            Text(
                                "X: ${String.format("%.2f", state.leftStickX)} | Y: ${String.format("%.2f", state.leftStickY)}",
                                color = if (state.isLeftStickActive) ElectricGreen else Color.White,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column {
                            Text("Sağ Çubuk (RS):", color = Color.Gray, fontSize = 12.sp)
                            Text(
                                "X: ${String.format("%.2f", state.rightStickX)} | Y: ${String.format("%.2f", state.rightStickY)}",
                                color = if (state.isRightStickActive) ElectricGreen else Color.White,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("LT Sol Tetik:", color = Color.Gray, fontSize = 12.sp)
                            Text(
                                "${(state.l2Trigger * 100).toInt()}%",
                                color = if (state.l2Trigger > 0.05f) ScopeCyan else Color.White,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column {
                            Text("RT Sağ Tetik:", color = Color.Gray, fontSize = 12.sp)
                            Text(
                                "${(state.r2Trigger * 100).toInt()}%",
                                color = if (state.r2Trigger > 0.05f) CombatRed else Color.White,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column {
                            Text("Aktif Tuş Sayısı:", color = Color.Gray, fontSize = 12.sp)
                            Text(
                                "${state.pressedButtons.size}",
                                color = if (state.pressedButtons.isNotEmpty()) TacticalAmber else Color.White,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (state.pressedButtons.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            for (btn in state.pressedButtons) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = TacticalAmber.copy(alpha = 0.25f)
                                ) {
                                    Text(
                                        text = btn.defaultLabel,
                                        color = TacticalAmber,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Interactive Playground
            Text(
                "Aşağıdaki Butonlara Dokunarak Canlı Test Edin:",
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                fontSize = 13.sp
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14181E)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Top Bumpers & Triggers Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        VirtualTrigger(
                            sizeDp = 64f,
                            label = "LT",
                            subLabel = "Sol Tetik",
                            color = ScopeCyan,
                            testTag = "test_trigger_lt",
                            onTriggerChanged = { p -> inputManager.updateTrigger(true, p) }
                        )

                        VirtualButton(
                            sizeDp = 54f,
                            label = "LB",
                            subLabel = "Bumper",
                            color = Color(0xFF78909C),
                            testTag = "test_btn_lb",
                            onPressChanged = { pr -> inputManager.setButtonPressed(GamepadButtonType.BUTTON_L1, pr) }
                        )

                        VirtualButton(
                            sizeDp = 54f,
                            label = "RB",
                            subLabel = "Bumper",
                            color = Color(0xFF78909C),
                            testTag = "test_btn_rb",
                            onPressChanged = { pr -> inputManager.setButtonPressed(GamepadButtonType.BUTTON_R1, pr) }
                        )

                        VirtualTrigger(
                            sizeDp = 64f,
                            label = "RT",
                            subLabel = "Sağ Tetik",
                            color = CombatRed,
                            testTag = "test_trigger_rt",
                            onTriggerChanged = { p -> inputManager.updateTrigger(false, p) }
                        )
                    }

                    // Main Controllers Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Stick & D-Pad
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            VirtualJoystick(
                                sizeDp = 120f,
                                label = "LS",
                                color = TacticalAmber,
                                onMove = { x, y -> inputManager.updateJoystick(true, x, y) },
                                onClickThumb = {
                                    inputManager.setButtonPressed(GamepadButtonType.BUTTON_L3, true)
                                    inputManager.setButtonPressed(GamepadButtonType.BUTTON_L3, false)
                                }
                            )

                            VirtualDpad(
                                sizeDp = 100f,
                                onDirectionChanged = { dir, pr -> inputManager.setButtonPressed(dir, pr) }
                            )
                        }

                        // Face Buttons (A, B, X, Y)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            VirtualButton(
                                sizeDp = 52f,
                                label = "Y",
                                subLabel = "Sarı",
                                color = Color(0xFFFFCA28),
                                onPressChanged = { pr -> inputManager.setButtonPressed(GamepadButtonType.BUTTON_Y, pr) }
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                VirtualButton(
                                    sizeDp = 52f,
                                    label = "X",
                                    subLabel = "Mavi",
                                    color = Color(0xFF29B6F6),
                                    onPressChanged = { pr -> inputManager.setButtonPressed(GamepadButtonType.BUTTON_X, pr) }
                                )
                                VirtualButton(
                                    sizeDp = 52f,
                                    label = "B",
                                    subLabel = "Kırmızı",
                                    color = CombatRed,
                                    onPressChanged = { pr -> inputManager.setButtonPressed(GamepadButtonType.BUTTON_B, pr) }
                                )
                            }

                            VirtualButton(
                                sizeDp = 52f,
                                label = "A",
                                subLabel = "Yeşil",
                                color = ElectricGreen,
                                onPressChanged = { pr -> inputManager.setButtonPressed(GamepadButtonType.BUTTON_A, pr) }
                            )
                        }
                    }
                }
            }
        }
    }
}
