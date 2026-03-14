package com.dogfight.magic.home_screen.home_widgets

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition


@Composable
fun ButtonCardItem(
    buttonData: ModeButtonData,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    applyHue: Boolean = false,
) {
    val infiniteTransition = rememberInfiniteTransition()

    val shadowModifier = Modifier
        .shadow(4.dp, RoundedCornerShape(8.dp))
        .background(Color.White)

    val hueMatrix = if (applyHue) {
        val hueRotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 4000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
        MatrixUtils.partialHueRotateMatrix(hueRotation, fraction = 0.3f)
    } else {
        ColorMatrix()
    }

    val brush = if (applyHue) {
        val holographicProgress by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
        val offsetX = with(LocalDensity.current) { 300.dp.toPx() } * holographicProgress
        // Holog-Color Graph.
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFFF0000),
                Color(0xFFFFFF00),
                Color(0xFF00FF00),
                Color(0xFF00FFFF),
                Color(0xFF0000FF),
                Color(0xFFFF00FF),
                Color(0xFFFF0000)
            ),
            start = Offset(offsetX, 0f),
            end = Offset(offsetX + 600f, 600f),
            tileMode = androidx.compose.ui.graphics.TileMode.Mirror
        )
    } else {
        null
    }



    Column(
        modifier = modifier .clickable { onClick() }
    ) {
        Box(modifier = shadowModifier) {
            if (buttonData.image !=null)
                Image(
                    painter = painterResource(id = buttonData.image),
                    contentDescription = buttonData.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .let { baseModifier ->
                            if (applyHue && brush != null) {
                                baseModifier.drawWithContent {
                                    drawContent()
                                    drawRect(
                                        brush = brush,
                                        alpha = 0.3f,
                                        blendMode = BlendMode.Overlay
                                    )
                                }
                            } else baseModifier
                        },
                    colorFilter = if (applyHue) ColorFilter.colorMatrix(hueMatrix) else null
                )else if (buttonData.lottieAsset != null){
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.Asset(buttonData.lottieAsset)
                )
                val progress by animateLottieCompositionAsState(
                    composition,
                    iterations = LottieConstants.IterateForever
                )
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth()
                        .aspectRatio(1f)
                        .let { baseModifier ->
                            if (applyHue && brush != null) {
                                baseModifier.drawWithContent {
                                    drawContent()
                                    drawRect(
                                        brush = brush,
                                        alpha = 0.3f,
                                        blendMode = BlendMode.Overlay
                                    )
                                }
                            } else baseModifier
                        },
                )
                if (buttonData.optionLottieAsset!=null){
                    val compositionOpt by rememberLottieComposition(
                        LottieCompositionSpec.Asset(buttonData.optionLottieAsset))
                LottieAnimation(
                    composition = compositionOpt,
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth()
                        .aspectRatio(1f)
                        .let { baseModifier ->
                            if (applyHue && brush != null) {
                                baseModifier.drawWithContent {
                                    drawContent()
                                    drawRect(
                                        brush = brush,
                                        alpha = 0.3f,
                                        blendMode = BlendMode.Overlay
                                    )
                                }
                            } else baseModifier
                        },
                )
                }

            }


        }
        Text(
            text = buttonData.name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )
    }
}