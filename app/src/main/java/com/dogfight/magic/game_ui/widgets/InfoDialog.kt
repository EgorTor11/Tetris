package com.dogfight.magic.game_ui.widgets

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.dogfight.magic.R

@Composable
fun NeonInfoDialog(
    message: String,
    visible: Boolean,
    onDismiss: () -> Unit
) {
    // Анимация для неоновой обводки
    val transition = rememberInfiniteTransition(label = "neonBorder")
    val borderColor by transition.animateColor(
        initialValue = Color(0xFF00FFF0),
        targetValue = Color(0xFF0055FF),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "borderAnim"
    )

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize().background(
                    Color.Black.copy(alpha = 0.7f)
                )
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .padding(24.dp)
                    .shadow(16.dp, shape = RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF00FFF0).copy(alpha = 0.9f),
                                Color(0xFF0055FF).copy(alpha = 0.9f)
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(BorderStroke(2.dp, borderColor), shape = RoundedCornerShape(16.dp))
                    .clickable(enabled = false) {}
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Text(
                        text = message,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFCCFFFF)
                    )
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = stringResource(R.string.close),
                            fontSize = 16.sp,
                            color = Color(0xFFFF8888),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
