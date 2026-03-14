package com.dogfight.magic.unity_ads

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dogfight.magic.R
import com.dogfight.magic.navigationwithcompose.presentation.composition_local.LocalNavController
import androidx.annotation.StringRes

@Composable
fun NeonChoiceDialog(
    type: ResourceDepletionType,
    visible: Boolean,
    starCount: Int,
    onDismiss: () -> Unit,
    onChooseStar: () -> Unit,
    onChooseAd: () -> Unit,
) {
    val navController = LocalNavController.current
    // Анимация для неонового переливания
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
    // Определим награду
    val rewardForStar = type.rewardForStar
    val rewardForAd = type.rewardForAd

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
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
                    .border(
                        border = BorderStroke(2.dp, borderColor),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable(enabled = false) {}
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(id = type.messageRes),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFCCFFFF)
                    )

                    if (type !is ResourceDepletionType.Stars)
                    Text(
                        text = stringResource(R.string.choose_how_to_top_up),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF111133)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    ) {
                        val buttonModifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(12.dp))

                        // ⭐ Кнопка за звезду
                        if (type !is ResourceDepletionType.Stars)
                            Box(
                                modifier = buttonModifier
                                    .background(Color(0xFF111133))
                                    .border(
                                        width = 1.dp,
                                        color = if (starCount > 0) Color.Cyan else Color.Gray,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable(enabled = starCount > 0) { onChooseStar() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$rewardForStar/⭐",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (starCount > 0) Color(0xFF00FFFF) else Color.Gray
                                )
                            } else Box(
                            modifier = buttonModifier
                                .background(Color(0xFF111133))
                                .border(
                                    width = 1.dp,
                                    color = if (starCount > 0) Color.Cyan else Color.Gray,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable() { navController.popBackStack() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.close),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color =  Color(0xFF00FFFF)
                            )
                        }


                        // 🎬 Кнопка за рекламу
                        Box(
                            modifier = buttonModifier
                                .background(Color(0xFF113311))
                                .border(
                                    width = 1.dp,
                                    color = Color.Green,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onChooseAd() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$rewardForAd/🎬",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF00FFFF),
                            )
                        }
                    }

                    Text(
                        text = if (starCount <= 0) stringResource(R.string.you_have_no_stars) else stringResource(
                            R.string.you_have_x_stars, starCount
                        ),
                        fontSize = 14.sp,
                        color = if (starCount <= 0) Color.Red else Color.Green,
                        fontWeight = FontWeight.Normal
                    )
                    if (type != ResourceDepletionType.Stars)
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            TextButton(onDismiss) {
                                Text(
                                    text = stringResource(R.string.close),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFFF8888)
                                )
                            }
                        }
                }
            }
        }
    }
}


/*
sealed class ResourceDepletionType(
    val message: String,
    val rewardForStar: Int,
    val rewardForAd: Int,
    val rewardForFail: Int,
) {
    object Afterburner : ResourceDepletionType(
        message = "⚡ Форсажи закончились!",
        rewardForStar = 3,
        rewardForAd = 30,
        rewardForFail = 1
    )

    object Ammo : ResourceDepletionType(
        message = "🔫 Снаряды  закончились!",
        rewardForStar = 50,
        rewardForAd = 500,
        rewardForFail = 15
    )

    object Shield : ResourceDepletionType(
        message = "🛡 Щиты закончились!",
        rewardForStar = 1,
        rewardForAd = 10,
        rewardForFail = 0
    )

    object Homing : ResourceDepletionType(
        message = "🎯 Секторные Ракеты закончились!",
        rewardForStar = 3,
        rewardForAd = 30,
        rewardForFail = 1
    )

    object SuperRocket :
        ResourceDepletionType(
            "Всенаправленные суперракеты закончились!",
            rewardForStar = 2,
            rewardForAd = 20,
            rewardForFail = 1
        )

    object Avada : ResourceDepletionType(
        "⚡ Aвaдa закончилась!",
        rewardForStar = 1,
        rewardForAd = 10,
        rewardForFail = 0
    )

    object Turn : ResourceDepletionType(
        "🔄 Развороты кончились!",
        rewardForStar = 3,
        rewardForAd = 30,
        rewardForFail = 1
    )

    object Reverse : ResourceDepletionType(
        "⇆ Реверсы кончились!",
        rewardForStar = 3,
        rewardForAd = 30,
        rewardForFail = 1
    )

    object Stars : ResourceDepletionType(
        "😢У тебя недостаточно звезд для этого уровня!",
        rewardForStar = 1,
        rewardForAd = 10,
        rewardForFail = 1
    )

}
*/


sealed class ResourceDepletionType(
    @StringRes val messageRes: Int,
    val rewardForStar: Int,
    val rewardForAd: Int,
    val rewardForFail: Int,
) {
    object Afterburner : ResourceDepletionType(
        messageRes = R.string.depletion_afterburner,
        rewardForStar = 3,
        rewardForAd = 30,
        rewardForFail = 1
    )

    object Ammo : ResourceDepletionType(
        messageRes = R.string.depletion_ammo,
        rewardForStar = 50,
        rewardForAd = 500,
        rewardForFail = 15
    )

    object Shield : ResourceDepletionType(
        messageRes = R.string.depletion_shield,
        rewardForStar = 1,
        rewardForAd = 10,
        rewardForFail = 0
    )

    object Homing : ResourceDepletionType(
        messageRes = R.string.depletion_homing,
        rewardForStar = 3,
        rewardForAd = 30,
        rewardForFail = 1
    )

    object SuperRocket : ResourceDepletionType(
        messageRes = R.string.depletion_superrocket,
        rewardForStar = 2,
        rewardForAd = 20,
        rewardForFail = 1
    )

    object Avada : ResourceDepletionType(
        messageRes = R.string.depletion_avada,
        rewardForStar = 1,
        rewardForAd = 10,
        rewardForFail = 0
    )

    object Turn : ResourceDepletionType(
        messageRes = R.string.depletion_turn,
        rewardForStar = 3,
        rewardForAd = 30,
        rewardForFail = 1
    )

    object Reverse : ResourceDepletionType(
        messageRes = R.string.depletion_reverse,
        rewardForStar = 3,
        rewardForAd = 30,
        rewardForFail = 1
    )

    object Stars : ResourceDepletionType(
        messageRes = R.string.depletion_stars,
        rewardForStar = 1,
        rewardForAd = 10,
        rewardForFail = 1
    )
}

