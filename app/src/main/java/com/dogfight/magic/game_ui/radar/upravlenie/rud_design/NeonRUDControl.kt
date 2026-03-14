package com.dogfight.magic.game_ui.radar.upravlenie.rud_design

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dogfight.magic.game_ui.radar.mvi_radar.RadarViewModel

const val height = 150

@Composable
fun NeonRUDControl(
    viewModel: RadarViewModel,
    modifier: Modifier = Modifier,
    onThrottleChange: (Float) -> Unit,
    scaleImage: Painter? = null,
    knobImage: Painter? = null,
    gridLines: Int = 6,
    knobSize: Dp = 32.dp,
    glowColor: Color = Color.Cyan,
    onClick:()-> Unit
) {
   // var throttlePosition by rememberSaveable { mutableStateOf(0.25f) }
    var componentHeightPx by rememberSaveable { mutableStateOf(1f) }
    val screenState by viewModel.screenState.collectAsState()
    val knobSizePx = with(LocalDensity.current) { knobSize.toPx() }
    val maxOffsetPx = (componentHeightPx - knobSizePx).coerceAtLeast(1f)

/*    LaunchedEffect(screenState.throttleVoice) {
        // Используем значение throttle для управления скоростью самолета
        Log.d("speed", "throttlePosition до присвоения: $throttlePosition")
        throttlePosition = screenState.throttleVoice
        Log.d("speed", "throttlePosition после: $throttlePosition")
    }*/

    val animatedOffset by animateDpAsState(
        targetValue = (screenState.throttleSlider * height /*maxOffsetPx*/).dp,
        label = "Throttle Offset"
    )

    Box(
        modifier = modifier.clickable{onClick()}
            .width(50.dp)
            .height(height .dp)
            .onGloballyPositioned {
                componentHeightPx = it.size.height.toFloat()
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    change.consume()
                  /* throttlePosition = (throttlePosition - dragAmount / 1000
                            *//*componentHeightPx*//*).coerceIn(0.15f, 0.85f)*/

                    val newThrottle = (screenState.throttleSlider - dragAmount / 1000f)
                        .coerceIn(0.15f, 0.85f)
                    onThrottleChange(newThrottle)
                }
            }
    ) {
        // Шкала — фото или неон
        if (scaleImage != null) {
            Image(
                painter = scaleImage,
                contentDescription = "Throttle Scale",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .align(Alignment.Center)
            )
        } else {
            NeonThrottleScale(gridLines = gridLines, glowColor = glowColor)
        }

        // Ползунок — фото или неоновый
        if (knobImage != null) {
            Image(
                painter = knobImage,
                contentDescription = "Throttle Knob",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = -animatedOffset)
                    .size(knobSize)
            )
        } else {
            NeonThrottleKnob(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = -animatedOffset)
                    .size(knobSize),
                glowColor = glowColor
            )
        }
    }
}

@Composable
fun NeonThrottleScale(
    modifier: Modifier = Modifier,
    gridLines: Int = 6,
    glowColor: Color = Color.Green,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val spacing = size.height / (gridLines - 1)
            for (i in 0 until gridLines) {
                val y = i * spacing
                drawLine(
                    color = glowColor,
                    start = Offset(x = 10f, y = y),
                    end = Offset(x = size.width - 10f, y = y),
                    strokeWidth = 4f
                )
            }
        }
    }
}

@Composable
fun NeonThrottleKnob(
    modifier: Modifier = Modifier,
    glowColor: Color = Color.Magenta,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF222222).copy(alpha = 0.2f))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
/*            drawCircle(
                color = glowColor,
                style = Stroke(width = 4f),
                alpha = 0.7f
            )
            drawCircle(
                color = glowColor.copy(alpha = 0.4f),
                radius = size.minDimension / 2
            )*/
            drawRect(color = glowColor.copy(alpha = 0.4f))
        }
    }
}
