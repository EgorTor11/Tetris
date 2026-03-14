package com.dogfight.magic.game_ui.widgets.animations

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.toArgb
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback


fun DrawScope.drawLottieAnimation(
    composition: LottieComposition?,
    progress: Float,
    position: Offset,
    baseSize: Float,
    fighterAngle: Float,
    tint: Color? = null,
    isLeft: Boolean=false,
    scale: Float = 1f, // Новый параметр для зума
    centerOffset: Float = 0.0f, // смещения центра в сторону носа самолетика, но ябуду юзать только при взрыве
) {
    if (composition == null) return

    val drawable = LottieDrawable().apply {
        this.composition = composition
        setProgress(progress)
    }

    if (tint!=null)
    createColoredLottieDrawable(drawable, tint)

    val scaledSize = baseSize * scale // Масштабируем размер
    val bitmap = Bitmap.createBitmap(scaledSize.toInt(), scaledSize.toInt(), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    drawable.setBounds(0, 0, scaledSize.toInt(), scaledSize.toInt())
    drawable.draw(canvas)

    val adjustedAngle = if(!isLeft)fighterAngle - 90f  else fighterAngle + 90f
    val noseOffset = Offset(0f, -scaledSize * centerOffset)// смещение центра в сторону носа самолета , выглядит криво при разворотах, оставляю ноль

    withTransform({
        rotate(
            adjustedAngle,
            pivot = position + noseOffset
        )
    }) {
        drawImage(
            bitmap.asImageBitmap(),
            topLeft = position - Offset(scaledSize / 2, scaledSize / 2) + noseOffset
        )
    }
}





fun createColoredLottieDrawable(
    drawable: LottieDrawable,
    color: Color
) {
    val keyPath = KeyPath("**") // Применить цвет ко всей анимации
    val colorCallback = LottieValueCallback(color.toArgb())

    // Меняем цвет в LottieDrawable
    drawable.addValueCallback(keyPath, LottieProperty.COLOR, colorCallback)
}
