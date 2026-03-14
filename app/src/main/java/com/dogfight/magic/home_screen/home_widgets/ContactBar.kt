package com.dogfight.magic.home_screen.home_widgets

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalPostOffice
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dogfight.magic.R

@Composable
fun ContactBar(
    email: String,
    tiktokUrl: String,
    playStoreUrl: String,
    telegramUrl: String? = null,
    instagramUrl: String? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Почта
        Icon(
            //painter = painterResource(R.drawable.ic_1), // свой икон
            imageVector = Icons.Default.LocalPostOffice,
            contentDescription = "Email",
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:$email")
                    }
                    context.startActivity(intent)
                },
            tint = Color.Blue
        )

        // TikTok
        Icon(
            painter = painterResource(R.drawable.img_tik_tok), // TikTok logo
            contentDescription = "TikTok",
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(tiktokUrl))
                    context.startActivity(intent)
                },
            tint = Color.Blue
        )



        // Telegram (если указан)
        telegramUrl?.let {
            Icon(
              //  painter = painterResource(R.drawable.ic_3),
                painter = painterResource(R.drawable.img_telegram),
                contentDescription = "Telegram",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                        context.startActivity(intent)
                    },
                tint = Color.Blue
            )
        }

        // Instagram (если указан)
        instagramUrl?.let {
            Icon(
                painter = painterResource(R.drawable.img_instagram),
                contentDescription = "Instagram",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                        context.startActivity(intent)
                    },
                tint = Color.Blue
            )
        }
        // Поделиться
        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Share",
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, playStoreUrl)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "Поделиться ссылкой")
                    context.startActivity(shareIntent)
                },
            tint = Color.Blue
        )
    }
}
