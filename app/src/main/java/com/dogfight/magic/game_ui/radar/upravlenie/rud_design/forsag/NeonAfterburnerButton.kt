package com.dogfight.magic.game_ui.radar.upravlenie.rud_design.forsag

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dogfight.magic.R
import com.dogfight.magic.game_ui.radar.mvi_radar.RadarViewModel

@Composable
fun NeonAfterburnerButton(
    iconRes: Int = R.drawable.img_forsag,
    viewModel: RadarViewModel,
    onShowAdRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.screenState.collectAsState()
    val isActive = state.isAfterburnerActive
    val remaining = state.forsagCount
    val progress = state.afterburnerProgress

    Box(
        modifier = Modifier
            .clickable {
                if (!isActive && remaining <= 0) {
                    onShowAdRequest() // показать рекламу если закончилось
                } else {
                    viewModel.toggleAfterburner()
                }

            }
            .size(64.dp)
            .background(
                Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 2.dp,
                color =  Color.Cyan ,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    )
    {
        Image(
            painterResource(iconRes),
            "",
            Modifier
                .alpha(0.5f)
                .align(Alignment.CenterStart)
                .padding(1.dp)
                .clip(RoundedCornerShape(13.dp))
                .size(64.dp),
            contentScale = ContentScale.Crop
        )
        Text(
            remaining.toString(),
            fontSize = 14.sp,
            color = Color(0xFF00FFF0),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(4.dp)
                .background(Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(4.dp))
                .align(Alignment.BottomEnd)

        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(2.dp)
                .clip(RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.BottomStart
        ) {
                    Box(
                        modifier = Modifier.padding(start = 0.5.dp)
                            .height(24.dp)
                            .fillMaxWidth(progress)
                           .clip(RoundedCornerShape(2.dp))//CircleShape)
                                .background( color = Color(0xFF00FFF0).copy(alpha = 0.5f))
                    )
                }
    }
    /* Column(horizontalAlignment = Alignment.CenterHorizontally) {
         Box(
             modifier = modifier
                 .size(width = 64.dp, height = 64.dp)
                 .clip(*//*RoundedCornerShape(8.dp)*//*CircleShape)
                .background(Color(0xFF111133))
                .border(
                    width = 2.dp,
                    color = if (isActive) Color.Cyan else Color.Gray,
                    shape = CircleShape
                )
                .clickable {
                 *//*   if (!isActive && remaining <= 0) {
                        onShowAdRequest() // показать рекламу если закончилось
                    } else {
                        viewModel.toggleAfterburner()
                    }*//*
                    viewModel.toggleAfterburner()
                },
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(2.dp)
                    .clip(*//*RoundedCornerShape(6.dp)*//*CircleShape)
                //    .background(Color.Red.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .clip(*//*RoundedCornerShape(6.dp)*//*CircleShape)
                        .background(Color(0xFF00FFAA))
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
               Text(
                    text = "🚀 x7",
                    color = Color.White,
                    fontSize = 14.sp
                )
                AfterburnerCount(count = remaining)
            }
        }

        // Счётчик форсажей
        //  ForsagCount(count = remaining)
        //   AfterburnerCount(count = remaining)
    }*/
}

@Composable
fun AfterburnerCount(count: Int, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (count > 10) {
            Text(text = count.toString(), color = Color.Green, fontSize = 12.sp)
        }
        repeat(10) {
            Box(
                modifier = modifier
                    .size(width = 2.dp, height = 10.dp)
                    .background(
                        if (it >= count) Color.Red else Color.Green,
                        shape = RoundedCornerShape(1.dp)
                    )
            )
        }
    }
}