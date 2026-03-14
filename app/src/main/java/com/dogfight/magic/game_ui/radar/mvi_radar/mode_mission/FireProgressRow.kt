package com.dogfight.magic.game_ui.radar.mvi_radar.mode_mission

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dogfight.magic.R

@Composable
fun FireProgressRow(
    fireProgress: Float = 0f,
    modifier: Modifier = Modifier,
    familyImageRes: Int = R.drawable.img_famely_fire, // замените на свой ресурс
) {
    val animatedWidth = animateDpAsState(
        targetValue = (fireProgress.coerceIn(0f, 1f) * 96).dp,
        animationSpec = tween(durationMillis = 300),
        label = "ShieldProgressWidth"
    ).value
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
        Image(
            painter = painterResource(id = if (fireProgress < 0.8f) familyImageRes else familyImageRes) ,
        contentDescription = "Family in danger",
        modifier = Modifier.align(Alignment.TopStart).padding(start = 4.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Fit
        )
        Column (
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.align(Alignment.TopStart).padding(vertical = 2.dp)
                .width(4.dp)
                .fillMaxHeight()
                .background(Color.Cyan.copy(alpha = 1f), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 0.dp, vertical = 0.dp)
        ) {
/*            val fireEmoji = "\uD83D\uDD25" // 🔥
            repeat(totalFireCount) { index ->
                if (index < activeFireCount)
                    Text(
                        text = if (index < activeFireCount) fireEmoji else "⬛",
                        fontSize = 8.sp,
                        modifier = Modifier.padding(end = 0.dp)
                    ) else Box(
                    modifier = Modifier
                        .size(8.dp)
                        .border(0.5.dp, Color.Red)
                        .padding(0.5.dp)
                )
            }*/
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(animatedWidth)
                    .background(Color.Red.copy(alpha = 1f), shape = RoundedCornerShape(8.dp))
            )
        }
    }
}
