package com.dogfight.magic.game_ui.radar.upravlenie.buttons.shield

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dogfight.magic.R

@Composable
fun ShieldButton(
    count: Int = 0,
    modifier: Modifier = Modifier,
    isActive: Boolean,
    progress: Float,
    onClick: () -> Unit,
) {
    val animatedWidth = animateDpAsState(
        targetValue = (progress.coerceIn(0f, 1f) * 64).dp,
        animationSpec = tween(durationMillis = 300),
        label = "ShieldProgressWidth"
    ).value

    Box(
        modifier = modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(Color(0xFF111133))
            .border(
                width = 2.dp,
                color = if (isActive) Color.Cyan else Color.White,
                shape = CircleShape
            )
          .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Прогресс внутрь
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .height(8.dp)
                .width(animatedWidth)
                .background(
                    color = if (progress <= 0.2f && isActive) Color.Red else Color.Cyan,
                    shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                )
        )
        Icon(
            painter = painterResource(id = R.drawable.img_shield_neon),
            contentDescription = "Shield",
            tint = if (progress <= 0.2f && isActive) Color.Red else Color.Cyan,
            modifier = Modifier.size(32.dp)
        )
        Text(
            "$count",
            modifier = Modifier
                .padding(4.dp),
            fontSize = 12.sp,
            color =  Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}



