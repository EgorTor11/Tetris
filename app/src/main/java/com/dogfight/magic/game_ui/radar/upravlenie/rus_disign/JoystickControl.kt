package com.dogfight.magic.game_ui.radar.upravlenie.rus_disign

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dogfight.magic.R
import com.dogfight.magic.game_ui.radar.mvi_radar.RadarViewModel
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun JoystickControl(
    viewModel: RadarViewModel,
    joystickPainter: Painter? = painterResource(id = R.drawable.img_joystick_black), // Передаем изображение, если оно есть
    imageModifier: Modifier = Modifier,
    modifier: Modifier = Modifier,
    onClick:()->Unit
) {
    val screenState by viewModel.screenState.collectAsState()
    val scope = rememberCoroutineScope()
    var tiltAngle by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    LaunchedEffect(screenState.rollAngle) {
        // Синхронизация локального угла с глобальным
        if (!isDragging) {
            tiltAngle = screenState.rollAngle / 1.5f
        }
    }

    Box(modifier = modifier.size(200.dp, 140.dp).clickable{onClick()}) {
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            isDragging = true
                            viewModel.updateDragStateFromJoystick(isDragging)
                        },
                        onDragEnd = {
                            isDragging = false
                            viewModel.updateDragStateFromJoystick(isDragging)
                            scope.launch {
                                tiltAngle = 0f
                                viewModel.updateRollAngle(0f)
                            }
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        val sensitivity = 0.2f
                        tiltAngle =
                            (tiltAngle + dragAmount.x * sensitivity).coerceIn(
                                -55f,
                                55f
                            )
                        viewModel.updateRollAngle(tiltAngle)
                    }
                }
        ) {
            val centerX = size.width / 2
            val bottomY = size.height
            val stickHeight = size.height * 0.85f
            val baseWidth = size.width * 0.6f
            val baseHeight = size.height * 0.15f

            val angleRad = Math.toRadians(tiltAngle.toDouble()).toFloat()
            val topX = centerX + stickHeight * sin(angleRad)
            val topY = bottomY - stickHeight * cos(angleRad)

            // Если изображение передано — используем его
            if (joystickPainter == null) {
                // Основание
                drawRoundRect(
                    color = Color.DarkGray,
                    size = Size(baseWidth, baseHeight),
                    cornerRadius = CornerRadius(10f, 10f),
                    topLeft = Offset(centerX - baseWidth / 2, bottomY - baseHeight)
                )

                drawCircle(Color.Black, radius = 6f, center = Offset(centerX - 25f, bottomY - 10f))
                drawCircle(Color.Black, radius = 6f, center = Offset(centerX + 25f, bottomY - 10f))

                drawPath(
                    path = Path().apply {
                        moveTo(centerX - 20f, bottomY - baseHeight)
                        lineTo(centerX + 20f, bottomY - baseHeight)
                        lineTo(topX + 18f, topY)
                        lineTo(topX - 18f, topY)
                        close()
                    },
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Gray, Color.DarkGray, Color.Black),
                        startY = bottomY - baseHeight,
                        endY = topY
                    )
                )

                drawCircle(
                    Color.Black.copy(alpha = 0.15f),
                    radius = 12f,
                    center = Offset(topX, topY + 6f)
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.Red, Color.Red),
                        center = Offset(topX - 10f, topY + 5f),
                        radius = 9f
                    ),
                    radius = 9f,
                    center = Offset(topX - 10f, topY + 5f)
                )
            }
        }

        // Если изображение передано, рендерим его отдельно с поворотом
        if (joystickPainter != null) {
            Image(
                painter = joystickPainter,
                contentDescription = "Joystick",
                modifier = imageModifier
                    .size(120.dp)
                    .align(androidx.compose.ui.Alignment.BottomCenter)
                    .graphicsLayer(
                        rotationZ = tiltAngle, // Применяем поворот
                        transformOrigin = androidx.compose.ui.graphics.TransformOrigin(
                            0.5f,
                            1f
                        ) // Устанавливаем точку привязки на нижнюю часть
                    )
            )
        }
    }
}
