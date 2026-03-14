package com.dogfight.magic.home_screen

import android.content.pm.ActivityInfo
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dogfight.magic.home_screen.home_widgets.ModeButtonsGrid
import com.dogfight.magic.home_screen.top_bar.SetSystemBars
import com.dogfight.magic.utils.LockScreenOrientation

@Composable
fun NeonHomeScreen(
    modifier: Modifier = Modifier,
) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    SetSystemBars()
    Box(
        modifier = modifier
            .background(color = Color(0xFFD7CCC8))
            .fillMaxSize()
    ) {
/*        // 🌌 Фон: звёздное небо
        Image(
            painter = painterResource(id = R.drawable.home_bg),
            contentDescription = "Night Sky Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )*/

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
/*            Spacer(modifier = Modifier.height(100.dp))
            CarouselWithVisibleEdges()*/
            ModeButtonsGrid()
        }
    }
}

@Composable
fun NeonButton(
    modifier: Modifier = Modifier,
    text: String,
    imageRes: Int,
) {
    val transition = rememberInfiniteTransition()
    val borderColor by transition.animateColor(
        initialValue = Color(0xFF00FFF0),
        targetValue = Color(0xFF0055FF),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000, // длительность анимации 2 секунды
                easing = FastOutSlowInEasing // плавный переход
            ),
            repeatMode = RepeatMode.Reverse // повтор с обратным эффектом
        )
    )

    Box(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(100.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF00FFF0).copy(alpha = 1.0f),
                        Color(0xFF0055FF).copy(alpha = 1.0f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                border = BorderStroke(2.dp, borderColor),
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(painter = painterResource(imageRes), "")
        Text(
            text = text,
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}



