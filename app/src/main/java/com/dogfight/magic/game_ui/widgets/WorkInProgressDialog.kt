package com.dogfight.magic.game_ui.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dogfight.magic.R
import com.dogfight.magic.home_screen.home_widgets.ContactBar

@Composable
fun WorkInProgressDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            tonalElevation = 30.dp,
            modifier = Modifier
                .wrapContentSize()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(300.dp)) {
                Box(
                    modifier = Modifier
                        .size(300.dp),
                ) {
                    // Центрируем контент
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Загрузка анимации из ресурсов
                        val composition by rememberLottieComposition(LottieCompositionSpec.Asset("animation_developer.json"))
                        val progress by animateLottieCompositionAsState(composition)

                        LottieAnimation(
                            composition = composition,
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }


                }
                // Текст поверх или ниже анимации
                Text(
                    //  text = "Ещё не готово — разработчик очень старается и спешит!",
                    text = stringResource(R.string.online_not_redy_title) ,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .fillMaxWidth().padding(top=6.dp)
                        ,
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                )

                Text(
                    text = stringResource(R.string.share_and_show_update),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp), // сместить ниже анимации
                    textAlign = TextAlign.Center,
                )
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                    ContactBar(
                        email = "magicaldogfight@gmail.com",
                        /*tiktokUrl = "https://www.tiktok.com/@yourappdemo",*/
                        tiktokUrl = "https://www.tiktok.com/@magical.dogfight?_t=ZM-8wZC21HGEWT&_r=1",
                        playStoreUrl = "https://play.google.com/store/apps/details?id=com.dogfight.magic",
                        telegramUrl = "https://t.me/magicaldogfight", // можно null
                        instagramUrl = null, // можно null
                        modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
                    )
                    Text(stringResource(R.string.close), modifier = Modifier.clickable{
                        onDismiss()
                    }.padding(6.dp), color = Color.Red)
                }
            }

        }
    }
}
