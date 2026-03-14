package com.dogfight.magic.game_ui.radar.upravlenie.avia_gorizont

import android.content.Context
import android.util.Log
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.dogfight.magic.game_ui.radar.upravlenie.super_container.DragContainer
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin


@Composable
fun ElonHorizon(
    torsoImage: ImageBitmap?,
    elonModifier: Modifier = Modifier,
    sizeFactor: Int=1,
) {
    Box(
        modifier = elonModifier
            .size(55.dp*sizeFactor) // размер аватара
            .clip(RoundedCornerShape(16.dp))
    ) {
        // 📸 Тело Илона
        if (torsoImage != null)
            Image(
                bitmap = torsoImage,
                contentDescription = "Elon Torso",
                modifier = Modifier.fillMaxSize()
            )
    }
}


@Composable
fun SuperRotatingHand(
    course: Float,
    /*handPainter: Painter,*/
    handImage: ImageBitmap?,
    modifier: Modifier = Modifier,
    sizeFactor: Int = 1,
) {
    val angle = remember(course) { course - 90 }

    Box(
        modifier = modifier
            .size(48.dp*sizeFactor) // Размер внешнего контейнера
            .background(Color.Transparent),
        contentAlignment = Alignment.TopStart // Важно! Привязка к верхнему левому углу
    ) {
        // Сдвигаем изображение руки так, чтобы "плечо" было в
        if (handImage != null)
            Image(
                bitmap = handImage,
                contentDescription = "Rotating Hand",
                modifier = Modifier
                    /* .offset(
                         x = (35).dp,
                         y = (35).dp
                     )*/ // подбери значения так, чтобы плечо было в (0, 0)
                    .graphicsLayer(
                        rotationZ = angle,
                        transformOrigin = TransformOrigin(
                            0.02f,
                            0.5f
                        ) // Вращаем относительно правого нижнего угла руки
                    )
            )else  Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Rotating Hand",
            tint = Color.Green,
            modifier = Modifier
                /* .offset(
                     x = (35).dp,
                     y = (35).dp
                 )*/ // подбери значения так, чтобы плечо было в (0, 0)
                .graphicsLayer(
                    rotationZ = angle,
                    transformOrigin = TransformOrigin(
                        0.02f,
                        0.5f
                    ) // Вращаем относительно правого нижнего угла руки
                )
        )
    }
}

@Composable
fun EnemyAzimuthRing(
    azimuth: Float, // 0..360, 0 = вверх
    enemyImage: ImageBitmap?,
    /*ringRadius: Dp = 50.dp,
    enemySize: Dp = 32.dp,*/
    enemyAzimuthModifier: Modifier = Modifier,
    sizeFactor: Int = 1,
) {
    val ringRadius = 45.dp * sizeFactor
    val enemySize: Dp = 28.dp * sizeFactor
    val density = LocalDensity.current
    val radiusPx = with(density) { ringRadius.toPx() }

    Box(
        modifier = enemyAzimuthModifier
            .size(ringRadius * 2 + enemySize) // учёт иконки
            .drawBehind {
                // Рисуем кольцо
                drawCircle(
                    color = Color.Green,
                    radius = radiusPx,
                    center = center,
                    style = Stroke(width = 2 .dp.toPx())
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Переводим азимут в радианы, 0° = вверх
        val angleRad = Math.toRadians(azimuth.toDouble() - 90.0)

        // Расчёт позиции врага на окружности
        val offsetX = cos(angleRad).toFloat() * radiusPx
        val offsetY = sin(angleRad).toFloat() * radiusPx
        if (enemyImage != null)
            Image(
                bitmap = enemyImage,
                contentDescription = "Enemy",
                modifier = Modifier
                    .size(enemySize)
                    .graphicsLayer {
                        translationX = offsetX
                        translationY = offsetY
                    }
            )else    Icon(
           imageVector = Icons.Default.AirplanemodeActive,
            contentDescription = "Enemy",
            modifier = Modifier
                .size(enemySize)
                .graphicsLayer {
                    translationX = offsetX
                    translationY = offsetY
                },
                tint = Color.Red
        )
    }
}

@Composable
fun Pricel(
    azimuth: Float, // 0..360, 0 = вверх,
    heading: Float, // 0..360
    pricelmodifier: Modifier = Modifier,
    bodyBitmap: ImageBitmap?,
    handBitmap: ImageBitmap?,
    enemyBitmap: ImageBitmap?,
    sizeFactor: Int = 1,
) {

    Box(contentAlignment = Alignment.Center, modifier = pricelmodifier.clip(CircleShape)) {
        CoordinateContainer(prefsKey = "ElonHorizon") {
            ElonHorizon(bodyBitmap)
        }
        CoordinateContainer(prefsKey = "SuperRotatingHand") {
            SuperRotatingHand(course = heading, handBitmap)
        }
        /*   Box(
               modifier = Modifier
                   .size(5.dp)
                   .background(Color.Red)
           )*/
        EnemyAzimuthRing(azimuth,  enemyBitmap/* ringRadius, enemySize*/)
    }
}

@Composable
fun PricelSettings(
    azimuth: Float, // 0..360, 0 = вверх,
    heading: Float, // 0..360
    torsoImage: ImageBitmap?,
    handImage: ImageBitmap?,
    enemyImage: ImageBitmap?,
    pricelmodifier: Modifier = Modifier,
    sizeFactor: Int = 1,
) {
    Box(contentAlignment = Alignment.Center, modifier = pricelmodifier.clip(CircleShape)) {
        DragContainer(prefsKey = "ElonHorizon", onClick = {}) {
            ElonHorizon(torsoImage,sizeFactor=sizeFactor, elonModifier = it)
        }
        DragContainer(prefsKey = "SuperRotatingHand", onClick = {}, portraitOffset =  Offset(63.5f,38.68f) ) {
            SuperRotatingHand(course = heading, handImage,sizeFactor=sizeFactor, modifier = it)
        }
        Box(
            modifier = Modifier
                .size(5.dp)
                .background(Color.Red)
        )
        EnemyAzimuthRing(azimuth, enemyImage,sizeFactor=sizeFactor/* ringRadius, enemySize*/)
    }
}

@Composable
fun CoordinateContainer(
    sizFactor: Int = 1,
    prefsKey: String = "Elon",
    content: @Composable (Modifier) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val context = LocalContext.current
    val orientationKey = if (isLandscape) "landscape" else "portrait"
    val fullPrefsKey = "$prefsKey-$orientationKey"
    val prefs = remember { context.getSharedPreferences(fullPrefsKey, Context.MODE_PRIVATE) }

    val defaultOffsetX = 63.5f * sizFactor
    val defaultOffsetY = 38.68f * sizFactor


    // Загружаем сохраненные координаты или устанавливаем дефолтные
    var offsetX by remember { mutableStateOf(prefs.getFloat("offsetX", defaultOffsetX)) }
    var offsetY by remember { mutableStateOf(prefs.getFloat("offsetY", defaultOffsetY)) }


    Log.d("offset", "$fullPrefsKey  offsetX= $offsetX offsetY=$offsetY")

    var isModifyMode by remember { mutableStateOf(false) }

    // Анимация для неонового переливания
    val transition = rememberInfiniteTransition()
    val borderColor by transition.animateColor(
        initialValue = Color(0xFF00FF00), // Начальный цвет (зеленый)
        targetValue = Color(0xFFFF00FF), // Целевой цвет (пурпурный)
        /*        initialValue = Color(0xFF00FFF0),
                targetValue = Color(0xFF0055FF),*/
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000, // длительность анимации 2 секунды
                easing = FastOutSlowInEasing // плавный переход
            ),
            repeatMode = RepeatMode.Reverse // повтор с обратным эффектом
        )
    )
    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
    ) {
        val innerModifier by remember(isModifyMode) {
            derivedStateOf {
                Modifier
                    .border(
                        border = BorderStroke(
                            if (isModifyMode) 4.dp else 0.dp,
                            if (isModifyMode) borderColor else Color.Transparent
                        ), shape = RoundedCornerShape(16.dp)
                    )
                    .padding(10.dp)
            }
        }
        content.invoke(innerModifier)
    }
}