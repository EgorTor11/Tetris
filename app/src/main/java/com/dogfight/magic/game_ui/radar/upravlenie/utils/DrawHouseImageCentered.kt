package com.dogfight.magic.game_ui.radar.upravlenie.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.max
import kotlin.math.min

fun DrawScope.drawHouseImageCentered(
    image: ImageBitmap,
    position: Offset,
    baseSize: Float,
    scale: Float = 1f,
) {
    val scaledSize = baseSize * scale
    val imageWidth = image.width.toFloat()
    val imageHeight = image.height.toFloat()

    // Равномерное масштабирование, чтобы сохранить пропорции и форму круга
    val uniformScale = scaledSize / max(imageWidth, imageHeight)

    val drawWidth = imageWidth * uniformScale
    val drawHeight = imageHeight * uniformScale

    val topLeft = position - Offset(drawWidth / 2f, drawHeight / 2f)

    // Радиус круга — половина минимальной стороны
    val radius = min(drawWidth, drawHeight) / 2f

    // Центрированная круглая область
    val clipPath = Path().apply {
        addOval(
            Rect(
                center = position,
                radius = radius
            )
        )
    }

    // Масштабирование и обрезка по кругу
    withTransform({
        scale(uniformScale, uniformScale, pivot = position)
    }) {
        clipPath(clipPath) {
            drawImage(
                image = image,
                topLeft = position - Offset(imageWidth / 2f, imageHeight / 2f)
            )
        }
    }
}




