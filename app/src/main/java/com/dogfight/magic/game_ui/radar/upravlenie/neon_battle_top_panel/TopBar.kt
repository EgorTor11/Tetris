package com.dogfight.magic.game_ui.radar.upravlenie.neon_battle_top_panel

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun NeonTopBattlePanel(
    modifier: Modifier = Modifier,
    playerName: String = "Пилот",
    enemyName: String = "Враг",
    playerScore: Int = 2,
    enemyScore: Int = 3,
    timerText: String = "01:12",
    playerHealth: Int = 3,
    enemyHealth: Int = 2,
    isPaused: Boolean,
    onPauseToggle: () -> Unit,
    @DrawableRes playerAvatar: Int,
    @DrawableRes enemyAvatar: Int,
) {
    var expanded by remember { mutableStateOf(true) }
    var pinned by remember { mutableStateOf(false) }

    var panelWidthPx by remember { mutableStateOf(0) }
    val density = LocalDensity.current

    LaunchedEffect(expanded, pinned) {
        if (expanded && !pinned) {
            delay(5000)
            expanded = false
        }
    }

    Box(
        modifier = modifier
            .wrapContentWidth()
            .padding(top = 12.dp)
    ) {

        // 📏 Динамически рассчитываем отступ по ширине панели
        val offsetX = with(density) { panelWidthPx.toDp() + 4.dp }

        // Вынесенная булавка
        AnimatedContent(
            targetState = expanded,
            transitionSpec = {
                fadeIn() + slideInHorizontally { -it } togetherWith
                        fadeOut() + slideOutHorizontally { -it }
            },
            label = "PushPin"
        ) { isExpanded ->
            // 📌 Булавка за краем
            if (isExpanded)
                Icon(
                    imageVector = if (pinned) Icons.Default.PushPin else Icons.Default.PushPin,
                    contentDescription = "Pin",
                    tint = if (pinned) Color.Green else Color.Gray,
                    modifier = Modifier
                        .size(20.dp)
                        .offset(x = offsetX, y = (-8).dp)
                        .clickable { pinned = !pinned }
                )

            if (!isExpanded) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .offset(x = 8.dp /*offsetX / 2*/, y = (-28).dp)
                ) {
                    Text(
                        text = "$playerScore",
                        color = Color.Green,
                        fontSize = 16.sp,
                    )
                    Icon(
                        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.PauseCircle,
                        contentDescription = if (isPaused) "Play" else "Pause",
                        tint = if (isPaused) Color.Green else Color.Cyan,
                        modifier = Modifier
                            .size(28.dp)
                            .padding(horizontal = 4.dp)
                            .clickable { onPauseToggle() }
                    )
                    Text(
                        text = "$enemyScore",
                        color = Color.Red,
                        fontSize = 16.sp,
                    )
                }
            }
        }

        // 🧩 Анимированная панель
        AnimatedContent(
            targetState = expanded,
            transitionSpec = {
                fadeIn() + slideInHorizontally { -it } togetherWith
                        fadeOut() + slideOutHorizontally { -it }
            },
            label = "TopPanel"
        ) { isExpanded ->
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .onGloballyPositioned { layoutCoordinates ->
                        panelWidthPx = layoutCoordinates.size.width
                    }
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xAA000022))
                    .border(1.dp, Color.Cyan, RoundedCornerShape(16.dp))
                    .clickable { expanded = !expanded }
                    .padding(horizontal = if (expanded) 6.dp else 12.dp, vertical = 6.dp)
            ) {
                if (isExpanded) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        PlayerPanelMini(
                            name = playerName,
                            avatarRes = playerAvatar,
                            score = playerScore,
                            health = playerHealth, // или динамически из ViewModel
                            isLeft = true
                        )

                        TimerAndScoreCenter(
                            timerText = timerText,
                            leftScore = playerScore,
                            rightScore = enemyScore,
                            isPaused = isPaused,
                            onPauseToggle = { onPauseToggle() }
                        )

                        PlayerPanelMini(
                            name = enemyName,
                            avatarRes = enemyAvatar,
                            score = enemyScore,
                            health = enemyHealth,
                            isLeft = false
                        )
                    }
                } else {
                    Text(
                        text = timerText,
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun TimerAndScoreCenter(
    timerText: String,
    leftScore: Int,
    rightScore: Int,
    isPaused: Boolean,
    onPauseToggle: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.padding(horizontal = 6.dp)
    ) {
        Text(
            text = timerText,
            fontSize = 24.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = leftScore.toString(),
                fontSize = 16.sp,
                color = Color.LightGray,
                fontWeight = FontWeight.SemiBold
            )

            Icon(
                imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.PauseCircle,
                contentDescription = if (isPaused) "Play" else "Pause",
                tint = if (isPaused) Color.Green else Color.Cyan,
                modifier = Modifier
                    .size(28.dp)
                    .padding(horizontal = 4.dp)
                    .clickable { onPauseToggle() }
            )

            Text(
                text = rightScore.toString(),
                fontSize = 16.sp,
                color = Color.LightGray,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


@Composable
fun PlayerPanelMini(
    name: String,
    @DrawableRes avatarRes: Int,
    score: Int,
    health: Int,
    isLeft: Boolean,
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (isLeft) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Image(
                    painter = painterResource(id = avatarRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(35.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.White, CircleShape)
                )
                ShrinkableScoreHealthBar(health = health)
            }
            Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Top) {
                Text(
                    text = name.take(10) + if (name.length > 10) "…" else "",
                    fontSize = 10.sp,
                    color = Color.White
                )
            }

        } else {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Top) {
                Text(
                    text = name.take(10) + if (name.length > 10) "…" else "",
                    fontSize = 10.sp,
                    color = Color.White
                )

            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Image(
                    painter = painterResource(id = avatarRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(35.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.White, CircleShape)
                )
                ShrinkableScoreHealthBar(health = health)
            }
        }
    }
}

@Composable
fun ShrinkableScoreHealthBar(health: Int, count: Int = 5) {
    Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
        repeat(count) {
            Box(
                modifier = Modifier
                    .size(width = 4.dp, height = 10.dp)
                    .background(
                        if (it >= count - health) Color.Red else Color.Green,
                        shape = RoundedCornerShape(1.dp)
                    )
            )
        }
    }
}