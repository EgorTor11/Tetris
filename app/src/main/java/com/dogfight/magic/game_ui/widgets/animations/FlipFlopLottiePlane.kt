package com.dogfight.magic.game_ui.widgets.animations

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun FlipFlopLottiePlane(
    animationAsset: String,
    modifier: Modifier = Modifier,
    durationMillis: Int = 2000
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(animationAsset))

    val infiniteTransition = rememberInfiniteTransition()

    // Чередуем направление (нормальное ↔ отражённое)
    val flip by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = -1f, // Отражение по оси X
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
            .graphicsLayer {
                scaleX = flip // 🔄 зеркалим по горизонтали
            }
    )
}
