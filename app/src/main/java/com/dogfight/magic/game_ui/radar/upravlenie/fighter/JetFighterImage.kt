package com.dogfight.magic.game_ui.radar.upravlenie.fighter

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.abs

fun DrawScope.drawFighterImageWithRollEffect(
    image: ImageBitmap,
    position: Offset,
    baseSize: Float,
    fighterAngle: Float,
    rollAngle: Float, // Крен в градусах, положительный — вправо
    isLeft: Boolean = false,
    scale: Float = 1f,
    centerOffset: Float = 0f,
) {
    val scaledSize = baseSize * scale
    val imageWidth = image.width.toFloat()
    val imageHeight = image.height.toFloat()

    val scaleX = scaledSize / imageWidth
    val scaleY = scaledSize / imageHeight

    val noseOffset = Offset(0f, -scaledSize * centerOffset)
    val adjustedAngle = if (!isLeft) fighterAngle else fighterAngle
    if (/*!enableRollEffect*/false) {
        // 🔹 Простой поворот без крена
        withTransform({
            rotate(degrees = adjustedAngle, pivot = position + noseOffset)
            scale(scaleX, scaleY, pivot = position + noseOffset)
        }) {
            drawImage(
                image = image,
                topLeft = position - Offset(imageWidth / 2f, imageHeight / 2f) + noseOffset
            )
        }
        return
    }
    // Нормализуем крен от -45 до 45 в диапазон -1.0 ... 1.0
    val rollNorm = (rollAngle / 45f).coerceIn(-1f, 1f)

    // Сжатие по ширине при любом крене
    val rollFactor = 1f - abs(rollNorm) * 0.3f

    // Сдвиг изображения по X, визуально имитирующий наклон
    val shiftX = rollNorm * (scaledSize * 0.15f) // 15% от размера

    withTransform({
        rotate(degrees = adjustedAngle, pivot = position + noseOffset)
        translate(left = shiftX, top = 0f)
        scale(scaleX * rollFactor, scaleY, pivot = position + noseOffset)
    }) {
        drawImage(
            image = image,
            topLeft = position - Offset(imageWidth / 2f, imageHeight / 2f) + noseOffset
        )
    }
}


