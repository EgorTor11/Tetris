package com.dogfight.magic.game_ui.radar.upravlenie.avia_gorizont

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AttitudeIndicator(roll: Float, course: Float, modifier: Modifier = Modifier) {
    val textMeasurer = rememberTextMeasurer()
    Box(modifier = Modifier.clip(RoundedCornerShape(40))) {
        Canvas(modifier = modifier.size(200.dp)) {
            val width = size.width
            val height = size.height
            val centerX = width / 2
            val centerY = height / 2

            /*            // Рисуем небо (верхняя часть - голубая)
                    drawRect(
                        color = Color.Transparent,//Color(0xFF87CEEB),
                        size = Size(width, height / 2)
                    )*/
// Отображаем курс сверху
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    "${course.toInt()}°",
                    width / 2,
                    height * 0.50f,
                    Paint().apply {
                        color = android.graphics.Color.GREEN
                        textSize = 40f
                        textAlign = Paint.Align.CENTER
                    })
            }
            /*            // Рисуем землю (нижняя часть - желтоватая)
                    drawRect(
                        color = Color.Transparent,//Color(0xFFE6D36A),
                        topLeft = Offset(0f, height / 2),
                        size = Size(width, height / 2)
                    )*/

            // Рисуем самолетик с учетом крена
            rotate(roll, pivot = Offset(centerX, centerY)) {
                val wingLength = width * 0.3f
                val arcRadius = width * 0.1f

                drawLine(
                    color = Color.Green,
                    start = Offset(centerX - wingLength, centerY),
                    end = Offset(centerX - arcRadius, centerY),
                    strokeWidth = 5f
                )

                drawArc(
                    color = Color.Green,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(centerX - arcRadius, centerY - arcRadius),
                    size = Size(arcRadius * 2, arcRadius * 2),
                    style = Stroke(width = 5f)
                )

                drawLine(
                    color = Color.Green,
                    start = Offset(centerX + arcRadius, centerY),
                    end = Offset(centerX + wingLength, centerY),
                    strokeWidth = 5f
                )
            }

            // Рисуем шкалу крена
            val scaleRadius = width * -0.35f
            val majorAngles = listOf(0f, -30f, -60f, -90f, 30f, 60f, 90f)
            val labels =
                listOf("90", "60", "30", "", "60", "30", "") // Исправленный порядок подписей

            // Рисуем основные деления с цифрами
            majorAngles.zip(labels).forEach { (angle, label) ->
                val radian = Math.toRadians(angle.toDouble())
                val tickX = centerX + scaleRadius * Math.sin(radian).toFloat()
                val tickY = centerY - scaleRadius * Math.cos(radian).toFloat()

                drawLine(
                    color = Color.Green,
                    start = Offset(tickX, tickY),
                    end = Offset(tickX, tickY + 10),
                    strokeWidth = 3f
                )

                if (label.isNotEmpty()) {
                    drawText(
                        textMeasurer,
                        text = label,
                        topLeft = Offset(tickX - 10, tickY + 15),
                        style = TextStyle(fontSize = 7.sp, color = Color.Green)
                    )
                }
            }

            // Рисуем дополнительные деления каждые 15 градусов (без цифр)
            val minorAngles =
                (-90..90 step 15).filter { it % 30 != 0 } // Убираем углы 30, 60, 90

            minorAngles.forEach { angle ->
                val radian = Math.toRadians(angle.toDouble())
                val tickX = centerX + scaleRadius * Math.sin(radian).toFloat()
                val tickY = centerY - scaleRadius * Math.cos(radian).toFloat()

                drawLine(
                    color = Color.Green,
                    start = Offset(tickX, tickY),
                    end = Offset(tickX, tickY + 5), // Короче чем основные
                    strokeWidth = 2f
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewAttitudeIndicator() {
    AttitudeIndicator(roll = 30f, 0f)
}
