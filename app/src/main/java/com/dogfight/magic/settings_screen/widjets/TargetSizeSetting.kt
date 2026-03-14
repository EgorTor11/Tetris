package com.dogfight.magic.settings_screen.widjets
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dogfight.magic.game_ui.radar.mvi_radar.getRandomAnimFile
import com.dogfight.magic.game_ui.radar.upravlenie.fighter.drawFighterImageWithRollEffect
import com.dogfight.magic.game_ui.widgets.animations.drawLottieAnimation
import com.dogfight.magic.settings_screen.DisplayStyle
import com.dogfight.magic.settings_screen.RadarSettingsState
import kotlinx.coroutines.delay
import com.dogfight.magic.R


@Composable
fun TargetSizeSetting(
    uiState: RadarSettingsState,
    scale: Float,
    onScaleChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val enemyJetImage = ImageBitmap.imageResource(R.drawable.img_figter)
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.target_size),
            fontSize = 16.sp,
            color = Color.Black
        )

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            var progress by remember { mutableStateOf(0f) }
            var lottieKey by remember { mutableStateOf("initial") }
            val compositionRandom by rememberLottieComposition(
                LottieCompositionSpec.Asset(lottieKey),
                cacheKey = lottieKey
            )
            LaunchedEffect(Unit) {
                val file = getRandomAnimFile()
                lottieKey = file.first
            }
            LaunchedEffect(Unit) {
                while (true) {
                    progress = (progress + 0.02f) % 1f // Зацикленная анимация
                    delay(16L) // 60 FPS
                }
            }
            // Канвас-отображение цели (например, самолёт)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = size.center
                val radius = 12f * scale.coerceIn(0.5f, 2.5f)
                drawCircle(
                    color = Color.Red,
                    radius = radius,
                    center = center
                )
            /*    drawLine(
                    color = Color.Cyan,
                    start = Offset(center.x, center.y - radius),
                    end = Offset(center.x, center.y + radius),
                    strokeWidth = 2f
                )*/
                if (uiState.displayStyle== DisplayStyle.CHARACTERS)
                drawLottieAnimation(
                    composition = compositionRandom,
                    progress = progress,
                    position = center,
                    baseSize = 40f, // Базовый размер самолетика
                    fighterAngle = 270f,
                    isLeft = true,
                    // tint = enemyColor,
                    scale =  scale.coerceIn(0.5f, 2.5f) * 1.8f*2 // Масштабирование, как и у точки
                )
                if (uiState.displayStyle== DisplayStyle.FIGHTER)
                    drawFighterImageWithRollEffect(
                        image = enemyJetImage,
                        position = center,
                        rollAngle = 0f,
                        baseSize = 40f,
                        fighterAngle = 0f,
                        isLeft = false,
                        scale = scale.coerceIn(0.5f, 2.5f) * 1.8f*2
                    )
            }
        }

        Spacer(Modifier.height(12.dp))

        Slider(
            value = scale,
            onValueChange = { onScaleChange(it) },
            valueRange = 0.5f..2.5f,
            steps = 5,
            colors = SliderDefaults.colors(
              /*  thumbColor = Color.Cyan,
                activeTrackColor = Color.Cyan,*/
                inactiveTrackColor = Color.DarkGray
            )
        )
    }
}
