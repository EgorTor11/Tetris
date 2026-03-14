package com.dogfight.magic.game_ui.radar.upravlenie.buttons

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dogfight.magic.game_ui.widgets.animations.FlipFlopLottiePlane


private val NeonShape = RoundedCornerShape(14.dp)
private val EnabledGlow = Color(0xFF00FFF0)
private val DisabledGlow = Color(0xFF444444)

@Composable
fun NeonActionButton(
    modifier: Modifier = Modifier,
    count: Int = 0,
    countText: String,
    smileText: String = "",
    iconRes: Int? = null,          // PNG / Vector
    lottieAsset: String? = null,   // "magic_wand.json" из assets/
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val glow = if (enabled) EnabledGlow else DisabledGlow
    val brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFF0000),
            Color(0xFFFFFF00),
            Color(0xFF00FF00),
            Color(0xFF00FFFF),
            Color(0xFF0000FF),
            Color(0xFFFF00FF),
            Color(0xFFFF0000)
        ),

        //  start = Offset(8f, 8f),
        //  end = Offset(50f, 50f),
        tileMode = TileMode.Mirror
    )
    Box(
        modifier = Modifier
            .width(64.dp)
            .height(64.dp)
            .padding(0.5 .dp)
            .background(Color.Transparent, NeonShape)
            .border(2.dp, glow, NeonShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (lottieAsset == "anim_yak.json") {
                FlipFlopLottiePlane(lottieAsset)
            } else when {
                lottieAsset != null -> {
                    val comp by rememberLottieComposition(
                        LottieCompositionSpec.Asset(lottieAsset)
                    )
                    val progress by animateLottieCompositionAsState(
                        composition = comp,
                        iterations = LottieConstants.IterateForever,
                        isPlaying = true
                    )
                    /*LottieAnimation(
                        composition = comp,
                        progress = { progress },
                        modifier = Modifier.size(28.dp),
                        enableMergePaths = true
                    )*/
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                Color.Black.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clip(RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    )
                    {
                        if (lottieAsset != "") LottieAnimation(
                            composition = comp,
                            progress = { progress },
                            modifier = modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)) // <-- обрезаем всё содержимое по форме
                                .aspectRatio(1f)
                                .let { baseModifier ->
                                    if (enabled) {
                                        baseModifier.drawWithContent {
                                            drawContent()
                                            drawRect(
                                                brush = brush,
                                                alpha = 0.3f,
                                                blendMode = BlendMode.Overlay,
                                            )
                                        }
                                    } else baseModifier
                                },
                        ) else Text(
                            smileText,
                            fontSize = 16.sp,
                            color = glow,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(4.dp)
                        )
                        Text(
                            countText,
                            fontSize = 14.sp,
                            color = glow,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(2.dp)
                        )

                    }

                }
                iconRes != null -> {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                Color.Black.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clip(RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    )
                    {
                        Image(painterResource(iconRes),"", Modifier.alpha(0.5f).align(Alignment.CenterStart).padding(1.dp).clip(RoundedCornerShape(13.dp)).size(64.dp), contentScale = ContentScale.Crop)
                        Text(
                            countText,
                            fontSize = 14.sp,
                            color = glow,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier .padding(4.dp).background(Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(4.dp))
                                .align(Alignment.BottomEnd)

                        )

                    }
                }
            }
        }
    }
}
