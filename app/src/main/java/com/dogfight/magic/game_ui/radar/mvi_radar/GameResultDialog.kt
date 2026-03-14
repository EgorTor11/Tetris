package com.dogfight.magic.game_ui.radar.mvi_radar

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dogfight.magic.R
import kotlinx.coroutines.delay


@Composable
fun GameResultDialog(
    gameLevel: Int = 0,
    viewModel: RadarViewModel,
    isVisible: Boolean,
    isWinner: Boolean?,
    currentStarCount: Int = 0,
    deltaStarCount: Int = 1,
    onPlayAgain: () -> Unit,
    onExit: () -> Unit,
) {
    val state by viewModel.screenState.collectAsState()
    var starCountTextSize by remember { mutableStateOf(20.sp) }
    val delta = remember { mutableStateOf(getAddStarsCount(gameLevel, isWinner)) }
    var starCountText by remember { mutableStateOf(state.starCountAfterStartGame) }
    var progress by remember { mutableStateOf(0f) }
    val compositionSalut by rememberLottieComposition(LottieCompositionSpec.Asset("animation_salut.json"))
    val compositionHard by rememberLottieComposition(LottieCompositionSpec.Asset("animation_brocken_hard.json"))
    val compositionScales by rememberLottieComposition(LottieCompositionSpec.Asset("animation_scales.json"))

    LaunchedEffect(Unit) {
        delay(500)
        starCountTextSize = 30.sp
        while (starCountText < state.starCount) {
            Log.d("starCountText", "$starCountText")
            starCountText = starCountText + 1
            delay(500)
        }
        starCountTextSize = 20.sp
    }

    LaunchedEffect(Unit) {
        while (true) {
            progress = (progress + 0.005f) % 1f // Было 0.02f — стало 0.005f // Зацикленная анимация
            delay(16L) // 60 FPS
        }
    }
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
    if (!isVisible) return
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xAA000000))
                .clickable(enabled = false) {},
            contentAlignment = Alignment.Center
        ) {
            if (gameLevel != 0)
                LottieAnimation(
                    composition = when (isWinner) {
                        true -> compositionSalut
                        false -> compositionHard
                        null -> compositionScales
                    },
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(300.dp),
                )
            if (gameLevel == 11) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = if (isWinner != true) R.drawable.img_famely_fire else R.drawable.img_famely_win),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()//.clip(RoundedCornerShape(16.dp))
                        ,
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6f))
                    )

                    if (gameLevel == 11 && isWinner == true)
                        LottieAnimation(
                            composition = when (isWinner) {
                                true -> compositionSalut
                                false -> compositionHard
                                null -> compositionScales
                            },
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .size(300.dp),
                        )
                }

            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            /*   .background(
                                   brush = Brush.linearGradient(
                                       colors = listOf(Color(0xFF00FFF0), Color(0xFF0055FF))
                                   ),
                                   shape = RoundedCornerShape(16.dp)
                               )
                               .border(
                                   width = 2.dp,
                                   color = Color.Cyan,
                                   shape = RoundedCornerShape(16.dp)
                               )*/.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = when (isWinner) {
                                true -> stringResource(R.string.victory)
                                false -> stringResource(R.string.losing)
                                null -> stringResource(R.string.draw)
                            },
                            fontSize = 28.sp,
                            color = Color.White
                        )
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
                            Image(
                                painterResource(R.drawable.img_gold_kosmo_stars),
                                "",
                                Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(1.dp)
                                    .clip(RoundedCornerShape(13.dp))
                                /*.size(64.dp)*/,
                                contentScale = ContentScale.Crop,
                                alpha = 0.5f
                            )
                            Box(
                                modifier = Modifier
                                    .defaultMinSize(minWidth = 30.dp)
                                    .animateContentSize(
                                        animationSpec = spring(
                                            dampingRatio = 0.3f,
                                            stiffness = 2000f
                                        )
                                    )
                                    .background(
                                        MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .align(Alignment.BottomCenter)
                                    .padding(2.dp)
                            ) {
                                Text(
                                    "$starCountText",
                                    modifier = Modifier.align(Alignment.BottomCenter),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    fontSize = starCountTextSize
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    // .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                 /*   NeonButton(
                        text = "\uD83C\uDFAE\nИграть ещё",
                        onClick = onPlayAgain,
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp)
                    )*/
                    Box(
                        modifier = Modifier
                            .clickable { onPlayAgain() }
                            .padding(8.dp)
                            .size(100.dp)
                            .background(
                                Color.Black.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clip(RoundedCornerShape(12.dp))
                            .border(2.dp, Color.Cyan),
                        contentAlignment = Alignment.Center
                    )
                    {
                        Image(painterResource(R.drawable.img_play_again),"", Modifier
                            .alpha(0.9f)
                            .align(Alignment.CenterStart)
                            .padding(1.dp)
                            .clip(RoundedCornerShape(13.dp))
                            .size(100.dp), contentScale = ContentScale.Crop)
                        Text(
                            text = stringResource(R.string.play),
                            color = Color(0xFF00FFFF),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                   /* NeonButton(
                        text = "\uD83D\uDEB6\u200D♂\uFE0F⬅\uFE0F\nВыйти",
                        onClick = onExit,
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp)
                    )*/
                    Box(
                        modifier = Modifier
                            .clickable { onExit() }
                            .padding(8.dp)
                            .size(100.dp)
                            .background(
                                Color.Black.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clip(RoundedCornerShape(12.dp))
                            .border(2.dp, Color.Magenta),
                        contentAlignment = Alignment.Center
                    )
                    {
                        Image(painterResource(R.drawable.img_exit),"", Modifier
                            .alpha(0.9f)
                            .align(Alignment.CenterStart)
                            .padding(1.dp)
                            .clip(RoundedCornerShape(13.dp))
                            .size(100.dp), contentScale = ContentScale.Crop)
                        Text(
                            text = stringResource(R.string.exit),
                            color = Color.Magenta,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NeonButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .fillMaxWidth()
            .background(Color(0xFF111133))
            .border(
                width = 1.dp,
                color = Color.Cyan,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color(0xFF00FFFF),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

fun getAddStarsText(gameLevel: Int, isWinner: Boolean?): String {
    when (isWinner) {
        false -> {
            return ""
        }

        true -> {
            return when (gameLevel) {
                in 1..5 -> "Выйгрыш ${gameLevel * 2} ✰"
                11 -> {
                    "Выйгрыш ${1} ✰"
                }

                else -> ""
            }
        }

        null -> {
            return when (gameLevel) {
                in 1..5 -> "Выйгрыш ${gameLevel} ✰"
                else -> ""
            }
        }
    }
}

fun getAddStarsCount(gameLevel: Int, isWinner: Boolean?): Int {
    when (isWinner) {
        false -> {
            return 0
        }

        true -> {
            return when (gameLevel) {
                in 1..5 -> gameLevel * 2
                11 -> 1
                else -> 0
            }
        }

        null -> {
            return when (gameLevel) {
                in 1..5 -> gameLevel
                else -> 0
            }
        }
    }
}