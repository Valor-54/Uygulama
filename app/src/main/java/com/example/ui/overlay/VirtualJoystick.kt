package com.example.ui.overlay

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun VirtualJoystick(
    modifier: Modifier = Modifier,
    sizeDp: Float = 140f,
    label: String = "LS",
    color: Color = Color(0xFFFF9800),
    opacity: Float = 0.75f,
    sensitivity: Float = 1.0f,
    deadzone: Float = 0.08f,
    maxTravelFactor: Float = 0.72f,
    dynamicCenter: Boolean = false,
    isEditMode: Boolean = false,
    testTag: String = "gamepad_joystick",
    onMove: (x: Float, y: Float) -> Unit,
    onClickThumb: () -> Unit = {}
) {
    var knobOffset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .testTag(testTag)
            .pointerInput(isEditMode, sizeDp, sensitivity, deadzone, maxTravelFactor) {
                if (!isEditMode) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val pointerId = down.id
                        down.consume()
                        isDragging = true

                        val baseCenter = Offset(size.width / 2f, size.height / 2f)
                        val outerRadius = (size.width / 2f) - 3.dp.toPx()
                        val knobRadius = outerRadius * 0.28f
                        // Maximum knob center travel distance: reaches exactly the inside boundary of the outer circle without exceeding it
                        val maxTravel = outerRadius - knobRadius

                        fun updatePosition(pos: Offset) {
                            val delta = pos - baseCenter
                            val dist = delta.getDistance()

                            if (dist < 0.5f) {
                                knobOffset = Offset.Zero
                                onMove(0f, 0f)
                                return
                            }

                            // Keep knob strictly inside outer circle
                            val clampedDist = dist.coerceAtMost(maxTravel)
                            val angle = atan2(delta.y, delta.x)
                            val newKnob = Offset(
                                cos(angle) * clampedDist,
                                sin(angle) * clampedDist
                            )
                            knobOffset = newKnob

                            // Independent, stable, highly responsive X and Y axis calculations
                            // X: left (-1.0) to right (+1.0)
                            // Y: up (-1.0) to down (+1.0)
                            val rawX = (newKnob.x / maxTravel).coerceIn(-1f, 1f)
                            val rawY = (newKnob.y / maxTravel).coerceIn(-1f, 1f)

                            // Very low deadzone for high sensitivity (0.02f)
                            val effDeadzone = (deadzone * 0.4f).coerceIn(0.02f, 0.06f)
                            val effSensitivity = sensitivity.coerceIn(0.5f, 3.0f)

                            fun processAxis(raw: Float): Float {
                                val absVal = kotlin.math.abs(raw)
                                if (absVal < effDeadzone) return 0f
                                val sign = if (raw >= 0f) 1f else -1f
                                val scaled = ((absVal - effDeadzone) / (1f - effDeadzone)).coerceIn(0f, 1f) * effSensitivity
                                return (sign * scaled).coerceIn(-1f, 1f)
                            }

                            val finalX = processAxis(rawX)
                            val finalY = processAxis(rawY)
                            onMove(finalX, finalY)
                        }

                        // Immediate responsive registration on tap
                        updatePosition(down.position)

                        // Smooth fluid 360-degree tracking
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == pointerId }
                            if (change == null || !change.pressed) {
                                break
                            }
                            change.consume()
                            updatePosition(change.position)
                        }

                        // Finger lifted: cleanly and smoothly return to center (0, 0)
                        isDragging = false
                        knobOffset = Offset.Zero
                        onMove(0f, 0f)
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val baseCenter = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = size.width / 2f - 3.dp.toPx()

            // Outer Base Ring Background - COMPLETELY STATIC at baseCenter
            drawCircle(
                color = Color(0xFF0F1318).copy(alpha = (opacity * 0.60f).coerceIn(0f, 1f)),
                radius = outerRadius,
                center = baseCenter
            )

            // Outer Ring Border with Tactical Gradient - COMPLETELY STATIC at baseCenter
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = opacity),
                        color.copy(alpha = opacity * 0.35f)
                    ),
                    center = baseCenter,
                    radius = outerRadius
                ),
                radius = outerRadius,
                center = baseCenter,
                style = Stroke(width = if (isDragging) 3.dp.toPx() else 2.dp.toPx())
            )

            // Inner Deadzone Circle Indicator - COMPLETELY STATIC at baseCenter
            drawCircle(
                color = color.copy(alpha = opacity * 0.20f),
                radius = outerRadius * deadzone.coerceIn(0.04f, 0.25f),
                center = baseCenter,
                style = Stroke(width = 1.dp.toPx())
            )

            // Directional crosshair markers (Up, Down, Left, Right) - COMPLETELY STATIC at baseCenter
            val crossLength = 10.dp.toPx()
            val crossColor = color.copy(alpha = opacity * 0.70f)
            // Top
            drawLine(
                color = crossColor,
                start = Offset(baseCenter.x, baseCenter.y - outerRadius + 6.dp.toPx()),
                end = Offset(baseCenter.x, baseCenter.y - outerRadius + 6.dp.toPx() + crossLength),
                strokeWidth = 2.dp.toPx()
            )
            // Bottom
            drawLine(
                color = crossColor,
                start = Offset(baseCenter.x, baseCenter.y + outerRadius - 6.dp.toPx()),
                end = Offset(baseCenter.x, baseCenter.y + outerRadius - 6.dp.toPx() - crossLength),
                strokeWidth = 2.dp.toPx()
            )
            // Left
            drawLine(
                color = crossColor,
                start = Offset(baseCenter.x - outerRadius + 6.dp.toPx(), baseCenter.y),
                end = Offset(baseCenter.x - outerRadius + 6.dp.toPx() + crossLength, baseCenter.y),
                strokeWidth = 2.dp.toPx()
            )
            // Right
            drawLine(
                color = crossColor,
                start = Offset(baseCenter.x + outerRadius - 6.dp.toPx(), baseCenter.y),
                end = Offset(baseCenter.x + outerRadius - 6.dp.toPx() - crossLength, baseCenter.y),
                strokeWidth = 2.dp.toPx()
            )

            // Analog Knob (Küçük yuvarlak) - moves relative to baseCenter + knobOffset
            val knobCenter = baseCenter + knobOffset
            val knobRadius = outerRadius * 0.28f

            // Direction line connecting base to knob when deflected
            if (isDragging && knobOffset.getDistance() > 4.dp.toPx()) {
                drawLine(
                    color = color.copy(alpha = opacity * 0.4f),
                    start = baseCenter,
                    end = knobCenter,
                    strokeWidth = 3.dp.toPx()
                )
            }

            // Knob Base Shadow / Glow
            drawCircle(
                color = if (isDragging) color.copy(alpha = (opacity * 0.85f).coerceIn(0f, 1f)) else Color(0xFF21262D).copy(alpha = opacity * 0.80f),
                radius = knobRadius,
                center = knobCenter
            )

            // Knob Outer Border
            drawCircle(
                color = if (isDragging) Color.White else color.copy(alpha = opacity),
                radius = knobRadius,
                center = knobCenter,
                style = Stroke(width = if (isDragging) 2.5.dp.toPx() else 1.8.dp.toPx())
            )

            // Knob Inner Tactile Grip Ring
            drawCircle(
                color = Color.White.copy(alpha = if (isDragging) 0.65f else 0.25f),
                radius = knobRadius * 0.52f,
                center = knobCenter,
                style = Stroke(width = 1.5.dp.toPx())
            )
        }

        // Joystick Label smoothly follows the knob
        Text(
            text = label,
            modifier = Modifier.offset {
                IntOffset(
                    knobOffset.x.roundToInt(),
                    knobOffset.y.roundToInt()
                )
            },
            color = Color.White.copy(alpha = if (isDragging) 1.0f else opacity * 0.85f),
            fontSize = (sizeDp * 0.12f).coerceAtLeast(10f).sp,
            fontWeight = FontWeight.Black
        )
    }
}
