package com.dogfight.magic.game_ui.radar.upravlenie.rud_design

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dogfight.magic.game_ui.radar.mvi_radar.RadarViewModel
import com.dogfight.magic.game_ui.radar.upravlenie.buttons.neonButtonPresets
import kotlinx.coroutines.launch

@Composable
fun ForsagButton(
    modifier: Modifier = Modifier,
    shape: Shape = neonButtonPresets[1].shape,
    glowColor: Color = neonButtonPresets[1].neonColor,
    backgroundColor: Color = neonButtonPresets[1].backgroundColor,
    viewModel: RadarViewModel,
    onForsagesEmty: () -> Unit = {},
    onEnd: () -> Unit = {},
) {
    val screenState by viewModel.screenState.collectAsState()
    var isActive by remember { mutableStateOf(false) }
    val progress = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(screenState.fighterSpeed) {
        if (screenState.fighterSpeed < screenState.throttleSlider * 7) {
            isActive = false
            viewModel.updateThrottleFromSlider(screenState.throttleSlider)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500, easing = LinearEasing)
            )
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(/*enabled = (screenState.forsagCount > 0)*/) {
            // увеличить скорость, уменьшить счётчик
            if (screenState.forsagCount > 0) {
                viewModel.updateFighterSpeed(screenState.fighterSpeed * 7)
                viewModel.decreaseForsag()
                if (!isActive) {
                    isActive = true
                    coroutineScope.launch {
                        progress.snapTo(1f)
                        progress.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(durationMillis = 5000, easing = LinearEasing)
                        )
                        isActive = false
                        viewModel.updateThrottleFromSlider(screenState.throttleSlider)
                        progress.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(durationMillis = 200, easing = LinearEasing)
                        )
                    }
                } else {
                    coroutineScope.launch {
                        isActive = false

                        viewModel.updateThrottleFromSlider(screenState.throttleSlider)
                        progress.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(durationMillis = 500, easing = LinearEasing)
                        )
                    }
                }
            }else{
                if (!isActive)
                onForsagesEmty()
                else    coroutineScope.launch {
                    isActive = false

                    viewModel.updateThrottleFromSlider(screenState.throttleSlider)
                    progress.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 500, easing = LinearEasing)
                    )
                }
            }
        }) {
        Box(
            modifier = modifier
                .width(60.dp)
                .height(50.dp)
                .padding(8.dp)
                .shadow(20.dp, shape, ambientColor = glowColor, spotColor = glowColor)
                .background(backgroundColor.copy(alpha = 0.0f), shape)
                .clip(shape)
                .border(
                    width = 2.dp,
                    color = glowColor,
                    shape = shape
                )
            //  .padding(horizontal = 10.dp, vertical = 2.dp)
            ,
            //  .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {

            // Заливка, которая уменьшается
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress.value)
                        .background(if (isActive) Color.Magenta else backgroundColor)
                )
            }
            Text(
                text = if (isActive) "🚀x7" else "🚀x7",
                fontSize = if (isActive) 14.sp else 14.sp,
                color = if (isActive) Color.White else Color.White,
                textAlign = TextAlign.Center
            )
        }
        ForsagCount(screenState.forsagCount)
    }

}

@Composable
fun ForsagCount(count: Int = 10) {
    Box(contentAlignment = Alignment.Center) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (count > 10) Text(count.toString(), color = Color.Green, fontSize = 12.sp)
            repeat(10) {
                Box(
                    modifier = Modifier
                        .size(width = 2.dp, height = 10.dp)
                        .background(
                            if (it >= count) Color.Red else Color.Green,
                            shape = RoundedCornerShape(1.dp)
                        )
                )
            }

        }
    }
}

