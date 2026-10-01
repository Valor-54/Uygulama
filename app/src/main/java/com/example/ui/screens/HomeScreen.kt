package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GamepadProfile
import com.example.model.InputMode
import com.example.ui.dialogs.JoystickSettingsDialog
import com.example.ui.theme.CombatRed
import com.example.ui.theme.ElectricGreen
import com.example.ui.theme.ScopeCyan
import com.example.ui.theme.StealthBackground
import com.example.ui.theme.StealthBorder
import com.example.ui.theme.StealthCard
import com.example.ui.theme.StealthSurface
import com.example.ui.theme.TacticalAmber
import com.example.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToEditor: (GamepadProfile) -> Unit,
    onNavigateToTechReport: () -> Unit,
    onNavigateToTester: () -> Unit
) {
    val context = LocalContext.current
    val profiles by viewModel.allProfiles.collectAsState()
    val selectedProfile by viewModel.selectedProfile.collectAsState()
    val isRunning by viewModel.isOverlayRunning.collectAsState()
    val canDrawOverlays by viewModel.canDrawOverlays.collectAsState()

    var showOverlayPermissionPrompt by remember { mutableStateOf(!canDrawOverlays) }
    var showJoystickDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.checkOverlayPermission()
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(StealthBackground)
    ) {
        val isLandscape = maxWidth > 560.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top App Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = TacticalAmber,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.SportsEsports,
                                contentDescription = "WarPad Pro",
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "WarPad Pro",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Text(
                            text = "GeForce NOW & Bulut Oyun Kontrolcüsü",
                            fontSize = 11.sp,
                            color = Color(0xFF8B949E)
                        )
                    }
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isRunning) ElectricGreen.copy(alpha = 0.2f) else Color(0xFF30363D),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isRunning) ElectricGreen else Color.Gray
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(if (isRunning) ElectricGreen else Color.Gray, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isRunning) "AKTİF" else "DURDURULDU",
                            color = if (isRunning) ElectricGreen else Color.Gray,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Overlay Permission Banner if not granted
            if (!canDrawOverlays) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF332005)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TacticalAmber),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = TacticalAmber)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Ekran Üzerinde Gösterme İzni Gerekli",
                                fontWeight = FontWeight.Bold,
                                color = TacticalAmber,
                                fontSize = 13.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "WarPad Pro'nun GeForce NOW ve oyun ekranlarının üzerinde çalışabilmesi için sistem ayarlarından 'Diğer uygulamaların üzerinde görüntüleme' iznini açmanız gerekir.",
                            color = Color(0xFFE0E0E0),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                val intent = Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:${context.packageName}")
                                )
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TacticalAmber),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("grant_overlay_permission_btn")
                        ) {
                            Text("İzin Ayarlarını Aç", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            if (isLandscape) {
                // Landscape Two-Column Adaptive Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left Column: Control Panel & GeForce NOW launcher
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        PrimaryHeroCard(
                            isRunning = isRunning,
                            canDrawOverlays = canDrawOverlays,
                            onResetDefaults = { viewModel.resetToDefaults() },
                            onStartControls = {
                                if (canDrawOverlays) {
                                    viewModel.startControls(context)
                                } else {
                                    val intent = Intent(
                                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:${context.packageName}")
                                    )
                                    context.startActivity(intent)
                                }
                            },
                            onStopControls = { viewModel.stopControls(context) },
                            onOpenEditor = { selectedProfile?.let { onNavigateToEditor(it) } }
                        )

                        GeForceNowCard(
                            isRunning = isRunning,
                            canDrawOverlays = canDrawOverlays,
                            onLaunch = {
                                if (!isRunning && canDrawOverlays) {
                                    viewModel.startControls(context)
                                }
                                viewModel.launchGeForceNow(context)
                            }
                        )
                    }

                    // Right Column: Quick Settings & Navigation
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        QuickSettingsCard(
                            profile = selectedProfile,
                            onOpacityChange = { viewModel.setGlobalOpacity(it) },
                            onSensitivityChange = { viewModel.setJoystickSensitivity(it) },
                            onDeadzoneChange = { viewModel.setJoystickDeadzone(it) },
                            onHapticChange = { viewModel.setHapticEnabled(it) },
                            onOpenAdvanced = { showJoystickDialog = true }
                        )

                        NavCardsRow(
                            onNavigateToTester = onNavigateToTester,
                            onNavigateToTechReport = onNavigateToTechReport
                        )
                    }
                }
            } else {
                // Portrait Single-Column
                PrimaryHeroCard(
                    isRunning = isRunning,
                    canDrawOverlays = canDrawOverlays,
                    onResetDefaults = { viewModel.resetToDefaults() },
                    onStartControls = {
                        if (canDrawOverlays) {
                            viewModel.startControls(context)
                        } else {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${context.packageName}")
                            )
                            context.startActivity(intent)
                        }
                    },
                    onStopControls = { viewModel.stopControls(context) },
                    onOpenEditor = { selectedProfile?.let { onNavigateToEditor(it) } }
                )

                GeForceNowCard(
                    isRunning = isRunning,
                    canDrawOverlays = canDrawOverlays,
                    onLaunch = {
                        if (!isRunning && canDrawOverlays) {
                            viewModel.startControls(context)
                        }
                        viewModel.launchGeForceNow(context)
                    }
                )

                QuickSettingsCard(
                    profile = selectedProfile,
                    onOpacityChange = { viewModel.setGlobalOpacity(it) },
                    onSensitivityChange = { viewModel.setJoystickSensitivity(it) },
                    onDeadzoneChange = { viewModel.setJoystickDeadzone(it) },
                    onHapticChange = { viewModel.setHapticEnabled(it) },
                    onOpenAdvanced = { showJoystickDialog = true }
                )

                NavCardsRow(
                    onNavigateToTester = onNavigateToTester,
                    onNavigateToTechReport = onNavigateToTechReport
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showJoystickDialog && selectedProfile != null) {
        JoystickSettingsDialog(
            profile = selectedProfile!!,
            onDismiss = { showJoystickDialog = false },
            onSaveSettings = { updated ->
                viewModel.updateSelectedProfile(updated)
            }
        )
    }
}

@Composable
private fun PrimaryHeroCard(
    isRunning: Boolean,
    canDrawOverlays: Boolean,
    onResetDefaults: () -> Unit,
    onStartControls: () -> Unit,
    onStopControls: () -> Unit,
    onOpenEditor: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = StealthCard)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "OYUN KONTROL PANELİ",
                    color = Color(0xFF8B949E),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Varsayılana Sıfırla",
                    color = TacticalAmber,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable { onResetDefaults() }
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Primary Start / Stop buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!isRunning) {
                    Button(
                        onClick = onStartControls,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("start_controls_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = TacticalAmber),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Kontrolleri Başlat",
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    Button(
                        onClick = onStopControls,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("stop_controls_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = CombatRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Kontrolleri Durdur",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }

                // Layout Editor Button
                OutlinedButton(
                    onClick = onOpenEditor,
                    modifier = Modifier
                        .height(52.dp)
                        .testTag("open_editor_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF484F58))
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = TacticalAmber)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Düzenle", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun GeForceNowCard(
    isRunning: Boolean,
    canDrawOverlays: Boolean,
    onLaunch: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onLaunch)
            .testTag("launch_geforce_now_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF132219)),
        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricGreen.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = ElectricGreen.copy(alpha = 0.2f),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.CloudQueue,
                        contentDescription = null,
                        tint = ElectricGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "GeForce NOW'u Aç & Oyna",
                    fontWeight = FontWeight.Bold,
                    color = ElectricGreen,
                    fontSize = 14.sp
                )
                Text(
                    "Kontroller arka planda başlar, doğrudan oyuna geçersiniz.",
                    color = Color(0xFFC9D1D9),
                    fontSize = 11.5.sp
                )
            }
        }
    }
}

@Composable
private fun QuickSettingsCard(
    profile: GamepadProfile?,
    onOpacityChange: (Float) -> Unit,
    onSensitivityChange: (Float) -> Unit,
    onDeadzoneChange: (Float) -> Unit,
    onHapticChange: (Boolean) -> Unit,
    onOpenAdvanced: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = StealthCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "HIZLI AYARLAR & HASSASİYET",
                color = Color(0xFF8B949E),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Global Opacity
            val curOpacity = profile?.globalOpacity ?: 0.70f
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Genel Şeffaflık", color = Color.White, fontSize = 13.sp)
                Text("${(curOpacity * 100).toInt()}%", color = TacticalAmber, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = curOpacity,
                onValueChange = onOpacityChange,
                valueRange = 0.15f..1.0f,
                colors = SliderDefaults.colors(thumbColor = TacticalAmber, activeTrackColor = TacticalAmber)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Joystick Sensitivity
            val curSens = profile?.leftStickSensitivity ?: 1.0f
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Joystick Hassasiyeti", color = Color.White, fontSize = 13.sp)
                Text("${String.format("%.1f", curSens)}x", color = ScopeCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = curSens,
                onValueChange = onSensitivityChange,
                valueRange = 0.5f..2.5f,
                colors = SliderDefaults.colors(thumbColor = ScopeCyan, activeTrackColor = ScopeCyan)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Joystick Deadzone
            val curDeadzone = profile?.leftStickDeadzone ?: 0.04f
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Ölü Bölge (Deadzone)", color = Color.White, fontSize = 13.sp)
                Text("${(curDeadzone * 100).toInt()}%", color = Color.Yellow, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = curDeadzone,
                onValueChange = onDeadzoneChange,
                valueRange = 0.01f..0.25f,
                colors = SliderDefaults.colors(thumbColor = Color.Yellow, activeTrackColor = Color.Yellow)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Haptic Feedback Switch
            val haptic = profile?.hapticFeedback ?: true
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Vibration, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Titreşim Geri Bildirimi", color = Color.White, fontSize = 13.sp)
                }
                Switch(
                    checked = haptic,
                    onCheckedChange = onHapticChange,
                    colors = SwitchDefaults.colors(checkedThumbColor = TacticalAmber, checkedTrackColor = TacticalAmber.copy(alpha = 0.5f))
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Advanced Joystick Settings Button
            OutlinedButton(
                onClick = onOpenAdvanced,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("home_advanced_joystick_btn"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TacticalAmber),
                border = androidx.compose.foundation.BorderStroke(1.dp, TacticalAmber.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gelişmiş Joystick Ayarları", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun NavCardsRow(
    onNavigateToTester: () -> Unit,
    onNavigateToTechReport: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Live Tester Card
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onNavigateToTester)
                .testTag("nav_tester_btn"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = StealthCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, StealthBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = ScopeCyan)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Giriş Test Cihazı", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                Text("Canlı tuş & analog telemetrisi", color = Color.Gray, fontSize = 11.sp)
            }
        }

        // Tech Report Card
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onNavigateToTechReport)
                .testTag("nav_tech_report_btn"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = StealthCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, StealthBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Icon(Icons.Default.Security, contentDescription = null, tint = TacticalAmber)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Teknik Rapor", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                Text("Android güvenlik analizi & HID", color = Color.Gray, fontSize = 11.sp)
            }
        }
    }
}
