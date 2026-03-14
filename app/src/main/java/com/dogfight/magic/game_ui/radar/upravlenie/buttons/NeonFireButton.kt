package com.dogfight.magic.game_ui.radar.upravlenie.buttons

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun NeonFireButton(
    text: String,
    shape: Shape,
    glowColor: Color,
    backgroundColor: Color,
    imageUri: Uri? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    if (imageUri == null)
        Box(
            modifier = modifier
                .padding(8.dp)
                .shadow(20.dp, shape, ambientColor = glowColor, spotColor = glowColor)
                .background(backgroundColor, shape)
                .clip(shape)
                .clickable(onClick = onClick)
                .border(
                    width = 2.dp,
                    color = glowColor,
                    shape = shape
                )
                .padding(horizontal = 24.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    else Box(
        modifier = Modifier
            .size(100.dp)
            .clickable(onClick = onClick)
    ) {
        AsyncImage( // coil-compose
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUri)
                .crossfade(true)
                .build(),
            contentDescription = "Пользовательская кнопка",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
        )
    }
}


@Composable
fun NeonFireButtonWithoutOnClick(
    text: String,
    shape: Shape,
    glowColor: Color,
    backgroundColor: Color,
    imageUri: Uri? = null,
    modifier: Modifier = Modifier,
    ammoCount: Int = 100,
) {
    if (imageUri == null)
        Box(
            modifier = modifier
                .padding(8.dp)
                .shadow(20.dp, shape, ambientColor = glowColor, spotColor = glowColor)
                .background(backgroundColor, shape)
                .clip(shape)
                .border(
                    width = 2.dp,
                    color = glowColor,
                    shape = shape
                )
                .padding(horizontal = 24.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = ammoCount.toString() + text,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    else Box(modifier = Modifier.size(100.dp)) {
        AsyncImage( // coil-compose
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUri)
                .crossfade(true)
                .build(),
            contentDescription = "Пользовательская кнопка",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
        )
    }
}