package com.dogfight.magic.game_ui.radar.upravlenie.buttons

import android.net.Uri
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.dogfight.magic.game_ui.radar.mvi_radar.RadarViewModel


data class NeonButtonModel(
    val id: Int,
    val text: String,
    val shape: Shape,
    val neonColor: Color,
    val backgroundColor: Color,
    val isSelected: Boolean = false,
    val imageUri: Uri? = null, // ← если не null, отображаем изображение
)

val neonButtonPresets = listOf(
    NeonButtonModel(0, "🎯 Точечный", RoundedCornerShape(8.dp), Color.Red, Color(0xFF330000)),
    NeonButtonModel(1, "\uD83D\uDCA3🔥", CircleShape, Color.Cyan, Color(0xFF002233)),
    NeonButtonModel(2, "💥 Пуск", RoundedCornerShape(20.dp), Color(0xFF00FFAA), Color(0xFF003322)),
    NeonButtonModel(3, "⚡ Блиц", RoundedCornerShape(50), Color.Magenta, Color(0xFF220022)),
    NeonButtonModel(4, "🚀 Ускорение", RoundedCornerShape(12.dp), Color.Green, Color(0xFF003311)),

)
val defaultFireButtonModel =
    NeonButtonModel(1, "\uD83D\uDCA3🔥", CircleShape, Color.Cyan, Color(0xFF002233))

fun filteredButtonsList(selectedFireButton: NeonButtonModel) =
    neonButtonPresets.filter { it.id != selectedFireButton.id }

@Composable
fun NeonButtonsRow(
    viewModel: RadarViewModel,
    onElementClick: (NeonButtonModel) -> Unit,
    content: @Composable (Modifier) -> Unit,
) {
    val screenState by viewModel.screenState.collectAsState()
    LazyRow {
        item {
            content.invoke(Modifier)
        }
        if (screenState.selectedFireButton != null && filteredButtonsList(screenState.selectedFireButton!!).isNotEmpty())
            items(filteredButtonsList(screenState.selectedFireButton!!)) { button ->
                NeonFireButton(
                    text = button.text,
                    shape = button.shape,
                    glowColor = button.neonColor,
                    backgroundColor = button.backgroundColor,
                    imageUri = button.imageUri
                ) {
                   onElementClick.invoke(button)
                }
            }
    }
}


