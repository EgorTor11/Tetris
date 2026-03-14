package com.dogfight.magic.home_screen.home_widgets

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dogfight.magic.R
import com.dogfight.magic.game_ui.radar.mvi_radar.game_mode.GameMode

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ButtonDetailCardItem(
    buttonData: ModeButtonData?,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
) {
    // Анимация для неонового переливания
    val transition = rememberInfiniteTransition()
    val borderColor by transition.animateColor(
        initialValue = MaterialTheme.colorScheme.tertiary,
        targetValue = MaterialTheme.colorScheme.onTertiaryContainer,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000, // длительность анимации 2 секунды
                easing = FastOutSlowInEasing // плавный переход
            ),
            repeatMode = RepeatMode.Reverse // повтор с обратным эффектом
        )
    )
    AnimatedContent(
        modifier = modifier,
        targetState = buttonData,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "DetailCardItem"
    ) { button ->
        var title by remember {
            mutableStateOf(
                TitleModel(
                    R.string.difficulty_1,
                    R.string.stars_1
                )
            )
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (button != null) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onDismiss() }
                        .background(Color.Black.copy(alpha = 0.6f))
                )

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "${button.id}-bounds"),
                            animatedVisibilityScope = this@AnimatedContent,
                            clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(14.dp))
                        )
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Row {
                        ButtonCardItem(
                            buttonData = button,
                            modifier = Modifier
                                .sharedElement(
                                    state = rememberSharedContentState(key = button.id),
                                    animatedVisibilityScope = this@AnimatedContent
                                )
                                .size(
                                    100.dp
                                )
                                .padding(16.dp),
                            onClick = onDismiss,
                            applyHue = true
                        )
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(title.titleResId),
                                modifier = Modifier.padding(),
                                color = borderColor,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stringResource(title.subTitleResId),
                                modifier = Modifier.padding(),
                                color = borderColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                    }
                    val gameMode = when (button.id) {
                        99 -> GameMode.SHOP
                        1 -> GameMode.TRAINING
                        2 -> GameMode.DOGFIGHT
                        3 -> GameMode.ONLINE
                       // in 1..5 -> GameMode.DOGFIGHT
                      //  in 6..10 -> GameMode.ONLINE
                        else -> GameMode.MISSIONS
                    }
                    CarouselWithVisibleEdges(
                        { title = getTitleList(gameMode)[it] },
                        gameMode
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(stringResource(R.string.close))
                        }
                    }
                }
            }
        }
    }
}

val listLevelNames =
    listOf(
        TitleModel(R.string.difficulty_1, R.string.stars_1),
        TitleModel(R.string.difficulty_2, R.string.stars_2),
        TitleModel(R.string.difficulty_3, R.string.stars_3),
        TitleModel(R.string.difficulty_4, R.string.stars_4),
        TitleModel(R.string.difficulty_5, R.string.stars_5),
    )

val listMissionsNames =
    listOf(
        TitleModel(R.string.fire_title, R.string.watter_drop_subtitle),
        TitleModel(R.string.meteorit_title, R.string.meteorit_subtitle),
    )
val listOnlineNames =
    listOf(
        TitleModel(R.string.online_not_redy_title, R.string.online_not_redy_subtitle),
    )

fun getTitleList(gameMode: GameMode): List<TitleModel> {
    return when (gameMode) {
        GameMode.MISSIONS -> listMissionsNames
        GameMode.ONLINE -> listOnlineNames
        else -> listLevelNames
    }
}

data class TitleModel(val titleResId: Int, val subTitleResId: Int)