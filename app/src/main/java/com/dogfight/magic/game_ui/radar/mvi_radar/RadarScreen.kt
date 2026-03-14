package com.dogfight.magic.game_ui.radar.mvi_radar

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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dogfight.magic.R
import com.dogfight.magic.game_ui.radar.mvi_radar.mode_mission.drawRouteWithDeviationZone
import com.dogfight.magic.game_ui.radar.mvi_radar.mode_mission.getHelpPoint
import com.dogfight.magic.game_ui.radar.mvi_radar.mode_mission.isInStartCircle
import com.dogfight.magic.game_ui.radar.mvi_radar.mode_mission.isWithinDeviationZone
import com.dogfight.magic.game_ui.radar.upravlenie.fighter.drawFighterImageWithRollEffect
import com.dogfight.magic.game_ui.radar.upravlenie.utils.drawHouseImageCentered
import com.dogfight.magic.game_ui.widgets.SoundManager
import com.dogfight.magic.game_ui.widgets.SoundType
import com.dogfight.magic.game_ui.widgets.animations.drawLottieAnimation
import com.dogfight.magic.settings_screen.DisplayStyle
import com.dogfight.magic.settings_screen.widjets.VoiceCommandType
import com.dogfight.magic.utils.FpsOverlay
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlin.math.PI
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
fun RadarScreen(viewModel: RadarViewModel, gameLevel: Int, isLowEndDevice: Boolean) {
    val context = LocalContext.current
    val radarColor = Color.Green
    val beamColor = Color.Cyan
    val targetColor = Color.Cyan
    val fighterColor = Color.Red
    val enemyColor = Color.Yellow
    //val jetImage = ImageBitmap.imageResource(R.drawable.img_samol_test)
    val fighterJetImage = ImageBitmap.imageResource(/*R.drawable.img_fighter_jet*/R.drawable.img)
    val enemyJetImage = ImageBitmap.imageResource(/*R.drawable.img_figter*/R.drawable.img)
    val houseImage = ImageBitmap.imageResource(R.drawable.img_osobnyak)
    val screenState by viewModel.screenState.collectAsState()
    val rocketImage = rememberRocketImage()
    //костыль обновления курса истреблятола
    val course by viewModel.course.collectAsState()
    viewModel.updateFighterAngle(course)

    //стартовые позиции
    LaunchedEffect(Unit) {
        viewModel.updateEnemySpeed(
            when (gameLevel) {
                0 -> 0.5f
                1 -> 1.1f
                2 -> 1.2f
                3 -> 1.3f
                4 -> 1.4f
                5 -> 1.6f
                11 -> 1.6f // враг для режима прохождения маршрутов
                else -> 0.8f
            }
        )
        viewModel.updateEnemyAngle(270f)
        delay(100)
        viewModel.updateEnemyPosition(
            randomStartPosition(
                200f,
                screenState.enemyPosition.copy(x = 100f, y = 100f)
            )
        )
    }

    // позиция взрыва
    var explosionPosition by remember { mutableStateOf(Offset.Zero) }

    val animatedAngle = remember { Animatable(0f) }
    val velocity = 1f // скорость вращения луча

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

    val fighterTrail = remember { mutableStateListOf<FadingTrailPoint>() }
    val enemyTrail = remember { mutableStateListOf<FadingTrailPoint>() }
    // Список ракет истребителя
    val rockets = remember { mutableStateListOf<Rocket>() }

    // Список капелек истребителя
    val waters = remember { mutableStateListOf<Rocket>() }

    // Список самонаводящихся ракет истребителя
    val homingRockets = remember { mutableStateListOf<HomingRocket>() }

    // Список ракет врага
    val enemyRockets = remember { mutableStateListOf<Rocket>() }

    // Количество попаданий в текущую цель
    var hits by rememberSaveable { mutableStateOf(0) }

    // Количество попаданий в текущего истребителя
    var enemyHits by rememberSaveable { mutableStateOf(0) }

    // Стейт взрыва после пятого попадания в истребитель
    var isFighterBabahState by remember { mutableStateOf(false) }

    // Стейт взрыва после пятого попадания во врага
    var isEnemyBabahState by remember { mutableStateOf(false) }

    // Стейт клоуна после ляськи масяськи
    var isEnemyKlownState by remember { mutableStateOf(false) }
    var isEnemyDedState by remember { mutableStateOf(false) }

    // Вспышка при попадании
    var explosion by remember { mutableStateOf<Explosion?>(null) }

    var scaleFactorForSpeed by rememberSaveable {
        if (gameLevel == 0) mutableFloatStateOf(screenState.scaleFactorForSpeed) else mutableFloatStateOf(
            1f
        )
    }
    // Логика движения врага
    LaunchedEffect(!isEnemyBabahState) {
        while (true) {
            if (!screenState.isPaused && !isEnemyKlownState && !isEnemyDedState) {
                Log.d("isEnemyDedState", "isEnemyDedState=$isEnemyDedState, gamelavel==$gameLevel")
                val radians = Math.toRadians((screenState.enemyAngle - 90).toDouble())
                var newEnemyPosition = Offset(
                    screenState.enemyPosition.x + screenState.enemySpeed * cos(radians).toFloat() * scaleFactorForSpeed,
                    screenState.enemyPosition.y + screenState.enemySpeed * sin(radians).toFloat() * scaleFactorForSpeed
                )
                if (screenState.magicBeam == null && !isEnemyBabahState)
                    viewModel.updateEnemyPosition(newEnemyPosition)

                // Проверка, если враг выходит за пределы круга
                if (screenState.enemyPosition.getDistance() > 300f) {
                    newEnemyPosition =
                        randomStartPosition(
                            290f,
                            screenState.enemyPosition
                        ) // Перезапуск на краю круга
                    viewModel.updateEnemyPosition(newEnemyPosition)
                    viewModel.updateEnemyAngle(/*Random.nextFloat() * 360f*/calculateEnemyAngle(
                        newEnemyPosition
                    )
                    )
                }

                // Добавляем след только если точка пересекает луч
                if (isIntersectingWithBeam(screenState.enemyPosition, animatedAngle.value, 300f)) {
                    enemyTrail.add(FadingTrailPoint(screenState.enemyPosition))
                }

                enemyTrail.forEach { it.alpha *= /*0.98f*/ screenState.trailFadeFactor }
                enemyTrail.removeAll { it.alpha < 0.02f }
            }
            delay(50L)
        }
    }
    var enemyRollAngle: Float by remember { mutableStateOf(0f) }
    var enemyRollAngleAbsolute: Float by remember { mutableStateOf(0f) }
    LaunchedEffect(enemyRollAngleAbsolute) {
        while (true) {
            enemyRollAngle = when {
                enemyRollAngleAbsolute < 0 -> {
                    if (enemyRollAngle <= enemyRollAngleAbsolute) return@LaunchedEffect
                    enemyRollAngle - 1f
                }

                enemyRollAngleAbsolute > 0 -> {
                    if (enemyRollAngle >= enemyRollAngleAbsolute) return@LaunchedEffect
                    enemyRollAngle + 1f
                }

                else -> {
                    if (enemyRollAngle == enemyRollAngleAbsolute) return@LaunchedEffect
                    if (enemyRollAngle < 0) enemyRollAngle + 1f else enemyRollAngle - 1f
                }
            }
            delay(30)
        }
    }
    var enemyShieldCount by rememberSaveable { mutableStateOf(10) }
    var isMustEnemyShieldActiv by remember { mutableStateOf(true) }
    var isEnemyShieldActive by remember { mutableStateOf(false) }
    val enemyRocketCooldown = remember { mutableStateOf(true) }
    var isEnemyBatleTurn by remember { mutableStateOf(false) }// стейт что на врага наложен боевой разворот , чтоб курс не затерся при движении

    // доворот врага на истребитель и стрельба
    if (gameLevel != 0) LaunchedEffect(Unit) {
        var count = 0
        while (true) {
            if (!screenState.isPaused) {
                var enemyAngle = screenState.enemyAngle
                count++
                // Вычисляем угол между целью и истребителем
                val deltaX = screenState.fighterPosition.x - screenState.enemyPosition.x
                val deltaY = screenState.fighterPosition.y - screenState.enemyPosition.y
                val targetAngle =
                    Math.toDegrees(atan2(deltaY.toDouble(), deltaX.toDouble())).toFloat() + 90f

                // Плавный поворот к новому углу с учетом максимального изменения за шаг
                val maxTurnRate =  // Максимальный угол поворота за шаг
                    when (gameLevel) {
                        1 -> 0.4f
                        2 -> 0.8f
                        3 -> 1.2f
                        4 -> 1.6f
                        5 -> 2f
                        11 -> 2f  // для маршрутов враг самый агрессивный
                        0 -> 0.4f
                        else -> 0.4f
                    }

                var angleDiff = (targetAngle - enemyAngle + 360) % 360
                if (angleDiff > 180) angleDiff -= 360 // Приводим разницу к диапазону [-180, 180]
                Log.d("angleDiff", "angleDiff= $angleDiff")

                enemyRollAngleAbsolute = when {
                    angleDiff in -10f..10f -> 0f
                    angleDiff in 10f..40f -> 15f
                    angleDiff in -40f..-10f -> -15f
                    angleDiff < -40f -> -30f
                    angleDiff > 40f -> 30f
                    else -> 0f
                }
                enemyAngle = enemyAngle + min(
                    maxTurnRate,
                    abs(angleDiff)
                ) * angleDiff.sign
                // Нормализуем угол, чтобы он не выходил за пределы [0, 360]
                enemyAngle = (enemyAngle % 360 + 360) % 360
                if (!isEnemyBatleTurn)
                    viewModel.updateEnemyAngle(
                        enemyAngle
                    )
                val enemyAngleUpdateDelay = 30L
                val shotDelay = when (gameLevel) { // промежуток между выстрелами 1 секунда
                    0 -> 1000L
                    1 -> 500L
                    2 -> 400L
                    3 -> 300L
                    4 -> 200L
                    5 -> 200L
                    11 -> 200L
                    else -> 1000L
                }

                // Логика выстрела цели
                val isMustShoot = count >= (shotDelay / enemyAngleUpdateDelay)
                val distanceToFighter = sqrt(deltaX * deltaX + deltaY * deltaY)
                val angleDifference =
                    ((targetAngle - screenState.enemyAngle + 540) % 360) - 180 // Корректная разница углов
                if (abs(angleDifference) < 30f && isMustEnemyShieldActiv && distanceToFighter < 200f) {
                    isEnemyShieldActive = true
                    isMustEnemyShieldActiv = false
                }// else isEnemyShieldActive = false
                if (isMustShoot && distanceToFighter < 150f && abs(angleDifference) < 10f) {
                    enemyRockets.add(
                        Rocket(
                            position = screenState.enemyPosition,
                            angle = screenState.enemyAngle,
                            maxDistance = 170f
                        )
                    )
                    if (screenState.isEnemyShotSoundActive)
                        SoundManager.play(SoundType.ENEMY_SHOT, 1.5f)
                    count = 0
                }
                delay(enemyAngleUpdateDelay)
            } else {
                delay(30)
            }
        }
    }

    //логика применеия щита и самонаводящихся ракет ботом
    LaunchedEffect(isEnemyShieldActive) {
        if (isEnemyShieldActive) {
            delay(5000)
            isEnemyShieldActive = false
        }
    }
    LaunchedEffect(!isEnemyShieldActive) {
        if (!isEnemyShieldActive) {
            delay(5000)
            isMustEnemyShieldActiv = true
            Log.d("isMustEnemyShieldActiv", "isMustEnemyShieldActiv=$isMustEnemyShieldActiv")
        }
    }

    // Логика движения истребителя
    LaunchedEffect(!isFighterBabahState) {
        Log.d("fighterSpeed", "fighterSpeed= ${screenState.fighterSpeed}")
        while (true) {
            if (!screenState.isPaused && !isFighterBabahState) {
                val radians = Math.toRadians((screenState.fighterAngle - 90).toDouble())
                val newFighterPosition = Offset(
                    screenState.fighterPosition.x + screenState.fighterSpeed * cos(radians).toFloat() * scaleFactorForSpeed,
                    screenState.fighterPosition.y + screenState.fighterSpeed * sin(radians).toFloat() * scaleFactorForSpeed
                )
                viewModel.updateFighterPosition(newFighterPosition)
                Log.d("myLog", "Логика движения истребителя")
                // Проверка, если истребитель выходит за пределы круга
                if (screenState.fighterPosition.getDistance() > 300f) {
                    viewModel.updateFighterPosition(
                        /*   Offset( // Перезапуск истребителя в центр
                               0f,
                               0f
                           )*/
                        randomStartPosition(260f, screenState.fighterPosition)
                    )
                }

                // Добавляем след только если точка пересекает луч
                if (isIntersectingWithBeam(
                        screenState.fighterPosition,
                        animatedAngle.value,
                        300f
                    )
                ) {
                    fighterTrail.add(FadingTrailPoint(screenState.fighterPosition))
                }

                /*****  оставлю как пример более быстрого затухания
                fighterTrail.forEach { it.alpha *= 0.95f }
                fighterTrail.removeAll { it.alpha < 0.05f }****/
                fighterTrail.forEach { it.alpha *= /*0.98f*/ screenState.trailFadeFactor }
                fighterTrail.removeAll { it.alpha < 0.02f }
            }
            if (isLowEndDevice || gameLevel == 11) {
                Log.d("isLowEndDevice", "isLowEndDevice")
                delay(33L)
            } else {
                delay(33L) //16L
                Log.d("isLowEndDevice", "else")
            }
        }
    }

    // Логика выстрела ракеты
    LaunchedEffect(Unit) {
        while (true) {
            if (!screenState.isPaused) {
                // Обновляем позиции своих ракет и проверяем попадания
                rockets.removeAll { rocket ->
                    val radians = Math.toRadians((rocket.angle - 90).toDouble())
                    rocket.position = Offset(
                        rocket.position.x + rocket.speed * cos(radians).toFloat(),
                        rocket.position.y + rocket.speed * sin(radians).toFloat()
                    )
                    rocket.traveledDistance += rocket.speed

                    // 🔹 Проверка столкновения с активным щитом
                    if (isEnemyShieldActive) {
                        val shieldRadius = 40f
                        val enemyPos = screenState.enemyPosition
                        val rocketPos = rocket.position

                        val deltaX = rocketPos.x - enemyPos.x
                        val deltaY = rocketPos.y - enemyPos.y
                        val angleToRocket =
                            Math.toDegrees(atan2(deltaY, deltaX).toDouble()).toFloat()

                        // Поправка: у нас 0° вперёд (вверх), надо сдвинуть координаты
                        val enemyAngle = (screenState.enemyAngle - 90 + 360) % 360
                        val angleDiff = ((angleToRocket - enemyAngle + 360) % 360)

                        val dist = rocketPos.getDistance(enemyPos)

                        if ((dist <= shieldRadius && angleDiff in -60f..60f) || (dist <= shieldRadius && angleDiff in 300f..360f)) {
                            explosion = Explosion(
                                position = rocketPos,
                                color = Color.Green,
                                radiusCoefficient = 15f
                            )
                            SoundManager.play(SoundType.HIT_SHIELD)
                            return@removeAll true
                        }
                    }
                    if (rocket.traveledDistance > rocket.maxDistance) {
                        true // Удаляем ракету, если она достигла максимального расстояния
                    } else if (rocket.position.getDistance(screenState.enemyPosition) < 10f) {
                        SoundManager.play(SoundType.HIT_BODY)
                        hits++
                        viewModel.updateHits(hits)
                        explosion = Explosion(
                            rocket.position,
                            color = if (hits >= 5) Color.Red else Color.Yellow,
                            radiusCoefficient = if (hits >= 5) 25f else 20f,
                        )
                        if (hits >= 5 && !isEnemyBabahState) {
                            // Уничтожение цели (и респаун новой )
                            isEnemyBabahState = true
                            SoundManager.play(SoundType.EXPLOSION)
                            if (gameLevel == 11) isEnemyDedState = true

                            val newEnemyPos = randomStartPosition(
                                290f,
                                screenState.enemyPosition
                            )
                            viewModel.updateEnemyPosition(
                                newEnemyPos
                            )
                            val enemyAngle = calculateEnemyAngle(newEnemyPos)
                            viewModel.updateEnemyAngle(enemyAngle)
                            viewModel.incrementPlayerScore()  // playerScore++ // Увеличиваем счёт игрока
                        }
                        true // Удаляем ракету при попадании
                    } else {
                        false // Оставляем ракету
                    }
                }

                if (!isEnemyKlownState && !isEnemyDedState)
                    enemyRockets.removeAll { rocket ->
                        val radians = Math.toRadians((rocket.angle - 90).toDouble())
                        rocket.position = Offset(
                            rocket.position.x + rocket.speed * cos(radians).toFloat(),
                            rocket.position.y + rocket.speed * sin(radians).toFloat()
                        )
                        rocket.traveledDistance += rocket.speed

                        val fighterPos = screenState.fighterPosition
                        val rocketPos = rocket.position
                        val distToFighter = rocketPos.getDistance(fighterPos)

                        // 🔹 Проверка столкновения с активным щитом
                        if (screenState.isShieldActive) {
                            val shieldRadius = 40f
                            val fighterPos = screenState.fighterPosition
                            val rocketPos = rocket.position

                            val deltaX = rocketPos.x - fighterPos.x
                            val deltaY = rocketPos.y - fighterPos.y
                            val angleToRocket =
                                Math.toDegrees(atan2(deltaY, deltaX).toDouble()).toFloat()

                            // Поправка: у нас 0° вперёд (вверх), надо сдвинуть координаты
                            val fighterAngle = (screenState.fighterAngle - 90 + 360) % 360
                            val angleDiff = ((angleToRocket - fighterAngle + 360) % 360)

                            val dist = rocketPos.getDistance(fighterPos)

                            if (dist <= shieldRadius && angleDiff in -60f..60f || angleDiff in 300f..360f) {
                                explosion = Explosion(
                                    position = rocketPos,
                                    color = Color.Cyan,
                                    radiusCoefficient = 15f
                                )
                                SoundManager.play(SoundType.HIT_SHIELD)
                                return@removeAll true
                            }
                        }

                        if (rocket.traveledDistance > rocket.maxDistance) {
                            true
                        } else if (distToFighter < 10f) {
                            // 💣 Столкновение с самолётом (если нет щита)
                            SoundManager.play(SoundType.HIT_BODY)
                            enemyHits++
                            viewModel.updateEnemyHits(enemyHits)
                            explosion = Explosion(
                                rocket.position,
                                color = if (enemyHits >= 5) Color.Red else Color.White,
                                radiusCoefficient = if (enemyHits >= 5) 25f else 20f,
                            )
                            if (enemyHits >= 5 && !isFighterBabahState) {
                                viewModel.updateFighterPosition(
                                    randomStartPosition(
                                        290f,
                                        screenState.enemyPosition
                                    )
                                )
                                isFighterBabahState = true
                                SoundManager.play(SoundType.EXPLOSION)
                                viewModel.incrementEnemyScore()
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
                // задержка для показа взрывов
                if (isFighterBabahState) {
                    delay(700)
                    enemyHits = 0
                    viewModel.updateEnemyHits(enemyHits)
                    isFighterBabahState = false
                } else if (isEnemyBabahState) {
                    delay(700)
                    hits = 0
                    viewModel.updateHits(hits)
                    isEnemyBabahState = false
                } else delay(30L)


            } else {
                delay(30)
            }
        }
    }

    // ───────── state в Composable ───────────────────────────────
    var targetCourse by remember { mutableStateOf<Int?>(null) }
    val alignTolerance = 3                  // ±3° – считаем “поймали курс”

// ───────── слушаем изменение курса для вывода на прямую после голосовой команды ───────────
    LaunchedEffect(course) {
        if (screenState.isDragStart) targetCourse =
            null // зануляем курс заданный голосом когда летчик взялся за ручку сам
        targetCourse?.let { target ->
            if (course in (target - alignTolerance).toFloat()..(target + alignTolerance).toFloat()) {
                viewModel.updateRollAngle(0f)   // выровнялись – крен 0
                targetCourse = null             // цель достигнута, забываем
            }
        }
    }
    LaunchedEffect(isEnemyBatleTurn) {
        if (isEnemyBatleTurn) delay(50)
        isEnemyBatleTurn = false
    }
//для пасхалок заклинаний
    var isEnemyKlownStateBlocked by rememberSaveable { mutableStateOf(false) }
    var isEnemyKlownStateBlockedByBoo by rememberSaveable { mutableStateOf(false) }
    var isEnemyKlownStateBlockedByBobr by rememberSaveable { mutableStateOf(false) }
    var isBobrComposition by rememberSaveable { mutableStateOf(false) }
    var isCappuccinoComposition by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(screenState.showResultDialog) {
        if (screenState.showResultDialog == false) {
            isEnemyKlownStateBlocked = false
            isEnemyKlownStateBlockedByBoo = false
            isEnemyKlownStateBlockedByBobr = false
            isBobrComposition = false
            isCappuccinoComposition = false
        }
    }

// Каждый раз, когда приходит новая команда, проверяем, совпадает ли она с "огонь" это если юзать шаред флоу
    LaunchedEffect(Unit) {
        viewModel.commandFlow.collectLatest { newCommand ->
            if (newCommand.contains(
                    screenState.voiceCommands[VoiceCommandType.ONE_SHOT].toString(),
                    ignoreCase = true
                )
            ) {
                // Выполняем 5 выстрелов, если команда "огонь"
                var count = 0
                while (count < 1) {
                    rockets.add(
                        Rocket(
                            screenState.fighterPosition,
                            screenState.fighterAngle,
                            maxDistance = 170f
                        )
                    )
                    count++
                    delay(100)
                }
            }

            if (newCommand.contains(
                    screenState.voiceCommands[VoiceCommandType.AVADA_KEDAVRA].toString(),
                    ignoreCase = true
                )
            ) {
                //  viewModel.launchMagicBeamIfInRange()
                viewModel.onAvadaClick()
            }

            if (newCommand.contains(
                    "ляськи",
                    ignoreCase = true
                ) || newCommand.contains(
                    "ляжки",
                    ignoreCase = true
                )
                || newCommand.contains(
                    "халай",
                    ignoreCase = true
                )
                || newCommand.contains(
                    context.getString(R.string.hocus_pocus),
                    ignoreCase = true
                )
            ) {
                if (!isEnemyKlownStateBlocked) {
                    isEnemyKlownState = true
                    isEnemyKlownStateBlocked = true
                }
            }
            if (newCommand.contains(
                    context.getString(R.string.boo_cat_voice),
                    ignoreCase = true
                )
            ) {
                if (!isEnemyKlownStateBlockedByBoo) {
                    isCappuccinoComposition = true
                    isEnemyKlownState = true
                    isEnemyKlownStateBlockedByBoo = true
                }
            }
            if (newCommand.contains(
                    context.getString(R.string.bobr),
                    ignoreCase = true
                )
            ) {
                if (!isEnemyKlownStateBlockedByBobr) {
                    isBobrComposition = true
                    isEnemyKlownState = true
                    isEnemyKlownStateBlockedByBobr = true
                }
            }

            if (newCommand.contains(
                    screenState.voiceCommands[VoiceCommandType.FIRE].toString(),
                    ignoreCase = true
                )
            ) {
                viewModel.onRootClick()
            }
            if (newCommand.contains(
                    screenState.voiceCommands[VoiceCommandType.ROCKET_LAUNCH].toString(),
                    ignoreCase = true
                )
            ) {
                // Выполняем пуск
                if (screenState.isInLockZone) {
                    viewModel.onHomingButtonClick()
                    viewModel.decreaseHoming()
                }
            }

            if (newCommand.contains(
                    screenState.voiceCommands[VoiceCommandType.SUPER_ROCKET_LAUNCH].toString(),
                    ignoreCase = true
                )
            ) {
                // Выполняем пуск супер ракеты
                viewModel.onSupperRocketButtonClick()
                viewModel.decreaseSuperRocket()
            }

            if (newCommand.contains(
                    screenState.voiceCommands[VoiceCommandType.BATTLE_TURN].toString(),
                    ignoreCase = true
                )
            ) {
                isEnemyBatleTurn = true
                viewModel.setCourse(course - 180)
            }

            if (newCommand.contains(
                    screenState.voiceCommands[VoiceCommandType.BATTLE_TURN_ENEMY].toString(),
                    ignoreCase = true
                )
            ) {

                viewModel.updateEnemyAngle(screenState.enemyAngle - 180)
            }

            if (newCommand.contains(
                    screenState.voiceCommands[VoiceCommandType.AFTERBURNER_ON].toString(),
                    ignoreCase = true
                )
            ) {
                if (!viewModel.screenState.value.isAfterburnerActive)
                    viewModel.toggleAfterburner()
            }
            if (newCommand.contains(
                    screenState.voiceCommands[VoiceCommandType.AFTERBURNER_OFF].toString(),
                    ignoreCase = true
                )
            ) {
                if (viewModel.screenState.value.isAfterburnerActive)
                    viewModel.toggleAfterburner()
            }
            if (newCommand.contains(
                    screenState.voiceCommands[VoiceCommandType.SPEED_PLUS].toString(),
                    ignoreCase = true
                )
            ) {
                //выключаем форсаж если включен
                if (viewModel.screenState.value.isAfterburnerActive) {
                    viewModel.toggleAfterburner()
                    viewModel.updateThrottleFromVoice(screenState.throttleSlider + 0.1f)
                    if (screenState.throttleSlider >= 0.85f) viewModel.updateThrottleFromVoice(
                        0.85f
                    )
                } else {
                    // плавно увеличиваем скорость на одно деление
                    var count = 0
                    while (count < 10 && screenState.fighterSpeed / 2 < 0.85f) {
                        viewModel.updateThrottleFromVoice((screenState.fighterSpeed / 2) + 0.01f)
                        count++
                        delay(30)
                        if (screenState.fighterSpeed / 2 >= 0.85f) viewModel.updateThrottleFromVoice(
                            0.85f
                        )
                    }
                }
            }
            if (newCommand.contains(
                    screenState.voiceCommands[VoiceCommandType.SPEED_MAX].toString(),
                    ignoreCase = true
                )
            ) {
                //выключаем форсаж если включен
                if (viewModel.screenState.value.isAfterburnerActive) {
                    viewModel.toggleAfterburner()
                    viewModel.updateThrottleFromVoice(0.85f)
                } else {
                    while (screenState.fighterSpeed / 2 <= 0.85) {
                        viewModel.updateThrottleFromVoice((screenState.fighterSpeed / 2) + 0.01f)
                        delay(30)
                        if (screenState.fighterSpeed / 2 >= 0.85f) viewModel.updateThrottleFromVoice(
                            0.85f
                        )
                    }
                }
            }
            if (newCommand.contains(
                    screenState.voiceCommands[VoiceCommandType.SPEED_MIN].toString(),
                    ignoreCase = true
                )
            ) {
                //выключаем форсаж если включен
                if (viewModel.screenState.value.isAfterburnerActive) {
                    viewModel.toggleAfterburner()
                    viewModel.updateThrottleFromVoice(0.15f)
                } else {
                    // плавно уменьшаем скорость до минимума
                    while (screenState.fighterSpeed / 2 >= 0.15f) {
                        viewModel.updateThrottleFromVoice((screenState.fighterSpeed / 2) - 0.01f)
                        delay(30)
                        if (screenState.fighterSpeed / 2 <= 0.15f) viewModel.updateThrottleFromVoice(
                            0.15f
                        )
                    }
                }
            }
            if (newCommand.contains(
                    screenState.voiceCommands[VoiceCommandType.SPEED_HALF].toString(),
                    ignoreCase = true
                )
            ) {
                //выключаем форсаж если включен
                if (viewModel.screenState.value.isAfterburnerActive) {
                    viewModel.toggleAfterburner()
                    viewModel.updateThrottleFromVoice(0.5f)
                } else {
                    // плавно увеличиваем скорость на одно деление
                    while (screenState.fighterSpeed / 2 >= 0.52f || screenState.fighterSpeed / 2 <= 0.48f) {
                        if (screenState.fighterSpeed / 2 >= 0.52f)
                            viewModel.updateThrottleFromVoice((screenState.fighterSpeed / 2) - 0.01f)
                        if (screenState.fighterSpeed / 2 <= 0.48f)
                            viewModel.updateThrottleFromVoice((screenState.fighterSpeed / 2) + 0.01f)
                        delay(30)
                    }
                }
            }
            if (newCommand.contains(
                    screenState.voiceCommands[VoiceCommandType.SPEED_MINUS].toString(),
                    ignoreCase = true
                )
            ) {
                //выключаем форсаж если включен
                if (viewModel.screenState.value.isAfterburnerActive) {
                    viewModel.toggleAfterburner()
                    viewModel.updateThrottleFromVoice(screenState.throttleSlider - 0.1f)
                    if (screenState.throttleSlider <= 0.15) viewModel.updateThrottleFromVoice(
                        0.15f
                    )
                } else {
                    // плавно увеличиваем скорость на одно деление
                    var count = 0
                    while (count < 10 && screenState.fighterSpeed > 0.15) {
                        // fighterSpeed+=0.01f
                        viewModel.updateThrottleFromVoice((screenState.fighterSpeed / 2) - 0.01f)
                        count++
                        delay(30)
                        if (screenState.fighterSpeed / 2 <= 0.1f) viewModel.updateThrottleFromVoice(
                            0.15f
                        )
                    }
                }
            }

            if (newCommand.contains(
                    screenState.voiceCommands[VoiceCommandType.ROLL_LEFT].toString(),
                    ignoreCase = true
                )
            ) {
                val regex = "\\d+".toRegex()
                targetCourse = regex.find(newCommand)?.value?.toIntOrNull()
                viewModel.updateRollAngle(-screenState.krenVoice / 1.5f)
            }
            if (newCommand.contains(
                    screenState.voiceCommands[VoiceCommandType.ROLL_RIGHT].toString(),
                    ignoreCase = true
                )
            ) {
                val regex = "\\d+".toRegex()
                targetCourse = regex.find(newCommand)?.value?.toIntOrNull()
                viewModel.updateRollAngle(screenState.krenVoice / 1.5f)
            }

            if (newCommand.contains(
                    screenState.voiceCommands[VoiceCommandType.ROLL_CENTER].toString(),
                    ignoreCase = true
                )
            ) {
                viewModel.updateRollAngle(0f)
                targetCourse = null
            }

            if (newCommand.contains(
                    screenState.voiceCommands[VoiceCommandType.ROLL_LEFT_10].toString(),
                    ignoreCase = true
                )
            ) {
                viewModel.updateRollAngle(-60 / 1.5f)
                delay(600)
                viewModel.updateRollAngle(0f)
                targetCourse = null
            }

            if (newCommand.contains(
                    screenState.voiceCommands[VoiceCommandType.ROLL_RIGHT_10].toString(),
                    ignoreCase = true
                )
            ) {
                viewModel.updateRollAngle(60 / 1.5f)
                delay(600)
                viewModel.updateRollAngle(0f)
                targetCourse = null
            }
            if (newCommand.contains(
                    screenState.voiceCommands[VoiceCommandType.ROLL_LEFT_5].toString().trim(),
                    ignoreCase = true
                )
            ) {
                targetCourse = (course - 5f).toInt()
                viewModel.updateRollAngle(-60 / 1.5f)
                /* delay(300)
                 viewModel.updateRollAngle(0f)
                 targetCourse=null*/
            }

            if (newCommand.contains(
                    screenState.voiceCommands[VoiceCommandType.ROLL_RIGHT_5].toString(),
                    ignoreCase = true
                )
            ) {
                targetCourse = (course + 5f).toInt()
                viewModel.updateRollAngle(60 / 1.5f)
                /* delay(300)
                 viewModel.updateRollAngle(0f)
                 targetCourse=null*/
            }

            if (newCommand.contains(
                    screenState.voiceCommands[VoiceCommandType.SHIELD].toString(),
                    ignoreCase = true
                )
            ) {
                viewModel.useShield()
            }
        }
    }
// вычесление азимута цели
    LaunchedEffect(screenState.enemyPosition, screenState.fighterPosition) {
        viewModel.updateEnemyAzimuth(
            calculateEnemyAzimuth(
                screenState.fighterPosition,
                screenState.enemyPosition
            )
        )
    }


// Выстрел при клике на рут
    LaunchedEffect(Unit) {
        viewModel.singleShotFlow.collectLatest { newCommand ->
            // Выполняем 5 выстрелов, когда клик по руту
            var count = 0
            val shellsInSalvoCount = if (newCommand == "FireButton") {
                screenState.shellsInSalvoFireButton
            } else screenState.shellsInSalvoRoot
            val stopCount = if (shellsInSalvoCount <= screenState.ammoCount)
                shellsInSalvoCount else screenState.ammoCount
            while (count < stopCount) {
                rockets.add(
                    Rocket(
                        screenState.fighterPosition,
                        screenState.fighterAngle,
                        maxDistance = 170f
                    )
                )
                SoundManager.play(SoundType.GUN_SHOT)
                count++
                viewModel.decreaseAmmo()
                delay(100)
            }
        }
    }


    //логика самонаводящихся ракет
    LaunchedEffect(Unit) {
        while (isActive) {
            val now = System.currentTimeMillis()
            val rocketsToRemove = mutableListOf<HomingRocket>()

            homingRockets.forEach { rocket ->
                val angleToTarget =
                    calculateAngleToTarget(rocket.position, screenState.enemyPosition)
                rocket.angle = smoothRotateTowards(
                    current = rocket.angle,
                    target = angleToTarget,
                    maxRotationPerStep = rocket.maxRotationPerStep
                )

                val radians = Math.toRadians((rocket.angle - 90).toDouble())
                rocket.position = Offset(
                    rocket.position.x + rocket.speed * cos(radians).toFloat(),
                    rocket.position.y + rocket.speed * sin(radians).toFloat()
                )

                // 🔹 Проверка столкновения с активным щитом
                if (isEnemyShieldActive) {
                    val shieldRadius = 40f
                    val enemyPos = screenState.enemyPosition
                    val rocketPos = rocket.position

                    val deltaX = rocketPos.x - enemyPos.x
                    val deltaY = rocketPos.y - enemyPos.y
                    val angleToRocket =
                        Math.toDegrees(atan2(deltaY, deltaX).toDouble()).toFloat()

                    // Поправка: у нас 0° вперёд (вверх), надо сдвинуть координаты
                    val enemyAngle = (screenState.enemyAngle - 90 + 360) % 360
                    val angleDiff = ((angleToRocket - enemyAngle + 360) % 360)

                    val dist = rocketPos.getDistance(enemyPos)

                    if ((dist <= shieldRadius && angleDiff in -60f..60f) || (dist <= shieldRadius && angleDiff in 300f..360f)) {
                        SoundManager.play(SoundType.HIT_SHIELD)
                        explosion = Explosion(
                            position = rocketPos,
                            color = Color.Green,
                            radiusCoefficient = 15f
                        )
                        rocketsToRemove.add(rocket)
                        // return@forEach
                    }
                }
                val isHit = rocket.position.getDistance(screenState.enemyPosition) < 10f
                val isExpired = now - rocket.launchTime > rocket.lifespan

                if (isHit) {
                    SoundManager.play(SoundType.HIT_BODY)
                    hits++
                    viewModel.updateHits(hits)
                    explosion = Explosion(
                        position = rocket.position,
                        color = if (hits >= 5) Color.Red else Color.Yellow,
                        radiusCoefficient = if (hits >= 5) 25f else 20f
                    )
                    if (hits >= 5 && !isEnemyBabahState) {
                        isEnemyBabahState = true
                        SoundManager.play(SoundType.EXPLOSION)
                        if (gameLevel == 11) isEnemyDedState = true
                        viewModel.incrementPlayerScore()
                        val newEnemyPos = randomStartPosition(
                            290f,
                            screenState.enemyPosition
                        )
                        viewModel.updateEnemyPosition(
                            newEnemyPos
                        )
                        viewModel.updateEnemyAngle(calculateEnemyAngle(newEnemyPos))
                    }
                    rocketsToRemove.add(rocket)
                }

                if (isExpired) {
                    rocketsToRemove.add(rocket)
                }
            }

            homingRockets.removeAll(rocketsToRemove)
            delay(30L)
        }
    }

    // пуск самонаводящейся при клике на ее значек
    LaunchedEffect(Unit) {
        viewModel.singleHomingShotFlow.collectLatest { newCommand ->
            val currentTime = System.currentTimeMillis()
            val newRocket = HomingRocket(
                position = screenState.fighterPosition,
                angle = screenState.fighterAngle,
                launchTime = currentTime
            )
            SoundManager.play(SoundType.MISSILE_LAUNCH, 2.30f)
            homingRockets.add(newRocket)
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

    // для авады кедавры
    val currentDensity = LocalDensity.current
    LaunchedEffect(screenState.magicBeam) {
        val center = Offset(
            screenState.canvasSize.width / 2 + screenState.offset.x,
            screenState.canvasSize.height / 2 + screenState.offset.y
        )
        val enemyScreenPosition = center + screenState.enemyPosition * screenState.scale
        if (screenState.magicBeam != null) {
            val beam = screenState.magicBeam!!

            val shieldRadius = with(currentDensity) { 40.dp.toPx() }
            // val shieldRadius = 40.dp.toPx()
            val hitShield = isEnemyShieldActive && isAvadaBlockedByShield(
                start = beam.from,
                end = beam.to,
                shieldCenter = enemyScreenPosition,
                shieldAngle = screenState.enemyAngle,
                shieldRadius = shieldRadius
            )

            if (hitShield) {
                // эффект взрыва на щите
                SoundManager.play(SoundType.AVADA_SHIELD)
                explosion = Explosion(
                    position = enemyScreenPosition,
                    color = Color.Green,
                    radiusCoefficient = 15f
                )
                // Авада не уничтожает врага
                delay(500)
                viewModel.clearMagicBeam()
            } else {
                // Уничтожение цели
                isEnemyBabahState = true
                SoundManager.play(SoundType.EXPLOSION)
                if (gameLevel == 11) isEnemyDedState = true
                delay(300)
                val newEnemyPos = randomStartPosition(
                    290f,
                    screenState.enemyPosition
                )
                viewModel.updateEnemyPosition(newEnemyPos)
                val enemyAngle = calculateEnemyAngle(newEnemyPos)
                viewModel.updateEnemyAngle(enemyAngle)
                viewModel.incrementPlayerScore()
                isEnemyBabahState = false
            }
        }
    }


    // для ляськи масяськи
    LaunchedEffect(isEnemyKlownState) {
        if (gameLevel == 11)
            delay(10000) else delay(5000)
        isEnemyKlownState = false
        isBobrComposition = false
        isCappuccinoComposition = false
    }

    // для задержки респауна врага режима 11 тушение пожара
    LaunchedEffect(isEnemyDedState) {
        Log.d("LaunchedEffectisEnemyDedState", "LaunchedEffect: isEnemyDedState= $isEnemyDedState")
        delay(15000)
        isEnemyDedState = false
    }

    // для маршрутов
    var isAtStart by remember { mutableStateOf(false) }
    var isOffRoute by remember { mutableStateOf(false) }
    var isInLegalRoute by remember { mutableStateOf(false) }
    var fireProgress by rememberSaveable { mutableStateOf(0f) }
    //  var fireProgressSize by rememberSaveable { mutableStateOf(0f) }
    var waterRadiusProgress by rememberSaveable { mutableStateOf(0f) }
    val center = Offset(
        screenState.canvasSize.width / 2 + screenState.offset.x,
        screenState.canvasSize.height / 2 + screenState.offset.y
    )

    LaunchedEffect(screenState.isWaterDropping) {
        while (true) {
            if (screenState.isWaterDropping)
                waterRadiusProgress = waterRadiusProgress + 2f
            else waterRadiusProgress = 0f
            delay(30)
        }
    }
    LaunchedEffect(screenState.isWaterDropping) {
        if (screenState.isWaterDropping)
            SoundManager.play(SoundType.WATER, 4f)
    }

    //для маршрутов и пожара
    if (gameLevel == 11) {
        LaunchedEffect(screenState.fighterPosition) {
            isAtStart =
                isInStartCircle(screenState.fighterPosition, screenState.routePoints.first(), 40f)
            if (isAtStart) {
                isInLegalRoute = true
                viewModel.updateIsInLegalRoute(true)
            }

            isOffRoute = !isWithinDeviationZone(
                screenState.routePoints,
                screenState.fighterPosition,
                20f
            )
            Log.d("offRoute", "offRoute isAtStart")

            if (isOffRoute) {
                isInLegalRoute = false
                viewModel.updateIsInLegalRoute(false)
                // viewModel.updateHasStarted(false)
                // обработка отклонения
                Log.d("offRoute", "offRoute isAtStart")
            }
        }
        if (!screenState.isPaused)
            LaunchedEffect(screenState.routePoints) {
                if (!screenState.isFireActive) return@LaunchedEffect
                while (screenState.isFireActive) {
                    viewModel.updateFireProgress(screenState.fireProgressSize + 0.001f)
                    if (screenState.fireProgressSize >= 1f) {
                        //viewModel.updateFireProgress(0f)
                        viewModel.showGameResult(false)
                        return@LaunchedEffect
                    }
                    delay(100)
                }
            }
    }


    /*if (gameLevel == 11) {
        LaunchedEffect(screenState.routePoints) {
            Log.d("firePoints", "firePoints ${screenState.firePoints}")
            // firePoints.first().isActive = true
            while (true) {
                screenState.firePoints.forEachIndexed { index, firePoint ->
                    if (index != 0) {
                        if (firePoints[index - 1].isActive && !firePoint.isActive) {
                            firePoint.isStartProgress = true
                        }
                    }
                }
                delay(16)
            }
        }
        LaunchedEffect(screenState.routePoints) {
            while (true) {
                firePoints.forEachIndexed { index, firePoint ->
                    if (index != 0) {
                        if (firePoint.isStartProgress) {
                            delay(6000)
                            if (firePoints[index - 1].isActive) {
                                firePoint.isActive = true
                                if (firePoint == firePoints.last()) {
                                    viewModel.showGameResult(isWinner = false)
                                    return@LaunchedEffect
                                }
                            }
                            firePoint.isStartProgress = false
                        }
                    }
                }
                delay(16)
            }
        }
        LaunchedEffect(firePoints) {
            while (true) {
                if (firePoints.none { it.isActive }) {
                    delay(1000)
                    viewModel.showGameResult(isWinner = true)
                    Log.d("showGameResult", "showGameResult")
                    return@LaunchedEffect
                }
                delay(100)
            }
        }*/


    // для автофокуса на истребителе
    if (screenState.fighterFocus != null)
        LaunchedEffect(
            screenState.enemyPosition,
            screenState.fighterPosition,
            screenState.scale,
            screenState.canvasSize, /*screenState.isAutoFocusEnabled*/
        ) {
            //  if (!screenState.isAutoFocusEnabled) return@LaunchedEffect
            val pos =
                if (screenState.fighterFocus == true)
                    screenState.fighterPosition else
                    screenState.enemyPosition
            val x = pos.x
            val y = pos.y
            val screenWidth = screenState.canvasSize.width
            val screenHeight = screenState.canvasSize.height

            val marginX = screenWidth * 0.1f
            val marginY = screenHeight * 0.1f

            val targetObjectScreenPos = Offset(
                x = screenWidth / 2 + screenState.offset.x + x * screenState.scale,
                y = screenHeight / 2 + screenState.offset.y + y * screenState.scale
            )

            var newOffset = screenState.offset

            if (targetObjectScreenPos.x < marginX) {
                newOffset = newOffset.copy(x = newOffset.x + (marginX - targetObjectScreenPos.x))
            } else if (targetObjectScreenPos.x > screenWidth - marginX) {
                newOffset =
                    newOffset.copy(x = newOffset.x - (targetObjectScreenPos.x - (screenWidth - marginX)))
            }

            if (targetObjectScreenPos.y < marginY) {
                newOffset = newOffset.copy(y = newOffset.y + (marginY - targetObjectScreenPos.y))
            } else if (targetObjectScreenPos.y > screenHeight - marginY) {
                newOffset =
                    newOffset.copy(y = newOffset.y - (targetObjectScreenPos.y - (screenHeight - marginY)))
            }

            // Ограничиваем смещение, чтобы не выйти за круг радара
            val maxShift = 300f * (screenState.scale - 1)
            newOffset = Offset(
                newOffset.x.coerceIn(-maxShift, maxShift),
                newOffset.y.coerceIn(-maxShift, maxShift)
            )

            if (newOffset != screenState.offset) {
                viewModel.updateOffset(newOffset)
            }
        }

// создание анимаций самолетиков
    var progress by remember { mutableStateOf(0f) }
    var lottieKey by remember { mutableStateOf("initial") }
    var isLeft by remember { mutableStateOf(false) }
    val enemyLottieAsset = when (gameLevel) {
        0 -> {
            isLeft = false
            "animation_panda.json"
        }

        1 -> {
            isLeft = false
            "animation_panda.json"
        }

        2 -> {
            isLeft = true
            "animation_dobraya_vedma.json"
        }

        3 -> {
            isLeft = false
            "animation_astronaut.json"
        }

        4 -> {
            isLeft = true
            "animation_bez_metly.json"
        }

        5 -> {
            isLeft = true
            "animation_alian.json"
        }

        6 -> {
            isLeft = true
            "animation_alian.json"
        }

        else -> {
            isLeft = true
            "animation_bez_metly.json"
        }
    }
    val enemyComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.Asset(
            enemyLottieAsset
        )
    )

    val clownComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.Asset(
            if (isBobrComposition)"animation_bobr.json"  else if (isCappuccinoComposition) {
                "animation_cappuccino.json"
            } else  "animation_clown.json"
        )
    )
    val waterComposition by rememberLottieComposition(spec = LottieCompositionSpec.Asset("animation_smoke.json"))
    val fireComposition by rememberLottieComposition(spec = LottieCompositionSpec.Asset("animation_fire.json"))
    val catComposition by rememberLottieComposition(spec = LottieCompositionSpec.Asset("animation_cat.json"))
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
        //  isLeft = file.second
    }

    LaunchedEffect(Unit) {
        while (true) {
            progress = (progress + 0.01f) % 1f // Зацикленная анимация
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

                        val newScale = (screenState.scale * zoom).coerceIn(1f, 10f)
                        viewModel.updateScale(newScale)
                        val maxShift = 300f * (screenState.scale - 1)
                        val newOffset = Offset(
                            (screenState.offset.x + pan.x).coerceIn(-maxShift, maxShift),
                            (screenState.offset.y + pan.y).coerceIn(-maxShift, maxShift)
                        )
                        viewModel.updateOffset(newOffset)
                    }
                }
        ) {
            val fpsState = FpsOverlay()

            Canvas(Modifier.fillMaxSize()) {
                /*drawIntoCanvas { canvas ->
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 28f
                        isAntiAlias = true
                    }
                    canvas.nativeCanvas.drawText(
                        "FPS: ${fpsState.value}",
                        size.width - 160f,
                        40f,
                        paint
                    )
                }*/

                viewModel.updateCanvasSize(size)
                val center = Offset(
                    size.width / 2 + screenState.offset.x,
                    size.height / 2 + screenState.offset.y
                )
                val radius = 300f * screenState.scale

                /*    val targetScreenPosition = Offset(
                        center.x + screenState.targetPosition.x * screenState.scale,
                        center.y + screenState.targetPosition.y * screenState.scale
                    )*/
                /*  val enemyScreenPosition = Offset(
                      center.x + screenState.enemyPosition.x * screenState.scale,
                      center.y + screenState.enemyPosition.y * screenState.scale
                  )*/
                /*  val fighterScreenPosition = Offset(
                      center.x + screenState.fighterPosition.x * screenState.scale,
                      center.y + screenState.fighterPosition.y * screenState.scale
                  )*/
                val enemyScreenPosition =
                    toScreen(screenState.enemyPosition, center, screenState.scale)
                val fighterScreenPosition =
                    toScreen(screenState.fighterPosition, center, screenState.scale)

                if (screenState.routePoints.isNotEmpty()) {
                    //отрисовка маршрута
                    if (gameLevel == 11) {
                        val screenPosFire =
                            center + screenState.routePoints.last() * screenState.scale
                        drawRouteWithDeviationZone(
                            route = screenState.routePoints,
                            center = center,
                            scale = screenState.scale,
                            deviationRadius = 20f * screenState.scale,
                            started = isAtStart,
                            isInWrongZone = !isInLegalRoute,
                            isInStart = fighterScreenPosition.getDistance(screenState.routePoints.first()) < 15f,
                            isInFinish = fighterScreenPosition.getDistance(screenState.routePoints.last()) < 15f,
                            fireProgress = fireProgress
                        )
                        drawHouseImageCentered(
                            houseImage,
                            screenPosFire,
                            80f,
                            screenState.scale * 3/*screenState.aircraftSizeFactor*/
                        )
                        //пламя пожара
                        if (screenState.isFireActive) {
                            drawLottieAnimation(
                                composition = fireComposition,
                                progress = progress,
                                position = screenPosFire,
                                baseSize = 10f,
                                fighterAngle = -90f, //screenState.enemyAngle + 90f, // Ориентируем вдоль туловища
                                isLeft = true,
                                scale = 1f + screenState.scale *
                                        /*screenState.aircraftSizeFactor **/
                                        screenState.fireProgressSize * 20,
                                centerOffset = 0.3f, // Подгоняешь смещение сам
                            )
                        }
                        val helpPoint =
                            center + (getHelpPoint(screenState.routePoints)?.times(screenState.scale)
                                ?: (screenState.routePoints.last() * screenState.scale))
                        // 🔹 HELP над последней точкой
                        val timeMillis = System.currentTimeMillis() % 1000
                        val alpha = if (timeMillis < 500) 1f else 0f // мигает раз в полсекунды
                        drawIntoCanvas { canvas ->
                            val paint = android.graphics.Paint().apply {
                                color = android.graphics.Color.RED
                                textSize = 18f * screenState.scale
                                textAlign = android.graphics.Paint.Align.CENTER
                                isFakeBoldText = true
                                this.alpha = (alpha * 255).toInt()
                            }

                            canvas.nativeCanvas.drawText(
                                "SOS",
                                helpPoint.x,
                                helpPoint.y + 24f,
                                paint
                            )
                        }
                        drawIntoCanvas { canvas ->
                            val textSize = 18f * screenState.scale
                            val x = helpPoint.x
                            val y = helpPoint.y + 24f

                            // Сначала рисуем обводку
                            val strokePaint = android.graphics.Paint().apply {
                                color = android.graphics.Color.BLACK         // Цвет обводки
                                this.textSize = textSize
                                textAlign = android.graphics.Paint.Align.CENTER
                                style = android.graphics.Paint.Style.STROKE
                                strokeWidth = 4f                             // Толщина обводки
                                isAntiAlias = true
                                isFakeBoldText = true
                                this.alpha = (alpha * 255).toInt()
                            }

                            // Потом рисуем сам текст
                            val fillPaint = android.graphics.Paint().apply {
                                color = android.graphics.Color.RED           // Основной цвет текста
                                this.textSize = textSize
                                textAlign = android.graphics.Paint.Align.CENTER
                                style = android.graphics.Paint.Style.FILL
                                isAntiAlias = true
                                isFakeBoldText = true
                                this.alpha = (alpha * 255).toInt()
                            }

                            canvas.nativeCanvas.drawText("SOS", x, y, strokePaint)
                            canvas.nativeCanvas.drawText("SOS", x, y, fillPaint)
                        }

                        // сброс воды
                        if (screenState.isWaterDropping) {
                            /*       screenState.waterTrailPoints.forEachIndexed { index, trailPos ->
                                       val p = center + trailPos * screenState.scale
                                       drawCircle(
                                           color = Color.Cyan.copy(alpha = 0.4f),
                                           center = p,
                                           radius = waterRadiusProgress / (index + 1)
                                       )
                                   }*/
                            drawCircle(
                                color = Color.Cyan.copy(alpha = 0.4f),
                                center = fighterScreenPosition,
                                radius = waterRadiusProgress * screenState.aircraftSizeFactor
                            )

                            drawLottieAnimation(
                                composition = waterComposition,
                                progress = progress,
                                position = fighterScreenPosition,
                                baseSize = 40f, // Базовый размер самолетика
                                fighterAngle = screenState.fighterAngle,
                                isLeft = isLeft,
                                // tint = enemyColor,
                                scale = screenState.scale * 4f * screenState.aircraftSizeFactor //screenState.aircraftSizeFactor
                            )
                        }


                    }
                }

                // эффект молнии авада кедавра
                val beam = screenState.magicBeam
                if (beam != null) {
                    val shieldRadius = 40.dp.toPx()
                    val hitShield = isEnemyShieldActive && isAvadaBlockedByShield(
                        start = beam.from,
                        end = beam.to,
                        shieldCenter = enemyScreenPosition,
                        shieldAngle = screenState.enemyAngle,
                        shieldRadius = shieldRadius
                    )

                    val finalPath = if (hitShield) {
                        // Укорачиваем луч до щита
                        val dir = (beam.to - beam.from).normalize()
                        val shortenedEnd = enemyScreenPosition - dir * shieldRadius
                        generateLightningPath(beam.from, shortenedEnd)
                    } else {
                        beam.path
                    }

                    drawPath(
                        path = Path().apply {
                            moveTo(finalPath.first().x, finalPath.first().y)
                            finalPath.drop(1).forEach { lineTo(it.x, it.y) }
                        },
                        color = Color.Red,
                        style = Stroke(width = 6f)
                    )

                    explosionPosition = enemyScreenPosition
                }


                // Отрисовка ракет истребителя
                rockets.forEachIndexed { index, rocket ->
                    /*   val rocketScreenPos = Offset(
                           center.x + rocket.position.x * screenState.scale,
                           center.y + rocket.position.y * screenState.scale
                       )*/
                    val rocketScreenPos = toScreen(rocket.position, center, screenState.scale)
                    drawCircle(Color.White, 3f * screenState.scale, rocketScreenPos)
                }

                // Отрисовка ракет врага
                enemyRockets.forEach { rocket ->
                    /*  val rocketScreenPos = Offset(
                          center.x + rocket.position.x * screenState.scale,
                          center.y + rocket.position.y * screenState.scale
                      )*/
                    val rocketScreenPos = toScreen(rocket.position, center, screenState.scale)
                    drawCircle(Color.Blue, 3f * screenState.scale, rocketScreenPos)
                }
                // отрисовка самонаводящихся ракет истребителя
                homingRockets.forEach { rocket ->
                    /*val rocketScreenPos = Offset(
                        center.x + rocket.position.x * screenState.scale,
                        center.y + rocket.position.y * screenState.scale
                    )*/
                    val rocketScreenPos = toScreen(rocket.position, center, screenState.scale)

                    val rocketSize = 10f * screenState.scale
                    val angleRad = Math.toRadians((rocket.angle - 90).toDouble()).toFloat()

                    // Смещаем позицию, чтобы рисовать с учетом угла
                    val nose = Offset(
                        x = rocketScreenPos.x + rocketSize * cos(angleRad),
                        y = rocketScreenPos.y + rocketSize * sin(angleRad)
                    )
                    val tailLeft = Offset(
                        x = rocketScreenPos.x + rocketSize * 0.5f * cos(angleRad + PI.toFloat() / 2),
                        y = rocketScreenPos.y + rocketSize * 0.5f * sin(angleRad + PI.toFloat() / 2)
                    )
                    val tailRight = Offset(
                        x = rocketScreenPos.x + rocketSize * 0.5f * cos(angleRad - PI.toFloat() / 2),
                        y = rocketScreenPos.y + rocketSize * 0.5f * sin(angleRad - PI.toFloat() / 2)
                    )

                    // Треугольная форма ракеты (нос и два хвоста)
                    /*    drawPath(
                            path = Path().apply {
                                moveTo(nose.x, nose.y)
                                lineTo(tailLeft.x, tailLeft.y)
                                lineTo(tailRight.x, tailRight.y)
                                close()
                            },
                            color = Color.Cyan,
                        )*/
                    val imageSize = 24f * screenState.scale // или другой подходящий размер
                    val angle = rocket.angle
                    withTransform({
                        rotate(
                            degrees = angle,
                            pivot = rocketScreenPos
                        )
                        scale(
                            scaleX = imageSize / rocketImage.width,
                            scaleY = imageSize / rocketImage.height,
                            pivot = rocketScreenPos
                        )
                    }) {
                        drawImage(
                            image = rocketImage,
                            topLeft = rocketScreenPos - Offset(
                                rocketImage.width / 2f,
                                rocketImage.height / 2f
                            )
                        )
                    }
                }
                //отрисовка щита истребителя
                if (screenState.isShieldActive) {
                    val shieldRadius = 40f * screenState.scale
                    val shieldAngleStart = screenState.fighterAngle - 90 - 60
                    val shieldAngleSweep = 120f

                    val shieldCenter = fighterScreenPosition

                    drawArc(
                        color = Color.Cyan.copy(alpha = 0.5f),
                        startAngle = shieldAngleStart,
                        sweepAngle = shieldAngleSweep,
                        useCenter = false,
                        topLeft = Offset(
                            shieldCenter.x - shieldRadius,
                            shieldCenter.y - shieldRadius
                        ),
                        size = androidx.compose.ui.geometry.Size(
                            shieldRadius * 2,
                            shieldRadius * 2
                        ),
                        style = Stroke(width = 3.dp.toPx())
                    )
                }

                // отрисовка щита врага
                if (isEnemyShieldActive) {
                    val shieldRadius = 40f * screenState.scale
                    val shieldAngleStart = screenState.enemyAngle - 90 - 60
                    val shieldAngleSweep = 120f

                    val shieldCenter = enemyScreenPosition

                    drawArc(
                        color = Color.Cyan.copy(alpha = 0.5f),
                        startAngle = shieldAngleStart,
                        sweepAngle = shieldAngleSweep,
                        useCenter = false,
                        topLeft = Offset(
                            shieldCenter.x - shieldRadius,
                            shieldCenter.y - shieldRadius
                        ),
                        size = androidx.compose.ui.geometry.Size(
                            shieldRadius * 2,
                            shieldRadius * 2
                        ),
                        style = Stroke(width = 3.dp.toPx())
                    )
                }

                // Отрисовка взрыва
                explosion?.let { exp ->
                    val explosionScreenPos = toScreen(exp.position, center, screenState.scale)
                    drawCircle(
                        exp.color.copy(
                            alpha = exp.alpha
                        ),
                        exp.radiusCoefficient * screenState.scale,
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
                }*/

                /* if (gameLevel == 11) {
                     //отрисовка пожара
                     val helpPoint =
                         center + (getHelpPoint(screenState.routePoints)?.times(screenState.scale)
                             ?: (screenState.routePoints.last() * screenState.scale))

                     //домики  и кот
                     fireProgressSize = (firePoints.filter { it.isActive }.size.toFloat() / 5)
                     firePoints.forEachIndexed { index, point ->
                         val screenPos = center + point.position * screenState.scale
                         *//* if (index % 2 == 0)
                             drawHouseImageCentered(houseImage, screenPos, 80f, screenState.scale*screenState.aircraftSizeFactor)*//*
                        if (point == firePoints.last()) {
                            drawHouseImageCentered(
                                houseImage,
                                screenPos,
                                80f,
                                screenState.scale * screenState.aircraftSizeFactor
                            )
                            if (fireProgressSize > 0)
                                drawLottieAnimation(
                                    composition = fireComposition,
                                    progress = progress,
                                    position = screenPos,
                                    baseSize = 60f,
                                    fighterAngle = -90f, //screenState.enemyAngle + 90f, // Ориентируем вдоль туловища
                                    isLeft = true,
                                    scale = screenState.scale * screenState.aircraftSizeFactor * fireProgressSize,
                                    centerOffset = 0.3f, // Подгоняешь смещение сам
                                )
                        }

                        // 🔹 HELP над последней точкой
                        val timeMillis = System.currentTimeMillis() % 1000
                        val alpha = if (timeMillis < 500) 1f else 0f // мигает раз в полсекунды
                        drawIntoCanvas { canvas ->
                            val paint = android.graphics.Paint().apply {
                                color = android.graphics.Color.RED
                                textSize = 18f * screenState.scale
                                textAlign = android.graphics.Paint.Align.CENTER
                                isFakeBoldText = true
                                this.alpha = (alpha * 255).toInt()
                            }

                            canvas.nativeCanvas.drawText(
                                "SOS",
                                helpPoint.x,
                                helpPoint.y + 24f,
                                paint
                            )
                        }
                        drawIntoCanvas { canvas ->
                            val textSize = 18f * screenState.scale
                            val x = helpPoint.x
                            val y = helpPoint.y + 24f

                            // Сначала рисуем обводку
                            val strokePaint = android.graphics.Paint().apply {
                                color = android.graphics.Color.BLACK         // Цвет обводки
                                this.textSize = textSize
                                textAlign = android.graphics.Paint.Align.CENTER
                                style = android.graphics.Paint.Style.STROKE
                                strokeWidth = 4f                             // Толщина обводки
                                isAntiAlias = true
                                isFakeBoldText = true
                                this.alpha = (alpha * 255).toInt()
                            }

                            // Потом рисуем сам текст
                            val fillPaint = android.graphics.Paint().apply {
                                color = android.graphics.Color.RED           // Основной цвет текста
                                this.textSize = textSize
                                textAlign = android.graphics.Paint.Align.CENTER
                                style = android.graphics.Paint.Style.FILL
                                isAntiAlias = true
                                isFakeBoldText = true
                                this.alpha = (alpha * 255).toInt()
                            }

                            canvas.nativeCanvas.drawText("SOS", x, y, strokePaint)
                            canvas.nativeCanvas.drawText("SOS", x, y, fillPaint)
                        }

                    }

                    *//* //огоньки
                     firePoints.filter { it.isActive }.forEach { point ->
                         val screenPos = center + point.position * screenState.scale
                         drawCircle(
                             color = Color.Red,
                             center = screenPos,
                             radius = 6f
                         )

                         *//**//*drawLottieAnimation(
                            composition = fireComposition,
                            progress = progress,
                            position = screenPos,
                            baseSize = 60f,
                            fighterAngle = -90f, //screenState.enemyAngle + 90f, // Ориентируем вдоль туловища
                            isLeft = true,
                            scale = screenState.scale*screenState.aircraftSizeFactor,
                            centerOffset = 0.3f, // Подгоняешь смещение сам
                        )*//**//*
                        if (point == firePoints.last()) {
                            drawLottieAnimation(
                                composition = compositionBabah,
                                progress = progress,
                                position = helpPoint,
                                baseSize = 60f,
                                fighterAngle = -90f, //screenState.enemyAngle + 90f, // Ориентируем вдоль туловища
                                isLeft = true,
                                scale = screenState.scale,
                                centerOffset = 0.3f, // Подгоняешь смещение сам
                            )
                        }

                    }*//*
                    // сброс воды
                    if (screenState.isWaterDropping) {
                        *//* screenState.waterTrailPoints.forEachIndexed { index, trailPos ->
                             val p = center + trailPos * screenState.scale
                             drawCircle(
                                 color = Color.Cyan.copy(alpha = 0.4f),
                                 center = p,
                                 radius = waterRadiusProgress / (index + 1)
                             )
                         }*//*
                        drawLottieAnimation(
                            composition = waterComposition,
                            progress = progress,
                            position = fighterScreenPosition,
                            baseSize = 40f, // Базовый размер самолетика
                            fighterAngle = screenState.fighterAngle,
                            isLeft = isLeft,
                            // tint = enemyColor,
                            scale = screenState.scale * 1.3f *//*screenState.aircraftSizeFactor*//*
                        )
                    }
                }*/

                // Отрисовываем луч радара
                if (screenState.isRadarLuchVisible) {
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
                }

                // Свечение луча
                /*    if (gameLevel != 11)
                        drawCircle(
                            color = beamColor.copy(alpha = 0.2f),
                            center = center,
                            radius = radius * 0.2f
                        )*/
                // Отображаем следы истребителя
                fighterTrail.forEach {
                    /*val pos =
                        Offset(
                            center.x + it.position.x * screenState.scale,
                            center.y + it.position.y * screenState.scale
                        )*/
                    val pos = toScreen(it.position, center, screenState.scale)

                    drawCircle(
                        color = fighterColor.copy(alpha = it.alpha),
                        radius = 4f * screenState.scale * screenState.aircraftSizeFactor,
                        center = pos
                    )
                }

                // Отображаем следы врага
                enemyTrail.forEach {
                    val pos = toScreen(it.position, center, screenState.scale)
                    drawCircle(
                        color = enemyColor.copy(alpha = it.alpha),
                        radius = 4f * screenState.scale * screenState.aircraftSizeFactor,
                        center = pos
                    )
                }

                // Отображаем сами точки
                if (screenState.displayStyle != DisplayStyle.RAYS_ONLY) {
                    drawCircle(
                        color = fighterColor,
                        radius = 5f * screenState.scale * screenState.aircraftSizeFactor,
                        center = fighterScreenPosition
                    )
                    if (!isEnemyDedState)
                        drawCircle(
                            color = enemyColor,
                            radius = 5f * screenState.scale * screenState.aircraftSizeFactor,
                            center = enemyScreenPosition
                        ) // если задать прозрачный цвет , то будут только отметки от цели после луча
                }

                if (!isFighterBabahState && screenState.displayStyle == DisplayStyle.CHARACTERS) {
                    if (!isEnemyDedState)
                        if (gameLevel == 11 || gameLevel == 4) // подсветка черной ведьмы
                            drawCircle(
                                color = Color.White.copy(alpha = 0.3f),
                                radius = 25f * screenState.scale * screenState.aircraftSizeFactor,
                                center = enemyScreenPosition
                            )
                    if (!isEnemyDedState)
                        drawLottieAnimation(
                            composition = enemyComposition/*compositionRandom*/,
                            progress = progress,
                            position = enemyScreenPosition,
                            baseSize = 40f, // Базовый размер самолетика
                            fighterAngle = screenState.enemyAngle,
                            isLeft = isLeft,
                            // tint = enemyColor,
                            scale = screenState.scale * screenState.aircraftSizeFactor * 1.3f // Масштабирование, как и у точки
                        )

                    drawLottieAnimation(
                        composition = fighterComposition,
                        // tint = Color.Green,
                        progress = progress,
                        position = fighterScreenPosition,
                        baseSize = 40f, // Базовый размер самолетика
                        fighterAngle = screenState.fighterAngle,
                        isLeft = true,
                        scale = screenState.scale * screenState.aircraftSizeFactor * 1.5f, // Масштабирование, как и у точки
                    )
                }

                if (!isFighterBabahState && screenState.displayStyle == DisplayStyle.FIGHTER) {
                    if (!isEnemyDedState)
                        drawFighterImageWithRollEffect(
                            image = enemyJetImage,
                            position = enemyScreenPosition,
                            rollAngle = enemyRollAngle,
                            baseSize = 40f,
                            fighterAngle = screenState.enemyAngle,
                            isLeft = false,
                            scale = screenState.scale * screenState.aircraftSizeFactor
                        )

                    drawFighterImageWithRollEffect(
                        image = fighterJetImage,
                        position = fighterScreenPosition,
                        rollAngle = screenState.rollAngle,
                        baseSize = 40f,
                        fighterAngle = screenState.fighterAngle,
                        isLeft = false,
                        scale = screenState.scale * screenState.aircraftSizeFactor
                    )
                }




                if (isFighterBabahState || isEnemyBabahState) {
                    val explosionRadius = 30f * screenState.scale // Радиус взрыва (подгоняешь сам)
                    val explosionClipPath = Path().apply {
                        addOval(
                            androidx.compose.ui.geometry.Rect(
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
                            fighterAngle = screenState.enemyAngle + 90f, // Ориентируем вдоль туловища
                            isLeft = true,
                            scale = screenState.scale * 2f,
                            centerOffset = 0.3f, // Подгоняешь смещение сам
                        )

                        // Второй полукруг (в противоположную сторону)
                        drawLottieAnimation(
                            composition = compositionBabah,
                            progress = progress,
                            position = explosionPosition,
                            baseSize = 40f,
                            fighterAngle = screenState.enemyAngle - 90f, // В другую сторону от туловища
                            isLeft = true,
                            scale = screenState.scale * 2f,
                            centerOffset = 0.3f, // Подгоняешь смещение сам
                        )
                    }
                }
                if (isEnemyKlownState)
                    drawLottieAnimation(
                        composition = clownComposition,
                        progress = progress,
                        position = enemyScreenPosition,
                        baseSize = if(isCappuccinoComposition)100f else 40f,
                        fighterAngle = -90f, //screenState.enemyAngle + 90f, // Ориентируем вдоль туловища
                        isLeft = true,
                        scale = screenState.scale * screenState.aircraftSizeFactor * 1.3f,
                        centerOffset = 0.3f, // Подгоняешь смещение сам
                    )

            }
        }
    }
}

// Генерация случайной начальной позиции для врага
fun randomEnemyStartPosition(radius: Float, randomEnemySpeed: (Float) -> Unit): Offset {
    randomEnemySpeed(Random.nextFloat() * 0.5f + 1f)
    val angle = Random.nextDouble(0.0, 360.0)
    return Offset(
        radius * cos(Math.toRadians(angle)).toFloat(),
        radius * sin(Math.toRadians(angle)).toFloat()
    )
}

// Вычисление угла для вражеской цели
fun calculateEnemyAngle(position: Offset): Float {
    /* val baseAngle =
         Math.toDegrees(atan2(-position.y.toDouble(), -position.x.toDouble())).toFloat() + 90
     return baseAngle + Random.nextFloat() * 40 - 20*/
    val originalAzimuth = calculateAzimuth(position)
    val oppositeAzimuth = (originalAzimuth + 180f) % 360f
    val randomCourse = (oppositeAzimuth + Random.nextFloat() * 120f - 60f + 360f) % 360f
    return randomCourse
}

fun calculateAzimuth(position: Offset): Float {
    val angle = Math.toDegrees(atan2(position.x.toDouble(), -position.y.toDouble())).toFloat()
    return (angle + 360f) % 360f
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

// для самонаводящихся ракет
data class HomingRocket(
    var position: Offset,
    var angle: Float,
    val speed: Float = 6f,
    val launchTime: Long = System.currentTimeMillis(),
    val lifespan: Long = 5000L,
    val maxRotationPerStep: Float = 4f,
)


//для вспышки
data class Explosion(
    val position: Offset,
    var alpha: Float = 1f,
    var color: Color = Color.Yellow,
    var radiusCoefficient: Float = 20f,
)

fun getRandomAnimFile(): Pair<String, Boolean> {
    return listOf(
        /*       Pair("animation_panda.json", false),
               Pair("animation_dobraya_vedma.json", true),
               Pair("animation_nlo.json", false),
               Pair("aladdin_flying.json", false),
               Pair("animation_vedma.json", false),
               Pair("anim_yak.json", false),*/
        Pair("animation_astronaut.json", false),

        ).random()
}

fun randomStartPosition(
    radius: Float,
    lastEnemyPosition: Offset,
): Offset {
    // Центр радара (ноль)
    val lastAngle = Math.toDegrees(atan2(lastEnemyPosition.y, lastEnemyPosition.x).toDouble())
        .let { if (it < 0) it + 360 else it } // нормализуем в 0..360

    // Центр противоположного полукруга
    val oppositeCenter = (lastAngle + 180) % 360

    // Случайный угол в противоположной полусфере ±90°
    val newAngle = (oppositeCenter + Random.nextDouble(-90.0, 90.0)).let {
        (it + 360) % 360
    }

    return Offset(
        radius * cos(Math.toRadians(newAngle)).toFloat(),
        radius * sin(Math.toRadians(newAngle)).toFloat()
    )
}

fun calculateAngleToTarget(from: Offset, to: Offset): Float {
    val dx = to.x - from.x
    val dy = to.y - from.y
    val angleRad = atan2(dy, dx)
    val angleDeg = Math.toDegrees(angleRad.toDouble()).toFloat()
    return (angleDeg + 90f + 360f) % 360f
}

fun smoothRotateTowards(current: Float, target: Float, maxRotationPerStep: Float): Float {
    val diff = ((target - current + 540f) % 360f) - 180f
    val clampedDiff = diff.coerceIn(-maxRotationPerStep, maxRotationPerStep)
    return (current + clampedDiff + 360f) % 360f
}

fun isAvadaBlockedByShield(
    start: Offset,
    end: Offset,
    shieldCenter: Offset,
    shieldAngle: Float,
    shieldRadius: Float,
): Boolean {
    val deltaX = start.x - shieldCenter.x
    val deltaY = start.y - shieldCenter.y
    val angleToAvada = Math.toDegrees(atan2(deltaY, deltaX).toDouble()).toFloat()
    val correctedAngle = (shieldAngle - 90 + 360) % 360
    val angleDiff = (angleToAvada - correctedAngle + 360) % 360
    val dist = end.getDistance(shieldCenter)

    return (dist <= shieldRadius && (angleDiff in -60f..60f || angleDiff in 300f..360f))
}

fun Offset.normalize(): Offset {
    val length = getDistance(Offset.Zero)
    return if (length == 0f) this else this / length
}

// для оптимизации перерасчета
fun toScreen(pos: Offset, center: Offset, scale: Float): Offset {
    return center + pos * scale
}

@Composable
fun rememberRocketImage(): ImageBitmap {
    val context = LocalContext.current
    return remember {
        ImageBitmap.imageResource(context.resources, R.drawable.img_rocket_main)
    }
}

fun calculateEnemyAzimuth(from: Offset, to: Offset): Float {
    val dx = to.x - from.x
    val dy = to.y - from.y
    val angleRad = atan2(dx.toDouble(), -dy.toDouble()) // -dy чтобы 0° был на север
    val angleDeg = Math.toDegrees(angleRad).toFloat()
    return (angleDeg + 360f) % 360f
}