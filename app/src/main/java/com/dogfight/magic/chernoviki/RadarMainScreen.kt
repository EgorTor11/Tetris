/*
package com.taranovegor91.tetris.chernoviki

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.taranovegor91.tetris.game_ui.radar.upravlenie.view_model.ControlViewModel
import com.taranovegor91.tetris.game_ui.widgets.animations.drawLottieAnimation
import com.taranovegor91.tetris.home_screen.GameMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

@Composable
fun RadarMainScreen(viewModel: ControlViewModel, mode: GameMode = GameMode.Training) {
    val rollAngle by viewModel.rollAngle.collectAsState()
    val course by viewModel.course.collectAsState()
    val radarColor = Color.Green
    val beamColor = Color.Cyan
    val targetColor = Color.Cyan
    val fighterColor = Color.Red
    val enemyColor = Color.Yellow
    // pausa
    val isPaused by viewModel.isPaused.collectAsState()

    var scale by remember { mutableStateOf(1.8f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val animatedAngle = remember { Animatable(0f) }
    val velocity = 1f // скорость вращения луча

    var targetPosition by remember { mutableStateOf(Offset.Zero) }
    // позиция взрыва
    var explosionPosition by remember { mutableStateOf(Offset.Zero) }

    var enemyPosition by remember {
        mutableStateOf(
            randomStartPosition(300f)
        )
    }//тек. позиц. врага
    var enemyAngle by remember { mutableStateOf(calculateEnemyAngle(enemyPosition)) } //текущий курс врага
    var enemySpeed = 0.4f // скорость врага

    var fighterPosition by remember { mutableStateOf(Offset(0f, 0f)) }
    var fighterAngle by remember { mutableStateOf(0f) }
    var fighterSpeed by remember { mutableStateOf(0.5f) }

    fighterAngle = course

    val fighterTrail = remember { mutableStateListOf<FadingTrailPoint>() }
    val enemyTrail = remember { mutableStateListOf<FadingTrailPoint>() }

    // Список ракет истребителя
    val rockets = remember { mutableStateListOf<Rocket>() }

    // Список ракет врага
    val enemyRockets = remember { mutableStateListOf<Rocket>() }

    // Вспышка при попадании
    var explosion by remember { mutableStateOf<Explosion?>(null) }

    val throttle by viewModel.throttleSlider.collectAsState()
    val throttleFromVoice by viewModel.throttleVoice.collectAsState()

    */
/*    *//*
*/
/*var isPaused by remember { mutableStateOf(false) }*//*
*/
/*
    var countdownTimer by remember { mutableStateOf(120) } // 2 минуты*//*


    */
/*    //обратный отсчет времени
        LaunchedEffect(Unit) {
            while (countdownTimer > 0) {
                delay(1000L)
                if (!isPaused) countdownTimer--
            }
        }*//*


    LaunchedEffect(throttle) {
        // Используем значение throttle для управления скоростью самолета
        fighterSpeed = throttle * 2
    }

    LaunchedEffect(throttleFromVoice) {
        // Используем значение throttle для управления скоростью самолета
        fighterSpeed = throttleFromVoice * 2
    }

    // Анимация луча радара
    LaunchedEffect(Unit) {
        while (true) {
            animatedAngle.animateTo(
                targetValue = animatedAngle.value + 360,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = (5000 / velocity).toInt(),
                        easing = LinearEasing
                    )
                )
            )
        }
    }

    // Логика движения врага
    LaunchedEffect(Unit) {
        while (true) {
            if (!isPaused) {
                val radians = Math.toRadians((enemyAngle - 90).toDouble())
                enemyPosition = Offset(
                    enemyPosition.x + enemySpeed * cos(radians).toFloat(),
                    enemyPosition.y + enemySpeed * sin(radians).toFloat()
                )

                // Проверка, если враг выходит за пределы круга
                if (enemyPosition.getDistance() > 300f) {
                    enemyPosition =
                        randomStartPosition(300f) // Перезапуск на краю круга
                    enemyAngle = Random.nextFloat() * 360f
                }

                // Добавляем след только если точка пересекает луч
                if (isIntersectingWithBeam(enemyPosition, animatedAngle.value, 300f)) {
                    enemyTrail.add(FadingTrailPoint(enemyPosition))
                }

                enemyTrail.forEach { it.alpha *= 0.98f }
                enemyTrail.removeAll { it.alpha < 0.02f }
            }
            delay(50L)
        }
    }
    if (mode is GameMode.Bot || mode is GameMode.Online)
    // доворот врага на истребитель и стрельба
        LaunchedEffect(Unit) {
            var count = 0
            while (true) {
                count++
                // Вычисляем угол между целью и истребителем
                val deltaX = fighterPosition.x - enemyPosition.x
                val deltaY = fighterPosition.y - enemyPosition.y
                val targetAngle =
                    Math.toDegrees(atan2(deltaY.toDouble(), deltaX.toDouble())).toFloat() + 90f

                // Плавный поворот к новому углу с учетом максимального изменения за шаг
                val maxTurnRate = 2f // Максимальный угол поворота за шаг
                var angleDiff = (targetAngle - enemyAngle + 360) % 360
                if (angleDiff > 180) angleDiff -= 360 // Приводим разницу к диапазону [-180, 180]

                enemyAngle += min(maxTurnRate, abs(angleDiff)) * angleDiff.sign

                // Нормализуем угол, чтобы он не выходил за пределы [0, 360]
                enemyAngle = (enemyAngle % 360 + 360) % 360

                val enemyAngleUpdateDelay = 50L // обновление курса раз в 100 мc
                val shotDelay = 500L // промежуток между выстрелами 1 секунда

                // Логика выстрела цели
                val isMustShoot = count >= (shotDelay / enemyAngleUpdateDelay)
                val distanceToFighter = sqrt(deltaX * deltaX + deltaY * deltaY)
                val angleDifference =
                    ((targetAngle - enemyAngle + 540) % 360) - 180 // Корректная разница углов

                Log.d(
                    "shotLog",
                    "текущее: abs=${abs(angleDifference)} targetAngle: $targetAngle, курс врага: $enemyAngle"
                )

                if (isMustShoot && distanceToFighter < 150f && abs(angleDifference) < 10f) {
                    Log.d(
                        "shotLog",
                        "сработал иф!!!!!: abs=${abs(angleDifference)} targetAngle:$targetAngle, курс врага: $enemyAngle"
                    )
                    enemyRockets.add(
                        Rocket(
                            position = enemyPosition,
                            angle = enemyAngle,
                            maxDistance = 170f
                        )
                    )
                    count = 0
                }
                delay(enemyAngleUpdateDelay)
            }
        }

    // Логика движения истребителя
    LaunchedEffect(Unit) {
        while (true) {
            if (!isPaused) {
                val radians = Math.toRadians((fighterAngle - 90).toDouble())
                fighterPosition = Offset(
                    fighterPosition.x + fighterSpeed * cos(radians).toFloat(),
                    fighterPosition.y + fighterSpeed * sin(radians).toFloat()
                )

                // Проверка, если истребитель выходит за пределы круга
                if (fighterPosition.getDistance() > 300f) {
                    fighterPosition = Offset(0f, 0f) // Перезапуск истребителя в центр
                    // fighterAngle = Random.nextFloat() * 360f // закоменчено курс остается старый
                }

                // Добавляем след только если точка пересекает луч
                if (isIntersectingWithBeam(fighterPosition, animatedAngle.value, 300f)) {
                    fighterTrail.add(FadingTrailPoint(fighterPosition))
                }

                */
/*****  оставлю как пример более быстрого затухания
                fighterTrail.forEach { it.alpha *= 0.95f }
                fighterTrail.removeAll { it.alpha < 0.05f }****//*

                fighterTrail.forEach { it.alpha *= 0.98f }
                fighterTrail.removeAll { it.alpha < 0.02f }
            }
            delay(50L)
        }
    }

// Количество попаданий в текущую цель
    var hits by remember { mutableStateOf(0) }

// Количество попаданий в текущего истребителя
    var enemyHits by remember { mutableStateOf(0) }

// Стейт взрыва после пятого попадания в истребитель
    var isBabahState by remember { mutableStateOf(false) }

// Стейт взрыва после пятого попадания во врага
    var isEnemyBabahState by remember { mutableStateOf(false) }

// Логика выстрела ракеты
    LaunchedEffect(Unit) {
        while (true) {
            if (!isPaused) {
                // Обновляем позиции своих ракет и проверяем попадания
                rockets.removeAll { rocket ->
                    val radians = Math.toRadians((rocket.angle - 90).toDouble())
                    rocket.position = Offset(
                        rocket.position.x + rocket.speed * cos(radians).toFloat(),
                        rocket.position.y + rocket.speed * sin(radians).toFloat()
                    )
                    rocket.traveledDistance += rocket.speed

                    if (rocket.traveledDistance > rocket.maxDistance) {
                        true // Удаляем ракету, если она достигла максимального расстояния
                    } else if (rocket.position.getDistance(enemyPosition) < 10f) {
                        ++hits
                        explosion = Explosion(
                            rocket.position,
                            color = if (hits >= 5) Color.Red else Color.Yellow,
                            radiusCoefficient = if (hits >= 5) 25f else 20f,
                        )
                        if (hits >= 5) {
                            // Уничтожение цели (и респаун новой перенесен в иф с задержкой ниже)
                            isEnemyBabahState = true
                            viewModel.incrementPlayerScore()  // playerScore++ // Увеличиваем счёт игрока
                        }
                        true // Удаляем ракету при попадании
                    } else {
                        false // Оставляем ракету
                    }
                }

                // Обновляем позиции вражеских ракет и проверяем попадания по истребителю
                enemyRockets.removeAll { rocket ->
                    val radians = Math.toRadians((rocket.angle - 90).toDouble())
                    rocket.position = Offset(
                        rocket.position.x + rocket.speed * cos(radians).toFloat(),
                        rocket.position.y + rocket.speed * sin(radians).toFloat()
                    )
                    rocket.traveledDistance += rocket.speed

                    if (rocket.traveledDistance > rocket.maxDistance) {
                        true
                    } else if (rocket.position.getDistance(fighterPosition) < 10f) {
                        ++enemyHits
                        explosion = Explosion(
                            rocket.position,
                            color = if (enemyHits >= 5) Color.Red else Color.White,
                            radiusCoefficient = if (enemyHits >= 5) 25f else 20f,
                        )
                        if (enemyHits >= 5) {
                            // Уничтожение истребителя
                            isBabahState = true
                            viewModel.incrementEnemyScore()// enemyScore++ // Увеличиваем счёт врага
                        }
                        true
                    } else {
                        false
                    }
                }

                // Обновляем состояние вспышки
                explosion?.let {
                    it.alpha *= 0.85f
                    if (it.alpha < 0.05f) explosion = null
                }
                // задержка для показа взрывов и респаун точек истребителя и цели
                if (isBabahState) {
                    delay(700)
                    fighterPosition = Offset(0f, 0f)
                    enemyHits = 0
                } else if (isEnemyBabahState) {
                    delay(700)
                    hits = 0
                    enemyPosition = randomStartPosition(300f)
                    enemyAngle = calculateEnemyAngle(enemyPosition)
                } else delay(30L)

                isEnemyBabahState = false
                isBabahState = false
            } else {
                delay(30)
            }
        }
    }

// Каждый раз, когда приходит новая команда, проверяем, совпадает ли она с "огонь" это если юзать шаред флоу
    LaunchedEffect(Unit) {
        viewModel.commandFlow.collectLatest { newCommand ->
            if (newCommand == "авада кедавра") {
                // Выполняем 5 выстрелов, если команда "огонь"
                var count = 0
                while (count < 5) {
                    rockets.add(
                        Rocket(
                            fighterPosition,
                            fighterAngle,
                            maxDistance = 170f
                        )
                    )
                    count++
                    delay(100)
                }
            }
            if (newCommand.equals("скорость плюс", ignoreCase = true)) {
                // плавно увеличиваем скорость на одно деление
                var count = 0
                while (count < 10 && fighterSpeed / 2 < 0.9) {
                    // fighterSpeed+=0.01f
                    viewModel.updateThrottleFromVoice((fighterSpeed / 2) + 0.01f)
                    Log.d(
                        "speed",
                        "newCommand скорость плюс:fighterSpeed= $fighterSpeed, count= $count"
                    )
                    count++
                    delay(30)
                    if (fighterSpeed / 2 >= 0.9) viewModel.updateThrottleFromVoice(0.9f)
                }
            }
            if (newCommand.equals("максимал", ignoreCase = true)) {

                while (fighterSpeed / 2 <= 0.9) {
                    viewModel.updateThrottleFromVoice((fighterSpeed / 2) + 0.01f)
                    Log.d("speed", "newCommand максимал:fighterSpeed= $fighterSpeed")
                    delay(30)
                    if (fighterSpeed / 2 >= 0.9) viewModel.updateThrottleFromVoice(0.9f)
                }
            }
            if (newCommand.equals("малый газ", ignoreCase = true)) {
                // плавно увеличиваем скорость на одно деление
                while (fighterSpeed / 2 >= 0.1f) {
                    viewModel.updateThrottleFromVoice((fighterSpeed / 2) - 0.01f)
                    Log.d("speed", "newCommand малый газ:fighterSpeed= $fighterSpeed")
                    delay(30)
                    if (fighterSpeed / 2 <= 0.1f) viewModel.updateThrottleFromVoice(0.1f)
                }
            }
            if (newCommand.equals("обороты полсотни", ignoreCase = true)) {
                // плавно увеличиваем скорость на одно деление
                while (fighterSpeed / 2 >= 0.52f || fighterSpeed / 2 <= 0.48f) {
                    if (fighterSpeed / 2 >= 0.52f)
                        viewModel.updateThrottleFromVoice((fighterSpeed / 2) - 0.01f)
                    if (fighterSpeed / 2 <= 0.48f)
                        viewModel.updateThrottleFromVoice((fighterSpeed / 2) + 0.01f)
                    Log.d("speed", "newCommand обороты полсотни:fighterSpeed= $fighterSpeed")
                    delay(30)
                }
            }
            if (newCommand.equals("скорость минус", ignoreCase = true)) {
                // плавно увеличиваем скорость на одно деление
                var count = 0
                while (count < 10 && fighterSpeed > 0.1) {
                    // fighterSpeed+=0.01f
                    viewModel.updateThrottleFromVoice((fighterSpeed / 2) - 0.01f)
                    Log.d(
                        "speed",
                        "newCommand скорость минус:fighterSpeed= $fighterSpeed, count= $count"
                    )
                    count++
                    delay(30)
                    if (fighterSpeed / 2 <= 0.1f) viewModel.updateThrottleFromVoice(0.1f)
                }
            }
        }
    }

// Выстрел при клике на рут
    LaunchedEffect(Unit) {
        viewModel.singleShotFlow.collectLatest { newCommand ->
            Log.d("clickable", "viewModel.singleShotFlow.collectLatest")
            // Выполняем 5 выстрелов, когда клик по руту
            var count = 0
            while (count < 5) {
                rockets.add(
                    Rocket(
                        fighterPosition,
                        fighterAngle,
                        maxDistance = 170f
                    )
                )
                count++
                delay(100)
            }
        }
    }

// для шумовых помех
    val noisePoints = remember { mutableStateListOf<Pair<Offset, Float>>() }
    LaunchedEffect(Unit) {
        while (true) {
            val newNoise = List(50) {
                val angle = (0..360).random().toFloat() // Случайный угол
                val distance = (0..100).random() / 100f  // Радиус (0.0 - 1.0)

                val noiseX = cos(Math.toRadians(angle.toDouble())).toFloat() * distance
                val noiseY = sin(Math.toRadians(angle.toDouble())).toFloat() * distance

                val noiseAlpha = (5..20).random() / 100f // Прозрачность 0.05 - 0.2

                Offset(noiseX, noiseY) to noiseAlpha
            }
            noisePoints.clear()
            noisePoints.addAll(newNoise)
            delay(500L) // Обновление каждые 500 мс
        }
    }


// создание анимаций самолетиков
    var progress by remember { mutableStateOf(0f) }
    var lottieKey by remember { mutableStateOf("initial") }
    var isLeft by remember { mutableStateOf(false) }
    val enemyComposition by rememberLottieComposition(spec = LottieCompositionSpec.Asset("animation_nlo.json"))
    val rocketComposition by rememberLottieComposition(spec = LottieCompositionSpec.Asset("animation_astronaut.json"))
    val fighterComposition by rememberLottieComposition(LottieCompositionSpec.Asset("animation_vedma.json"))
    val compositionBabah by rememberLottieComposition(LottieCompositionSpec.Asset("anim_babah.json"))
    val compositionRandom by rememberLottieComposition(
        LottieCompositionSpec.Asset(lottieKey),
        cacheKey = lottieKey
    )
    LaunchedEffect(isEnemyBabahState) {
        val file = getRandomAnimFile()
        lottieKey = file.first
        isLeft = file.second
        Log.d("LaunchedEffect", "LaunchedEffect $isLeft")
    }
    LaunchedEffect(Unit) {
        while (true) {
            progress = (progress + 0.02f) % 1f // Зацикленная анимация
            delay(16L) // 60 FPS
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Box с возможностью зумирования , внутри него canvas с отрисовкой индикатора
        Box(
            Modifier
                .weight(3f)
                .size(700.dp)
                .weight(2f)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(1f, 20f)
                        val maxShift = 300f * (scale - 1)
                        offset = Offset(
                            (offset.x + pan.x).coerceIn(-maxShift, maxShift),
                            (offset.y + pan.y).coerceIn(-maxShift, maxShift)
                        )
                    }
                }
        ) {
            Canvas(Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2 + offset.x, size.height / 2 + offset.y)
                val radius = 300f * scale

                val targetScreenPosition = Offset(
                    center.x + targetPosition.x * scale,
                    center.y + targetPosition.y * scale
                )
                val enemyScreenPosition = Offset(
                    center.x + enemyPosition.x * scale,
                    center.y + enemyPosition.y * scale
                )
                val fighterScreenPosition = Offset(
                    center.x + fighterPosition.x * scale,
                    center.y + fighterPosition.y * scale
                )

                // Отрисовка ракет истребителя
                rockets.forEach { rocket ->
                    val rocketScreenPos = Offset(
                        center.x + rocket.position.x * scale,
                        center.y + rocket.position.y * scale
                    )
                    drawCircle(Color.White, 3f * scale, rocketScreenPos)
                }

                // Отрисовка ракет врага
                enemyRockets.forEach { rocket ->
                    val rocketScreenPos = Offset(
                        center.x + rocket.position.x * scale,
                        center.y + rocket.position.y * scale
                    )
                    drawCircle(Color.Blue, 3f * scale, rocketScreenPos)
                }

                // Отрисовка взрыва
                explosion?.let { exp ->
                    val explosionScreenPos =
                        Offset(center.x + exp.position.x * scale, center.y + exp.position.y * scale)

                    drawCircle(
                        exp.color.copy(
                            alpha = exp.alpha
                        ),
                        exp.radiusCoefficient * scale,
                        explosionScreenPos
                    )
                    explosionPosition = explosionScreenPos
                }
                // Отрисовываем круги дальности
                for (i in 1..4) {
                    drawCircle(
                        color = radarColor.copy(alpha = 0.5f),
                        center = center,
                        radius = radius * i / 4,
                        style = Stroke(2f)
                    )
                }
                // Рисуем шумовые помехи ВНУТРИ круга
                noisePoints.forEach { (position, alpha) ->
                    drawCircle(
                        color = Color.White.copy(alpha = alpha),
                        center = Offset(
                            x = center.x + position.x * radius,
                            y = center.y + position.y * radius
                        ),
                        radius = (1..3).random().toFloat()
                    )
                }

                // Отрисовываем азимутальные линии
                for (angle in 0 until 360 step 10) {
                    val isMainAzimuth = angle % 30 == 0
                    val alpha = 1f - abs(((animatedAngle.value - angle + 360) % 360) / 360)

                    val azimuthColor = radarColor.copy(alpha = alpha)
                    val azimuthEnd = Offset(
                        x = center.x + radius * cos(Math.toRadians(angle.toDouble())).toFloat(),
                        y = center.y + radius * sin(Math.toRadians(angle.toDouble())).toFloat()
                    )

                    drawLine(
                        color = azimuthColor,
                        start = center,
                        end = azimuthEnd,
                        strokeWidth = if (isMainAzimuth) 3f else 1f
                    )
                }

                // Числовые обозначения север юг запад восток 0 90 180 360 пока убрал, как опция
                */
/*val azimuthLabels = listOf("0°", "90°", "180°", "270°")
                val azimuthAnglesCorrected = listOf(270f, 0f, 90f, 180f)
                azimuthLabels.zip(azimuthAnglesCorrected).forEach { (label, angle) ->
                    val labelOffset = Offset(
                        x = center.x + (radius + 30) * cos(Math.toRadians(angle.toDouble())).toFloat(),
                        y = center.y + (radius + 30) * sin(Math.toRadians(angle.toDouble())).toFloat()
                    )
                    drawIntoCanvas { canvas ->
                        val paint = Paint().apply {
                            color = android.graphics.Color.RED
                            textSize = 30f
                            textAlign = Paint.Align.CENTER
                        }
                        canvas.nativeCanvas.drawText(label, labelOffset.x, labelOffset.y, paint)
                    }
                }*//*


                // Отрисовываем луч радара
                val beamEnd = Offset(
                    x = center.x + radius * cos(Math.toRadians(animatedAngle.value.toDouble())).toFloat(),
                    y = center.y + radius * sin(Math.toRadians(animatedAngle.value.toDouble())).toFloat()
                )
                drawLine(
                    color = beamColor,
                    start = center,
                    end = beamEnd,
                    strokeWidth = 4f
                )
                // Свечение луча
                drawCircle(
                    color = beamColor.copy(alpha = 0.2f),
                    center = center,
                    radius = radius * 0.2f
                )
                // Отображаем следы истребителя
                fighterTrail.forEach {
                    val pos =
                        Offset(center.x + it.position.x * scale, center.y + it.position.y * scale)
                    drawCircle(
                        color = fighterColor.copy(alpha = it.alpha),
                        radius = 3f * scale,
                        center = pos
                    )
                }

                // Отображаем следы врага
                enemyTrail.forEach {
                    val pos =
                        Offset(center.x + it.position.x * scale, center.y + it.position.y * scale)
                    drawCircle(
                        color = enemyColor.copy(alpha = it.alpha),
                        radius = 3f * scale,
                        center = pos
                    )
                }

                // Отображаем сами точки
                drawCircle(color = targetColor, radius = 5f * scale, center = targetScreenPosition)

                drawCircle(
                    color = enemyColor,
                    radius = 5f * scale,
                    center = enemyScreenPosition
                ) // если задать прозрачный цвет , то будут только отметки от цели после луча
                if (!isEnemyBabahState)
                    drawLottieAnimation(
                        composition = enemyComposition*/
/*compositionRandom*//*
,
                        progress = progress,
                        position = enemyScreenPosition,
                        baseSize = 40f, // Базовый размер самолетика
                        fighterAngle = enemyAngle,
                        isLeft = isLeft,//true,
                        // tint = enemyColor,
                        scale = scale * 1f // Масштабирование, как и у точки
                    )

                drawCircle(
                    color = fighterColor,
                    radius = 5f * scale,
                    center = fighterScreenPosition
                )
                if (!isBabahState)
                    drawLottieAnimation(
                        composition = rocketComposition,*/
/*fighterComposition*//*

                        progress = progress,
                        position = fighterScreenPosition,
                        baseSize = 40f, // Базовый размер самолетика
                        fighterAngle = fighterAngle,
                        isLeft = false, */
/*true*//*

                        scale = scale * 1.5f, // Масштабирование, как и у точки
                    )


                if (isBabahState || isEnemyBabahState) {
                    val explosionRadius = 30f * scale // Радиус взрыва (подгоняешь сам)
                    val explosionClipPath = Path().apply {
                        addOval(
                            Rect(
                                explosionPosition.x - explosionRadius,
                                explosionPosition.y - explosionRadius,
                                explosionPosition.x + explosionRadius,
                                explosionPosition.y + explosionRadius
                            )
                        )
                    }

                    withTransform({
                        clipPath(explosionClipPath) // Обрезаем анимацию по кругу
                    }) {
                        // Первый полукруг (вдоль туловища ведьмы)
                        drawLottieAnimation(
                            composition = compositionBabah,
                            progress = progress,
                            position = explosionPosition,
                            baseSize = 40f,
                            fighterAngle = enemyAngle + 90f, // Ориентируем вдоль туловища
                            isLeft = true,
                            scale = scale * 2f,
                            centerOffset = 0.3f, // Подгоняешь смещение сам
                        )

                        // Второй полукруг (в противоположную сторону)
                        drawLottieAnimation(
                            composition = compositionBabah,
                            progress = progress,
                            position = explosionPosition,
                            baseSize = 40f,
                            fighterAngle = enemyAngle - 90f, // В другую сторону от туловища
                            isLeft = true,
                            scale = scale * 2f,
                            centerOffset = 0.3f, // Подгоняешь смещение сам
                        )
                    }
                }


            }

        }
    }
}

// Генерация случайной начальной позиции для врага
fun randomStartPosition(radius: Float): Offset {
    val angle = Random.nextDouble(0.0, 360.0)
    return Offset(
        radius * cos(Math.toRadians(angle)).toFloat(),
        radius * sin(Math.toRadians(angle)).toFloat()
    )
}

// Вычисление угла для вражеской цели
fun calculateEnemyAngle(position: Offset): Float {
    val baseAngle =
        Math.toDegrees(atan2(-position.y.toDouble(), -position.x.toDouble())).toFloat() + 90
    return baseAngle + Random.nextFloat() * 40 - 20
}

// Проверка пересечения с лучом
fun isIntersectingWithBeam(position: Offset, beamAngle: Float, radius: Float): Boolean {
    val angleToTarget =
        Math.toDegrees(atan2(position.y.toDouble(), position.x.toDouble())).toFloat()
    val deltaAngle = abs((beamAngle - angleToTarget + 360) % 360)

    // Если угол между лучом и целью меньше 10 градусов, считаем что произошло пересечение
    return deltaAngle < 10f
}

fun Offset.getDistance(other: Offset): Float {
    return sqrt((x - other.x).pow(2) + (y - other.y).pow(2))
}

data class FadingTrailPoint(val position: Offset, var alpha: Float = 1f)

//для ракеты
data class Rocket(
    var position: Offset,
    val angle: Float,
    val speed: Float = 6f,
    val maxDistance: Float,
) {
    var traveledDistance = 0f
}

//для вспышки
data class Explosion(
    val position: Offset,
    var alpha: Float = 1f,
    var color: Color = Color.Yellow,
    var radiusCoefficient: Float = 20f,
)

fun getAnimFileList(): List<String> {
    return listOf("animation_panda.json", "animation_dobraya_vedma.json", "animation_nlo.json")
}

fun getRandomAnimFile(): Pair<String, Boolean> {
    return listOf(
        Pair("animation_panda.json", false),
        Pair("animation_dobraya_vedma.json", true),
        Pair("animation_nlo.json", false),
        Pair("aladdin_flying.json", false),
    ).random()
}
*/
