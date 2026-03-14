package com.dogfight.magic.game_ui.radar.upravlenie.buttons.target_fokus

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dogfight.magic.R


private val NeonShape = RoundedCornerShape(14.dp)
private val EnabledGlow = Color(0xFF00FFF0)
private val DisabledGlow = Color(0xFF444444)

@Composable
fun FocusPanel(
    fighterFocus: Boolean?,
    onEnemyFocusClick: () -> Unit,
    onFighterFocusClick: () -> Unit,
) {
    Row() {
        FocusButton(iconRes = R.drawable.img_enemy_focus,enabled = fighterFocus==false) { onEnemyFocusClick() }
        FocusButton(iconRes = R.drawable.img_fighter_focus, enabled = fighterFocus==true) { onFighterFocusClick() }
    }
}

@Composable
fun FocusButton(
    modifier: Modifier = Modifier,
    iconRes: Int = R.drawable.img_enemy_focus,          // PNG / Vector
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
            .width(48.dp)
            .height(48.dp)
            .padding(0.5.dp)
            .background(Color.Transparent, NeonShape)
            //.border(2.dp, glow, NeonShape)
            .clickable(/*enabled = enabled*/ onClick = onClick),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                )
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        )
        {
            Image(
                painterResource(iconRes),
                "",
                Modifier
                    .alpha(0.5f)
                    .align(Alignment.CenterStart)
                    .padding(1.dp)
                    .clip(RoundedCornerShape(13.dp)).fillMaxSize(),
                   // .size(56.dp),
                contentScale = ContentScale.Fit
            )
            CropFrameBox(cornerColor=glow)
        }
    }
}

@Composable
fun CropFrameBox(
    modifier: Modifier = Modifier,
    cornerColor: Color = Color.White,
    cornerSize: Dp = 32.dp,
    strokeWidth: Dp = 4.dp
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .border(BorderStroke(1.dp, Color.Transparent))
            .background(Color.Transparent)
    ) {
        // Top-left corner
        Canvas(modifier = Modifier
            .align(Alignment.TopStart)
            .size(cornerSize)) {
            drawLine(
                color = cornerColor,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = strokeWidth.toPx()
            )
            drawLine(
                color = cornerColor,
                start = Offset(0f, 0f),
                end = Offset(0f, size.height),
                strokeWidth = strokeWidth.toPx()
            )
        }

        // Top-right corner
        Canvas(modifier = Modifier
            .align(Alignment.TopEnd)
            .size(cornerSize)) {
            drawLine(
                color = cornerColor,
                start = Offset(size.width, 0f),
                end = Offset(0f, 0f),
                strokeWidth = strokeWidth.toPx()
            )
            drawLine(
                color = cornerColor,
                start = Offset(size.width, 0f),
                end = Offset(size.width, size.height),
                strokeWidth = strokeWidth.toPx()
            )
        }

        // Bottom-left corner
        Canvas(modifier = Modifier
            .align(Alignment.BottomStart)
            .size(cornerSize)) {
            drawLine(
                color = cornerColor,
                start = Offset(0f, size.height),
                end = Offset(0f, 0f),
                strokeWidth = strokeWidth.toPx()
            )
            drawLine(
                color = cornerColor,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = strokeWidth.toPx()
            )
        }

        // Bottom-right corner
        Canvas(modifier = Modifier
            .align(Alignment.BottomEnd)
            .size(cornerSize)) {
            drawLine(
                color = cornerColor,
                start = Offset(size.width, size.height),
                end = Offset(size.width, 0f),
                strokeWidth = strokeWidth.toPx()
            )
            drawLine(
                color = cornerColor,
                start = Offset(size.width, size.height),
                end = Offset(0f, size.height),
                strokeWidth = strokeWidth.toPx()
            )
        }
    }
}
