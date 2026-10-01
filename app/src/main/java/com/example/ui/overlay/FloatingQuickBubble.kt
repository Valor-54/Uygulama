package com.example.ui.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun FloatingQuickBubble(
    modifier: Modifier = Modifier,
    isEditMode: Boolean,
    isHudVisible: Boolean,
    onToggleEditMode: () -> Unit,
    onToggleHudVisibility: () -> Unit,
    onAddNewKey: () -> Unit = {},
    onOpenApp: () -> Unit,
    onStopService: () -> Unit,
    onDragDelta: (dx: Float, dy: Float) -> Unit = { _, _ -> }
) {
    var isExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Floating Bubble Icon
        Surface(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .clickable { isExpanded = !isExpanded }
                .testTag("floating_quick_bubble"),
            color = if (isEditMode) Color(0xFFFF9800) else Color(0xFF1E242B),
            shadowElevation = 6.dp,
            shape = CircleShape
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (isEditMode) Icons.Default.Edit else Icons.Default.Settings,
                    contentDescription = "WarPad Quick Settings",
                    tint = if (isEditMode) Color.Black else Color(0xFFFF9800),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Expanded Control Pill
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + slideInHorizontally(),
            exit = fadeOut() + slideOutHorizontally()
        ) {
            Surface(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .testTag("floating_quick_panel"),
                color = Color(0xEE12161A),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Lock / Edit Toggle
                    IconButton(
                        onClick = { onToggleEditMode() },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = if (isEditMode) Icons.Default.Lock else Icons.Default.Edit,
                            contentDescription = if (isEditMode) "Tuşları Kilitle" else "Düzenleme Modu",
                            tint = if (isEditMode) Color(0xFFFF9800) else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Add Key Button
                    IconButton(
                        onClick = { onAddNewKey() },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Yeni Tuş Ekle",
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Visibility Toggle
                    IconButton(
                        onClick = { onToggleHudVisibility() },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = if (isHudVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (isHudVisible) "Tuşları Gizle" else "Tuşları Göster",
                            tint = if (isHudVisible) Color(0xFF4CAF50) else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Open WarPad App
                    IconButton(
                        onClick = { onOpenApp() },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "Uygulamaya Dön",
                            tint = Color(0xFF64B5F6),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Stop Controls
                    IconButton(
                        onClick = { onStopService() },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kontrolleri Durdur",
                            tint = Color(0xFFE57373),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
