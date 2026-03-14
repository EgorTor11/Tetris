package com.dogfight.magic.home_screen.shop.shop_top_bar

import android.app.Activity
import android.widget.Toast
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dogfight.magic.R
import com.dogfight.magic.unity_ads.loadRewardedAd
import com.dogfight.magic.unity_ads.showRewardedAd

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ShopItemDetailCard(
    viewModel: ShopViewModel,
    shopItem: InventoryItem?, // твой data-класс
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onGetForAd: () -> Unit,
    onGetForStars: () -> Unit,
) {
    val context = LocalContext.current
    val shopState by viewModel.shopState.collectAsState()
    val transition = rememberInfiniteTransition()
    val neonColor by transition.animateColor(
        initialValue = Color(0xFFFFC1E3),
        targetValue = Color(0xFFE1A2F5),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    AnimatedContent(
        targetState = shopItem,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "ShopItemDetailCard"
    ) { item ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (item != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onDismiss() }
                        .background(Color.Black.copy(alpha = 0.5f))
                )

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "${item.id}-bounds"),
                            animatedVisibilityScope = this@AnimatedContent,
                            clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(16.dp))
                        )
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        ShopItemCardMini(
                            iconRes = item.iconRes,
                            count = getShopCount(item,shopState),
                            shopState = shopState,
                            shopItem = item,
                            animationModifier = Modifier.size(120.dp),
                            modifier = Modifier
                                .sharedElement(
                                    state = rememberSharedContentState(item.id),
                                    animatedVisibilityScope = this@AnimatedContent
                                )
                                .size(120.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            onClick = {})
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            Text(
                                text = item.name,
                                color = neonColor,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = item.description,
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Доп. информация (например, характеристики, уровни, эффекты)
                    if (item.details.isNotEmpty()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            item.details.forEach {
                                Text(
                                    text = "• $it",
                                    fontSize = 14.sp,
                                    color = Color.DarkGray,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }

                    PurchaseOptionsRow(
                        neonColor=neonColor,
                        shopItem=shopItem,
                        costInAd= shopItem?.costInAd ?: 0,
                        availableStars = shopState.starCount,
                        itemName = shopItem?.name ?: "",
                        itemPerStar = shopItem?.costInStars ?: 0,
                        onBuyWithStars = { count, starsUsed ->
                            // TODO: обнови DataStore, прибавь count к щитам и отними starsUsed от звёзд
                            shopItem?.let {
                                updateCountInStars(
                                    it,
                                    shopState,
                                    count,
                                    starsUsed,
                                    viewModel
                                )
                            }
                        },
                        onWatchAd = {
                            // TODO: показать рекламу и добавить 1-2 щита
                            val activity = context as Activity
                            val placementId = "Rewarded_Android"
                            showRewardedAd(
                                activity, placementId,
                                onReward = {
                                    shopItem?.let {
                                        updateCountInAd(
                                            it,
                                            shopState,
                                            it.costInAd,
                                            viewModel
                                        )
                                    }
                                    loadRewardedAd(placementId)
                                },
                                onFallback = {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.video_not_loadet_toast),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        })
                    // Кнопка закрытия
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp, bottom = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(stringResource(R.string.close), color = neonColor)
                        }
                    }
                }
            }
        }
    }
}

private fun updateCountInStars(
    item: InventoryItem,
    shopState: ShopState,
    count: Int,
    starsUsed: Int,
    viewModel: ShopViewModel,
) {
    when (item.id) {
        "stars" -> {
            viewModel.updateStarCount(shopState.starCount + count)
            viewModel.updateStarCount(shopState.starCount - starsUsed)
        }

        "avada" -> {
            viewModel.updateAvadaCount(shopState.avadaCount + count)
            viewModel.updateStarCount(shopState.starCount - starsUsed)
        }

        "ammo" -> {
            viewModel.updateAmmoCount(shopState.ammoCount + count)
            viewModel.updateStarCount(shopState.starCount - starsUsed)
        }

        "afterburner" -> {
            viewModel.updateForsagCount(shopState.forsagCount + count)
            viewModel.updateStarCount(shopState.starCount - starsUsed)
        }

        "shield" -> {
            viewModel.updateShieldCount(shopState.shieldCount + count)
            viewModel.updateStarCount(shopState.starCount - starsUsed)
        }

        "missile_sector" -> {
            viewModel.updateHomingCount(shopState.homingCount + count)
            viewModel.updateStarCount(shopState.starCount - starsUsed)
        }

        "missile_any" -> {
            viewModel.updateSuperRocketCount(shopState.superRocketCount + count)
            viewModel.updateStarCount(shopState.starCount - starsUsed)
        }

        "u_turn" -> {
            viewModel.updateTurnCount(shopState.turnCount + count)
            viewModel.updateStarCount(shopState.starCount - starsUsed)
        }

        "reverse_target" -> {
            viewModel.updateReverseCount(shopState.reverseCount + count)
            viewModel.updateStarCount(shopState.starCount - starsUsed)
        }

        else -> {}
    }

}

private fun updateCountInAd(
    item: InventoryItem,
    shopState: ShopState,
    count: Int,
    viewModel: ShopViewModel,
) {
    when (item.id) {
        "stars" -> {
            viewModel.updateStarCount(shopState.starCount + count)
        }

        "avada" -> {
            viewModel.updateAvadaCount(shopState.avadaCount + count)
        }

        "ammo" -> {
            viewModel.updateAmmoCount(shopState.ammoCount + count)
        }

        "afterburner" -> {
            viewModel.updateForsagCount(shopState.forsagCount + count)
        }

        "shield" -> {
            viewModel.updateShieldCount(shopState.shieldCount + count)
        }

        "missile_sector" -> {
            viewModel.updateHomingCount(shopState.homingCount + count)
        }

        "missile_any" -> {
            viewModel.updateSuperRocketCount(shopState.superRocketCount + count)
        }

        "u_turn" -> {
            viewModel.updateTurnCount(shopState.turnCount + count)
        }

        "reverse_target" -> {
            viewModel.updateReverseCount(shopState.reverseCount + count)
        }

        else -> {}
    }

}

