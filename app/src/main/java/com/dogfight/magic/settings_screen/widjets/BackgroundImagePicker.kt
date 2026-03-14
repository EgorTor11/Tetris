package com.dogfight.magic.settings_screen.widjets

import android.content.ContentResolver
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.dogfight.magic.R
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@Composable
fun BackgroundChooser(
    selectedUri: Uri?,
    selectedColor: Color?,
    onBackgroundSelected: (Uri?) -> Unit,
    onColorSelected: (Color?) -> Unit,
) {
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    var previewUri by remember { mutableStateOf(selectedUri) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        /* uri?.let {
             // Сохраняем разрешение на доступ
             try {
                 contentResolver.takePersistableUriPermission(
                     it,
                     Intent.FLAG_GRANT_READ_URI_PERMISSION
                 )
             } catch (e: SecurityException) {
                 e.printStackTrace()
             }

             // Передаём выбранный URI
             onBackgroundSelected(it)
         }*/
        // Удаляем все предыдущие фоновые изображения
        val dir = context.filesDir
        dir.listFiles()?.forEach { file ->
            if (file.name.startsWith("selected_background_")) {
                file.delete()
            }
        }

        uri?.let {
            // Копируем URI во внутреннее хранилище
            val inputStream = context.contentResolver.openInputStream(it)
            //  val fileName = "selected_background.jpg"
            val fileName = "selected_background_${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, fileName)
            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }

            val localUri = Uri.fromFile(file)
            previewUri = localUri
            onBackgroundSelected(localUri)
        }
    }

    val scrollState = rememberScrollState()
    val previewKey = remember(previewUri) { UUID.randomUUID().toString() }
    var color by remember { mutableStateOf(selectedColor ?: Color.White) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(stringResource(R.string.game_screen_background), fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Color.Gray)
                .border(1.dp, Color.Black)
        ) {
            when {
                selectedUri != null && selectedUri !="null".toUri() -> {
                    AsyncImage(
                        model = selectedUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    /*                    AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(previewUri?.toString() + "?key=$previewKey") // добавляем уникальный ключ
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )*/

                }

          /*      selectedColor != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(selectedColor)
                    )
                }*/

                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color)
                    )
                  //  Text("Фон не выбран", modifier = Modifier.align(Alignment.Center))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(stringResource(R.string.select_background), fontWeight = FontWeight.Bold)

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf(
                R.drawable.img_bg_kosmos,
                R.drawable.img_bg_more,
                R.drawable.img_bg_nebo,
            ).forEach { resId ->
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            val uri = Uri.parse(
                                ContentResolver.SCHEME_ANDROID_RESOURCE +
                                        "://${context.packageName}/" + resId
                            )
                            onBackgroundSelected(uri)
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Icon(Icons.Default.Image, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.select_from_gallery))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.or_set_the_background_color))
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color)
                    .border(1.dp, Color.Black)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier
                .size(16.dp)
                .background(Color.Red))
            Spacer(modifier = Modifier.width(16.dp))
            Slider(
                value = color.red,
                onValueChange = { red ->
                    color = Color(red, color.green, color.blue)
                    onColorSelected(color)
                    onBackgroundSelected(null)
                },
                valueRange = 0f..1f,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier
                .size(16.dp)
                .background(Color.Green))
            Spacer(modifier = Modifier.width(16.dp))
            Slider(
                value = color.green,
                onValueChange = { green ->
                    color = Color(color.red, green, color.blue)
                    onColorSelected(color)
                    onBackgroundSelected(null)
                },
                valueRange = 0f..1f,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier
                .size(16.dp)
                .background(Color.Blue))
            Spacer(modifier = Modifier.width(16.dp))
            Slider(
                value = color.blue,
                onValueChange = { blue ->
                    color = Color(color.red, color.green, blue)
                    onColorSelected(color)
                    onBackgroundSelected(null)
                },
                valueRange = 0f..1f,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
