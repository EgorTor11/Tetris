package com.dogfight.magic.game_ui.radar.upravlenie.super_container

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun DragContainer(
    prefsKey: String = "fire_button_prefs",
    isCenterDrop: Boolean = false,
    portraitOffset: Offset = Offset.Zero,
    landscapeOffset: Offset = Offset.Zero,
    onClick: () -> Unit,
    onDoubleTap: () -> Unit = {},
    onChangeIsCenterDrop: (Boolean) -> Unit = {},
    content: @Composable (Modifier) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE


    val context = LocalContext.current
    /*val prefs = remember { context.getSharedPreferences(prefsKey, Context.MODE_PRIVATE) }*/

    val orientationKey = if (isLandscape) "landscape" else "portrait"
    val fullPrefsKey = "$prefsKey-$orientationKey"
    val prefs = remember { context.getSharedPreferences(fullPrefsKey, Context.MODE_PRIVATE) }

    val density = LocalDensity.current


    // Размер экрана для установки дефолтного положения
    val screenHeight = remember { context.resources.displayMetrics.heightPixels.toFloat() }

    /*// Дефолтное положение: слева внизу с отступами 20.dp
    val defaultOffsetX = with(density) { 20.dp.toPx() }

    val defaultOffsetY =
        if (withDefaultOffsetY) screenHeight - with(density) { 100.dp.toPx() } else 0f// Отнимаем высоту кнопки + отступ*/

    val offset = if (isLandscape) landscapeOffset else portraitOffset


    val defaultOffsetX = offset.x
    val defaultOffsetY = offset.y


    // Загружаем сохраненные координаты или устанавливаем дефолтные
    var offsetX by remember { mutableStateOf(prefs.getFloat("offsetX", defaultOffsetX)) }
    var offsetY by remember { mutableStateOf(prefs.getFloat("offsetY", defaultOffsetY)) }
    if (isCenterDrop){
        offsetX= centerCoordinatesDemo().x
        offsetY= centerCoordinatesDemo().y
        prefs.edit()
            .putFloat("offsetX", offsetX)
            .putFloat("offsetY", offsetY)
            .apply()
    }

    Log.d("offset", "$fullPrefsKey  offsetX= $offsetX offsetY=$offsetY")

    var isModifyMode by remember { mutableStateOf(false) }
    var isDragging by remember { mutableStateOf(false) }
    var hasDragged by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    var longPressJob by remember { mutableStateOf<Job?>(null) }

    // Функция для сохранения позиции
    fun savePosition() {
        prefs.edit()
            .putFloat("offsetX", offsetX)
            .putFloat("offsetY", offsetY)
            .apply()
    }
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
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (isModifyMode) {
                            isModifyMode = false
                        } else {
                            onClick()
                        }
                    },
                    onLongPress = {
                        isModifyMode = true
                        isDragging = true
                        hasDragged = false

                        // Вибрация при лонгтапе
                        val vibrator =
                            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        if (vibrator.hasVibrator()) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(
                                    VibrationEffect.createOneShot(
                                        50,
                                        VibrationEffect.DEFAULT_AMPLITUDE
                                    )
                                )
                            } else {
                                @Suppress("DEPRECATION")
                                vibrator.vibrate(50)
                            }
                        }

                        // Таймер для выключения кольца, если не началось перетаскивание
                        longPressJob?.cancel()
                        longPressJob = coroutineScope.launch {
                            delay(1000)
                            if (!hasDragged && !isDragging) {
                                isModifyMode = false
                            }
                        }
                    },
                    onDoubleTap = {
                        onDoubleTap.invoke()
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        isDragging = true
                        hasDragged = true
                        longPressJob?.cancel()
                        isModifyMode = true
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    },
                    onDragEnd = {
                        isDragging = false
                        isModifyMode = false
                        onChangeIsCenterDrop(false)
                        savePosition() // Сохраняем координаты
                    }
                )
            }
    ) {
        val innerModifier by remember(isModifyMode) {
            derivedStateOf {
                Modifier
                    /*.border(
                        width = if (isModifyMode) 4.dp else 0.dp,
                        color = if (isModifyMode) Color.Magenta else Color.Transparent,
                        shape = RoundedCornerShape(16.dp)//CircleShape
                    )*/
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

@Composable
fun centerCoordinatesDemo(): Offset {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val density = LocalDensity.current
    val centerX: Float
    val centerY: Float

    with(density) {
        centerX = screenWidth.toPx() / 2
        centerY = screenHeight.toPx() / 2
    }
return Offset(centerX,centerY)
}