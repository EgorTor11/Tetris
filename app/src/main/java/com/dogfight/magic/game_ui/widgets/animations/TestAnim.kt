package com.dogfight.magic.game_ui.widgets.animations
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieDrawable

fun DrawScope.drawLottieAnimation2(
    composition: LottieComposition?,
    progress: Float,
    position: Offset,
    baseSize: Float,
    fighterAngle: Float,
    tint: Color? = null,
    isLeft: Boolean = false,
    scale: Float = 1f,
    centerOffset: Float = 0.0f,
    flipX: Boolean = false // Новый параметр для отражения
) {
    if (composition == null) return

    val drawable = LottieDrawable().apply {
        this.composition = composition
        setProgress(progress)
    }

    if (tint != null)
        createColoredLottieDrawable(drawable, tint)

    val scaledSize = baseSize * scale
    val bitmap = Bitmap.createBitmap(scaledSize.toInt(), scaledSize.toInt(), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    drawable.setBounds(0, 0, scaledSize.toInt(), scaledSize.toInt())
    drawable.draw(canvas)

    val adjustedAngle = if (!isLeft) fighterAngle - 90f else fighterAngle + 90f
    val noseOffset = Offset(0f, -scaledSize * centerOffset)

    withTransform({
        rotate(adjustedAngle, pivot = position + noseOffset)
        scale(scaleX = if (flipX) -1f else 1f, scaleY = 1f, pivot = position + noseOffset) // Отражение по X
    }) {
        drawImage(
            bitmap.asImageBitmap(),
            topLeft = position - Offset(scaledSize / 2, scaledSize / 2) + noseOffset
        )
    }
}
