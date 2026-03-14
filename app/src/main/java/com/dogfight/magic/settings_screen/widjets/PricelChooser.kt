package com.dogfight.magic.settings_screen.widjets

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BackupTable
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.dogfight.magic.R
import com.dogfight.magic.game_ui.radar.upravlenie.avia_gorizont.PricelSettings
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@Composable
fun PricelChooser(
    selectedScreenBgUri: Uri?,
    /* selectedBodyUri: Uri?,
     selectedHandUri: Uri?,
     selectedEnemyUri: Uri?,*/
    selectedColor: Color?,
    /* onBackgroundSelected: (Uri?) -> Unit,
     onColorSelected: (Color?) -> Unit,*/
) {
    val context = LocalContext.current
    var currentChooseType by rememberSaveable { mutableStateOf(0) }
    var bodyUri by remember { mutableStateOf("".toUri()) }
    var handUri by remember { mutableStateOf("".toUri()) }
    var enemyUri by remember { mutableStateOf("".toUri()) }
    LaunchedEffect(Unit) {
        val dir = context.filesDir
        dir.listFiles()?.forEach { file ->
            when {
                file.name.startsWith("selected_hand_") -> handUri = Uri.fromFile(file)
                file.name.startsWith("selected_body_") -> bodyUri = Uri.fromFile(file)
                file.name.startsWith("selected_enemy_") -> enemyUri = Uri.fromFile(file)
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        // Удаляем все предыдущие фоновые изображения

        val strImagesKey = when (currentChooseType) {
            0 -> "selected_hand_"
            1 -> "selected_body_"
            2 -> "selected_enemy_"
            else -> "selected_hand_"
        }
        val dir = context.filesDir
        dir.listFiles()?.forEach { file ->
            if (file.name.startsWith(strImagesKey)) {
                file.delete()
            }
        }

        uri?.let {
            // Копируем URI во внутреннее хранилище
            val inputStream = context.contentResolver.openInputStream(it)
            //  val fileName = "selected_background.jpg"
            val fileName = "$strImagesKey${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, fileName)
            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }

            val localUri = Uri.fromFile(file)
            //  onBackgroundSelected(localUri)

            when (currentChooseType) {
                0 -> {
                    handUri = localUri
                }

                1 -> {
                    bodyUri = localUri
                }

                2 -> {
                    enemyUri = localUri
                }
            }
        }
    }

    var color by remember { mutableStateOf(selectedColor ?: Color.White) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
        stringResource(R.string.direction_sight),
        fontWeight = FontWeight.Bold
    )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color.Gray)
                .border(1.dp, Color.Black),
            contentAlignment = Alignment.Center
        ) {
            when {
                selectedScreenBgUri != null && selectedScreenBgUri != "null".toUri() -> {
                    AsyncImage(
                        model = selectedScreenBgUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                }

                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color)
                    )
                    //  Text("Фон не выбран", modifier = Modifier.align(Alignment.Center))
                }
            }

            PricelSettings(
                heading = 0f,
                azimuth = 270f,
                torsoImage = uriToImageBitmap(context, bodyUri),
                handImage = uriToImageBitmap(context, handUri),
                enemyImage = uriToImageBitmap(context, enemyUri),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            currentChooseType = 0
            imagePickerLauncher.launch("image/*")
        }) {
            Icon(Icons.Default.Image, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.arrow_from_gallery))
            Icon(Icons.Default.ArrowUpward, contentDescription = null)
        }
        Button(onClick = {
            currentChooseType = 1
            imagePickerLauncher.launch("image/*")
        }) {
            Icon(Icons.Default.Image, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.background_from_gallery))
            Icon(Icons.Default.BackupTable, contentDescription = null)
        }
        Button(onClick = {
            currentChooseType = 2
            imagePickerLauncher.launch("image/*")
        }) {
            Icon(Icons.Default.Image, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.target_from_gallery))
            Icon(Icons.Default.AirplanemodeActive, contentDescription = null)
        }
        Button(onClick = {
            val dir = context.filesDir
            dir.listFiles()?.forEach { file ->
                if (file.name.startsWith("selected_hand_") ||
                    file.name.startsWith("selected_body_") ||
                    file.name.startsWith("selected_enemy_")
                ) {
                    file.delete()
                }
            }
            bodyUri = "".toUri()
            handUri = "".toUri()
            enemyUri = "".toUri()
        }) {
            Icon(Icons.Default.Replay, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.reset))
        }
    }
}

fun uriToImageBitmap(context: Context, uri: Uri?): ImageBitmap? {
    return if (uri != null && uri != "".toUri())
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } else null
}
