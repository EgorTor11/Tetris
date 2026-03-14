package com.dogfight.magic.home_screen.shop

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dogfight.magic.R

data class MissionItemData(
    val id: Int,
    val name: String,
    val iconResId: Int,
    val description: String,
    val priceInStars: Int,
    val lottieAsset: String = "",
)

val missionItems = listOf(
    MissionItemData(
        1,
        "Пожар в большом городе",
        R.drawable.img_famely_fire,
        "Путушить пожар и спасти семью от приблежающегося огня",
        3, lottieAsset = "animation_fire_spray.json"
    ),
    MissionItemData(2, "", R.drawable.img_meteor, "", 0),
    MissionItemData(3, "", R.drawable.img_meteor, "", 0),
    MissionItemData(4, "", R.drawable.img_meteor, "", 0),
)

// заглушка для онлайна и метеоритов
val onlineStubItems = listOf(
    MissionItemData(
        1,
        "",
        R.drawable.img_meteor,
        "",
        3, lottieAsset = "animation_developer.json"
    ),
)

@Composable
fun MissionItemCard(item: MissionItemData, onClick: () -> Unit) {
    val context = LocalContext.current
    androidx.compose.material3.Card(
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxSize()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (item.lottieAsset != "") {
                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.Asset(item.lottieAsset)
                    )
                    val progress by animateLottieCompositionAsState(
                        composition,
                        iterations = LottieConstants.IterateForever
                    )
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                    )
                } else {
                    //  Box(modifier = Modifier.fillMaxSize())
                    Image(
                        painter = painterResource(item.iconResId),
                        contentDescription = item.name,
                        modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}
