package com.dogfight.magic.home_screen.shop.shop_top_bar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dogfight.magic.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseOptionsRow(
    neonColor: Color = Color.White,
    shopItem: InventoryItem?,
    costInAd: Int = 0,
    availableStars: Int,
    itemName: String,
    itemPerStar: Int = 2,
    onBuyWithStars: (itemCount: Int, usedStars: Int) -> Unit,
    onWatchAd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var starToSpend by remember(availableStars) {
        mutableStateOf(if (availableStars >= 1) 1 else 0)
    }

    val actualStarToSpend = if (availableStars >= 1) {
        starToSpend.coerceIn(1, availableStars)
    } else 0

    var itemCount = actualStarToSpend * itemPerStar
    if (itemCount==0) itemCount= (actualStarToSpend/(shopItem?.quantity ?: 2f)).toInt()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (shopItem?.id != "stars")
        Column( modifier = Modifier
            .clickable { onBuyWithStars(itemCount, actualStarToSpend) }
            .weight(1f)
            .height(100.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            // Кнопка "за звезды"
                Card(
                    modifier = Modifier
                        .clickable { onBuyWithStars(itemCount, actualStarToSpend) }
                        // .weight(1f)
                        .height(72.dp),
                    elevation = CardDefaults.cardElevation(6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(modifier = Modifier
                            .fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (availableStars > 0)
                                    "+$itemCount/ $actualStarToSpend ⭐"
                                else
                                    stringResource(R.string.have_no_stars),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF333333),
                                textAlign = TextAlign.Center
                            )

                        }
                    }
                }
            if (availableStars > 1) {
                Slider(
                    value = actualStarToSpend.toFloat(),
                    onValueChange = {
                        starToSpend = it.toInt().coerceIn(1, availableStars)
                    },
                    valueRange = 1f..availableStars.toFloat(),
                    steps = (availableStars - 2).coerceAtLeast(0),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(24.dp) // общая высота области
                        /*.align(Alignment.BottomCenter)*/,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF007AFF),
                        activeTrackColor = Color(0xFF007AFF),
                        inactiveTrackColor = Color.LightGray
                    ),
                    thumb = {
                        Box(
                            Modifier
                                .size(12.dp) // размер "бегунка"
                                .background(Color(0xFF007AFF), shape = CircleShape)
                        )
                    },
                    track = { positions ->
                        Box(
                            Modifier
                                .height(4.dp) // толщина линии
                                .fillMaxWidth()
                                .background(Color.LightGray, RoundedCornerShape(2.dp))
                        )
                    }
                )

            }
        }


        // Кнопка "за рекламу"
        Card(
            modifier = Modifier
                .weight(1f)
                .height(72.dp)
                .clickable(enabled = true, onClick = onWatchAd),
            elevation = CardDefaults.cardElevation(6.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+$costInAd/\uD83D\uDC40\uD83D\uDCFA",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF007AFF),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

/*    // Кнопка "Купить"
    if (shopItem?.id != "stars")
        if (availableStars > 0) {
            Button(
                onClick = { onBuyWithStars(itemCount, actualStarToSpend) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Text("Получить $itemCount $itemName за $actualStarToSpend ⭐")
            }
        }*/
}
