@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.dogfight.magic.home_screen.shop.shop_top_bar

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dogfight.magic.R
import com.dogfight.magic.game_ui.widgets.animations.FlipFlopLottiePlane
import kotlinx.coroutines.delay


@Composable
fun SharedTransitionScope.InventoryBar(
    viewModel: ShopViewModel,
    selectedItem: InventoryItem?,
    items: List<InventoryItem>,
    onItemClick: (InventoryItem) -> Unit,
) {
    val shopState by viewModel.shopState.collectAsState()
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White), // фон тулбара
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { item ->

            AnimatedVisibility(
                visible = item != selectedItem,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
                modifier = Modifier.animateItem()
            ) {
                Box(
                    modifier = Modifier
                        .clickable {
                            onItemClick(item)
                        }
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "${item.id}-bounds"),
                            animatedVisibilityScope = this,
                            clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(12.dp))
                        )
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    ShopItemCardMini(
                        iconRes = item.iconRes,
                        shopState = shopState,
                        shopItem = item,
                        modifier = Modifier
                            .sharedElement(
                                state = rememberSharedContentState(item.id),
                                animatedVisibilityScope = this@AnimatedVisibility
                            )
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        onClick = {})
                }

            }
        }
    }
}


/*@Composable
fun ShopItemCardSmall(
    shopItem: InventoryItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .background(Color(0xFFFBEFFB), RoundedCornerShape(12.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = shopItem.name.take(2).uppercase(),
            color = Color(0xFFE91E63),
            fontWeight = FontWeight.Bold
        )
    }
}*/


@Composable
fun ShopItemCardMini(
    iconRes: Int? = null,
    count: Int = 0,
    shopState: ShopState,
    shopItem: InventoryItem,
    modifier: Modifier = Modifier,
    animationModifier: Modifier = Modifier.size(64.dp),
    onClick: () -> Unit,
) {
    var textSize by remember { mutableStateOf(14.sp) }
    var previousCount by remember { mutableStateOf(count) }
    val countIncreased = count != previousCount

    LaunchedEffect(shopState.starCount) {
        if (countIncreased) {
            textSize = 32.sp
            delay(1000)
            textSize = 14.sp
        }
    }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (iconRes != null) {
            Box(
                modifier = animationModifier
                    // .size(64.dp)
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
                    animationModifier
                        .align(Alignment.CenterStart)
                        .padding(1.dp)
                        .clip(RoundedCornerShape(13.dp))
                    /*.size(64.dp)*/,
                    contentScale = ContentScale.Crop
                )

            }
        } else Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Lottie анимация, если указана
            shopItem.lottieAssetPath?.let { assetPath ->

                val composition by rememberLottieComposition(
                    LottieCompositionSpec.Asset(
                        assetPath
                    )
                )
                val overPath = "animation_rocket_2.json"
                val overComposition by rememberLottieComposition(
                    LottieCompositionSpec.Asset(
                        overPath
                    )
                )


                val progress by animateLottieCompositionAsState(
                    composition,
                    iterations = LottieConstants.IterateForever
                )
                when (assetPath) {
                    "anim_yak.json" -> FlipFlopLottiePlane(assetPath)
                    "animation_sector.json" -> {
                        Box(contentAlignment = Alignment.Center) {
                            LottieAnimation(
                                composition = composition,
                                progress = { progress },
                                modifier = animationModifier

                            )
                            LottieAnimation(
                                composition = overComposition,
                                progress = { progress },
                                modifier = animationModifier

                            )
                        }
                    }

                    else -> LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = animationModifier

                    )
                }
            } ?: Text( // Fallback на 2 буквы, если анимации нет
                text = shopItem.name.take(2).uppercase(),
                color = Color(0xFFE91E63),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
        Box(
            modifier = Modifier
                .defaultMinSize(minWidth = 30.dp)
                .animateContentSize(animationSpec = spring(dampingRatio = 0.3f, stiffness = 2000f))
                .background(
                    MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f),
                    RoundedCornerShape(4.dp)
                )
                .align(Alignment.BottomCenter)
                .padding(2.dp)
        ) {
            Text(
                "${getShopCount(shopItem, shopState)}",
                modifier = Modifier.align(Alignment.BottomCenter),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = textSize
            )
        }
        // AnimatedShopCounterBox(count = getShopCount(shopItem, shopState))
    }
}

/*@Composable
fun BoxScope.AnimatedShopCounterBox(count: Int) {
    var previousCount by remember { mutableStateOf(count) }
    val countIncreased = count > previousCount

    // Запускаем анимации только если count увеличился
    val scale = remember { Animatable(1f) }
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(count) {
        if (countIncreased) {
            launch {
                scale.animateTo(
                    targetValue = 1.4f,
                    animationSpec = spring(dampingRatio = 0.4f, stiffness = 300f)
                )
                scale.animateTo(1f)
            }
            launch {
                alpha.animateTo(0.3f)
                alpha.animateTo(1f)
            }
        }
        previousCount = count
    }

    Box(
        modifier = Modifier
            .defaultMinSize(minWidth = 30.dp)
            .graphicsLayer(
                scaleX = scale.value,
                scaleY = scale.value,
                alpha = alpha.value
            )
            .background(
                MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.85f),
                RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 2.dp, vertical = 2.dp)
            .align(Alignment.BottomCenter),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = count.toString(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}*/


/*@Composable
fun ProductDetailDialog(
    item: InventoryItem,
    onDismiss: () -> Unit,
    onBuyWithStars: (count: Int) -> Unit,
    onBuyWithAd: (count: Int) -> Unit,
) {
    var count by remember { mutableStateOf(1) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(item.name) },
        text = {
            Column {
                Text(item.description)
                item.animation?.invoke()
                Row {
                    Button(onClick = { if (count > 1) count-- }) { Text("-") }
                    Text(" $count ")
                    Button(onClick = { count++ }) { Text("+") }
                }
            }
        },
        confirmButton = {
            Row {
                Button(onClick = { onBuyWithStars(count) }) {
                    Text("Купить за ${item.costInStars * count}★")
                }
                if (item.hasAdPurchaseOption) {
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { onBuyWithAd(count) }) {
                        Text("За рекламу x$count")
                    }
                }
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Отмена") }
        }
    )
}*/

/*val inventoryItems = listOf(
    InventoryItem(
        id = "stars",
        name = "Звезды",
        quantity = 1f,
        description = "Их вручают за победу! Можно менять на ресурсы и тратить на разных уровнях!",
        costInStars = 1,
        hasAdPurchaseOption = true,
        lottieAssetPath = "animation_one_star.json",
        costInAd = 10,
        iconRes = R.drawable.img_gold_kosmo_stars
    ),
    InventoryItem(
        id = "avada",
        name = "Авада Кедавра",
        quantity = 1f,
        description = "Удар молнии.Убивает цель. Защита только щитом",
        costInStars = 1,
        hasAdPurchaseOption = true,
        lottieAssetPath = "animation_magic_palka.json",
        costInAd = 10,
        iconRes = R.drawable.img_mag_realistic
    ),
    InventoryItem(
        id = "ammo",
        name = "Снаряды",
        quantity = 1 / 50f,
        description = "Боеприпасы для авиационной пушки",
        costInStars = 50,
        hasAdPurchaseOption = true,
        lottieAssetPath = "animation_bomba.json",
        costInAd = 500,
        iconRes = R.drawable.img_puska
    ),
    InventoryItem(
        id = "afterburner",
        name = "Форсаж",
        quantity = 1 / 3f,
        description = "Увеличивает скорость в 7 раз на 5 секунд",
        costInStars = 3,
        hasAdPurchaseOption = true,
        lottieAssetPath = "animation_speedometr.json",
        costInAd = 30,
        iconRes = R.drawable.img_forsag
    ),
    InventoryItem(
        id = "shield",
        name = "Щит",
        quantity = 1f,
        description = "Блокирует попадания и даже Аваду",
        costInStars = 1,
        hasAdPurchaseOption = true,
        lottieAssetPath = "animation_shield.json",
        costInAd = 10,
        iconRes = R.drawable.img_kosmo_shield
    ),
    InventoryItem(
        id = "missile_sector",
        name = "Секторная ракета",
        quantity = 1 / 3f,
        description = "Бьёт по цели в 60° секторе",
        costInStars = 3,
        hasAdPurchaseOption = true,
        lottieAssetPath = "animation_sector.json",
        costInAd = 30,
        iconRes = R.drawable.img_sectorrocket
    ),
    InventoryItem(
        id = "missile_any",
        name = "Всенаправленная суперракета",
        quantity = 1 / 2f,
        description = "Бьёт по любой цели",
        costInStars = 2,
        hasAdPurchaseOption = true,
        lottieAssetPath = "anim_target_super.json",
        costInAd = 20,
        iconRes = R.drawable.img_superrocket
    ),
    InventoryItem(
        id = "u_turn",
        name = "Боевой разворот",
        quantity = 1 / 3f,
        description = "Разворачивает самолёт на 180°",
        costInStars = 3,
        hasAdPurchaseOption = true,
        lottieAssetPath = "animation_turn.json",
        costInAd = 30,
        iconRes = R.drawable.img_turn_svoy
    ),
    InventoryItem(
        id = "reverse_target",
        name = "Развернуть цель",
        quantity = 1 / 3f,
        description = "Поворачивает цель на 180°",
        costInStars = 3,
        hasAdPurchaseOption = true,
        lottieAssetPath = "anim_yak.json",
        costInAd = 30,
        iconRes = R.drawable.img_boevoy_razvorot_vrag
    ),
)*/

fun getInventoryItems(context: Context): List<InventoryItem> {
    return listOf(
        InventoryItem(
            id = "stars",
            name = context.getString(R.string.item_stars_name),
            quantity = 1f,
            description = context.getString(R.string.item_stars_desc),
            costInStars = 1,
            hasAdPurchaseOption = true,
            lottieAssetPath = "animation_one_star.json",
            costInAd = 10,
            iconRes = R.drawable.img_gold_kosmo_stars
        ),
        InventoryItem(
            id = "avada",
            name = context.getString(R.string.item_avada_name),
            quantity = 1f,
            description = context.getString(R.string.item_avada_desc),
            costInStars = 1,
            hasAdPurchaseOption = true,
            lottieAssetPath = "animation_magic_palka.json",
            costInAd = 10,
            iconRes = R.drawable.img_mag_realistic
        ),
        InventoryItem(
            id = "ammo",
            name = context.getString(R.string.item_ammo_name),
            quantity = 1 / 50f,
            description = context.getString(R.string.item_ammo_desc),
            costInStars = 50,
            hasAdPurchaseOption = true,
            lottieAssetPath = "animation_bomba.json",
            costInAd = 500,
            iconRes = R.drawable.img_puska
        ),
        InventoryItem(
            id = "afterburner",
            name = context.getString(R.string.item_afterburner_name),
            quantity = 1 / 3f,
            description = context.getString(R.string.item_afterburner_desc),
            costInStars = 3,
            hasAdPurchaseOption = true,
            lottieAssetPath = "animation_speedometr.json",
            costInAd = 30,
            iconRes = R.drawable.img_forsag
        ),
        InventoryItem(
            id = "shield",
            name = context.getString(R.string.item_shield_name),
            quantity = 1f,
            description = context.getString(R.string.item_shield_desc),
            costInStars = 1,
            hasAdPurchaseOption = true,
            lottieAssetPath = "animation_shield.json",
            costInAd = 10,
            iconRes = R.drawable.img_kosmo_shield
        ),
        InventoryItem(
            id = "missile_sector",
            name = context.getString(R.string.item_missile_sector_name),
            quantity = 1 / 3f,
            description = context.getString(R.string.item_missile_sector_desc),
            costInStars = 3,
            hasAdPurchaseOption = true,
            lottieAssetPath = "animation_sector.json",
            costInAd = 30,
            iconRes = R.drawable.img_sectorrocket
        ),
        InventoryItem(
            id = "missile_any",
            name = context.getString(R.string.item_missile_any_name),
            quantity = 1 / 2f,
            description = context.getString(R.string.item_missile_any_desc),
            costInStars = 2,
            hasAdPurchaseOption = true,
            lottieAssetPath = "anim_target_super.json",
            costInAd = 20,
            iconRes = R.drawable.img_superrocket
        ),
        InventoryItem(
            id = "u_turn",
            name = context.getString(R.string.item_u_turn_name),
            quantity = 1 / 3f,
            description = context.getString(R.string.item_u_turn_desc),
            costInStars = 3,
            hasAdPurchaseOption = true,
            lottieAssetPath = "animation_turn.json",
            costInAd = 30,
            iconRes = R.drawable.img_turn_svoy
        ),
        InventoryItem(
            id = "reverse_target",
            name = context.getString(R.string.item_reverse_target_name),
            quantity = 1 / 3f,
            description = context.getString(R.string.item_reverse_target_desc),
            costInStars = 3,
            hasAdPurchaseOption = true,
            lottieAssetPath = "anim_yak.json",
            costInAd = 30,
            iconRes = R.drawable.img_boevoy_razvorot_vrag
        ),
    )
}


data class InventoryItem(
    val id: String,
    val name: String,
    val quantity: Float, // сколько звезд за одну штуку
    val description: String,
    val costInStars: Int,
    val hasAdPurchaseOption: Boolean,
    val animation: (@Composable () -> Unit)? = null,
    val details: List<String> = emptyList(),
    val lottieAssetPath: String? = null,
    val costInAd: Int = 1,
    val iconRes: Int? = null,
)

fun getShopCount(item: InventoryItem, shopState: ShopState): Int {
    val count = when (item.id) {
        "stars" -> shopState.starCount
        "avada" -> shopState.avadaCount
        "ammo" -> shopState.ammoCount
        "afterburner" -> shopState.forsagCount
        "shield" -> shopState.shieldCount
        "missile_sector" -> shopState.homingCount
        "missile_any" -> shopState.superRocketCount
        "u_turn" -> shopState.turnCount
        "reverse_target" -> shopState.reverseCount
        else -> shopState.starCount
    }
    return count
}
