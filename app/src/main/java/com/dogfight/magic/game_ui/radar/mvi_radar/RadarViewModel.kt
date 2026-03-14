package com.dogfight.magic.game_ui.radar.mvi_radar

import android.net.Uri
import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dogfight.magic.game_ui.radar.mvi_radar.mode_mission.generateRandomRoute
import com.dogfight.magic.game_ui.radar.upravlenie.buttons.NeonButtonModel
import com.dogfight.magic.game_ui.radar.upravlenie.repository.ControlRepository
import com.dogfight.magic.unity_ads.ResourceDepletionType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.atan2

private const val BULLET_RANGE = 200f      // дальность обычной пули (в px)

/*@HiltViewModel*/
class RadarViewModel @AssistedInject constructor(
    private val repository: ControlRepository,
    @Assisted private val gameLevel: Int,
) : ViewModel() {
    // Теперь у тебя есть gameLevel в ViewModel

    private val _uiState = MutableStateFlow(RadarScreenState())
    val screenState: StateFlow<RadarScreenState> = _uiState.asStateFlow()

    private val _commandFlow = MutableSharedFlow<String>()
    val commandFlow = _commandFlow.asSharedFlow()

    private val _singleShotFlow = MutableSharedFlow<String>()
    val singleShotFlow = _singleShotFlow.asSharedFlow()

    private val _singleHomingShotFlow = MutableSharedFlow<String>()
    val singleHomingShotFlow = _singleHomingShotFlow.asSharedFlow()

    private var timerJob: Job? = null


    /*fun activateShield(duration: Long = 5000L) {
       _uiState.update { it.copy(isShieldActive = true) }

       viewModelScope.launch {
           delay(duration)
           _uiState.update { it.copy(isShieldActive = false) }
       }
   }*/
    fun activateShield(duration: Long = 5000L) {
        _uiState.update {
            it.copy(
                isShieldActive = true,
                shieldTimeLeft = 1f
            )
        }


        viewModelScope.launch {
            val steps = 50
            val stepDelay = duration / steps
            for (i in steps downTo 1) {
                delay(stepDelay)
                _uiState.update { it.copy(shieldTimeLeft = i / steps.toFloat()) }
            }
            _uiState.update {
                it.copy(
                    isShieldActive = false,
                    shieldTimeLeft = 0f
                )
            }
        }
    }


    init {
        //  startTimer()
        Log.d("initVievmodel", "initVievmodel")
        viewModelScope.launch {
            combine(
                repository.avadaCountFlow,
                repository.turnCountFlow,
                repository.reverseCountFlow
            ) { a, t, r ->
                _uiState.update {
                    it.copy(
                        avadaCount = a,
                        turnCount = t,
                        reverseCount = r
                    )
                }
            }.collect {}
        }

        viewModelScope.launch {
            repository.radarSettingsFlow.collect { settings ->
                _uiState.update {
                    it.copy(
                        scaleFactorForSpeed = settings.scaleFactorForSpeed,
                        displayStyle = settings.displayStyle,
                        aircraftSizeFactor = settings.aircraftSizeFactor,
                        trailFadeFactor = settings.trailFadeFactor,
                        shellsInSalvoFireButton = settings.shellsInSalvoFireButton,
                        shellsInSalvoRoot = settings.shellsInSalvoRoot,
                        krenVoice = settings.krenVoice.toFloat(),
                        backgroundUri = settings.backgroundUri,
                        backgroundColor = settings.backgroundColor,
                        isEnemyShotSoundActive = settings.isEnemyShotSoundActive,
                        isPricelActive = settings.isPricelActive,
                        isRadarLuchVisible = settings.isRadarLuchVisible,
                        isCenterDrop = settings.isCenterDrop,
                        voiceCommands = settings.voiceCommands
                    )
                }
                Log.d("setBackgroundColor","radar vm : settings.backgroundColor=${settings.backgroundColor},\n _uiState.value.backgroundColor= ${_uiState.value.backgroundColor}")
            }
        }

        viewModelScope.launch {
            repository.voiceCommandFlow.collectLatest { command ->
                updateCommand(command)
            }
        }
        viewModelScope.launch {
            repository.superRocketCountFlow.collect { count ->
                _uiState.update { it.copy(superRocketCount = count) }
            }
        }
        viewModelScope.launch {
            repository.homingCountFlow.collect { count ->
                _uiState.update { it.copy(homingCount = count) }
            }
        }

        viewModelScope.launch {
            repository.rollAngle.collect {
                _uiState.update { state -> state.copy(rollAngle = it) }
            }
        }
        viewModelScope.launch {
            repository.shieldCountFlow.collect { count ->
                _uiState.update { it.copy(shieldCount = count) }
            }
        }
        viewModelScope.launch {
            repository.course.collect {
                _uiState.update { state -> state.copy(course = it) }
            }
        }

        viewModelScope.launch {
            repository.forsagCountFlow.collect { count ->
                _uiState.update { it.copy(forsagCount = count) }
            }
        }
        viewModelScope.launch {
            repository.ammoCountFlow.collect { count ->
                _uiState.update { it.copy(ammoCount = count) }
            }
        }
        var isCollectedStarCount = false
        viewModelScope.launch {
            repository.starCountFlow.collect { count ->
                _uiState.update { it.copy(starCount = count) }
                if (!isCollectedStarCount) {
                    restartGame() // для начала игры тоже норм
                }
                isCollectedStarCount = true
            }
        }
    }
    fun setIsCenterDrop(isCenterDrop: Boolean){
        viewModelScope.launch {
            repository.setIsCenterDrop(isCenterDrop)
        }
    }

    fun updateRouteProgress(fighterPos: Offset, route: List<Offset>) {
        if (screenState.value.currentRouteSegmentIndex >= route.lastIndex) return // маршрут пройден

        val target = route[screenState.value.currentRouteSegmentIndex]
        val distance = fighterPos.getDistance(target)
        if (distance < 10f) { // 10f — радиус зоны попадания в точку
            _uiState.update { it.copy(currentRouteSegmentIndex = it.currentRouteSegmentIndex + 1) }
        }
    }

    fun updateSuperRocketCount(count: Int) {
        viewModelScope.launch {
            repository.setSuperRocketCount(count)
        }
    }

    fun updateAvadaCount(count: Int) = viewModelScope.launch { repository.setAvadaCount(count) }
    fun updateTurnCount(count: Int) = viewModelScope.launch { repository.setTurnCount(count) }
    fun updateReverseCount(count: Int) = viewModelScope.launch { repository.setReverseCount(count) }


    fun decreaseSuperRocket() {
        viewModelScope.launch {
            val current = screenState.value.superRocketCount
            repository.setSuperRocketCount((current - 1).coerceAtLeast(0))
        }
    }

    fun rewardSuperRocket(amount: Int) {
        viewModelScope.launch {
            val current = screenState.value.superRocketCount
            repository.setSuperRocketCount(current + amount)
        }
    }

    fun updateCanvasSize(size: Size) {
        _uiState.update { it.copy(canvasSize = size) }
    }

    fun clearMagicBeam() {
        _uiState.update { it.copy(magicBeam = null) }
    }

    fun startWaterDrop() {
        if (screenState.value.isWaterDropping) return // уже идёт сброс
        val waterTrailPoints: MutableList<Offset> = mutableListOf()
        _uiState.update { it.copy(isWaterDropping = true, waterTrailPoints = emptyList()) }
        viewModelScope.launch {
            val dropDuration = 1500L // миллисекунд
            val startTime = System.currentTimeMillis()

            while (System.currentTimeMillis() - startTime < dropDuration) {
                delay(16L)
                waterTrailPoints.add(screenState.value.fighterPosition)
                _uiState.update { it.copy(waterTrailPoints = waterTrailPoints) }
            }
            _uiState.update { it.copy(isWaterDropping = false) }

            checkFirePointsExtinguished()
        }
    }

    fun checkFirePointsExtinguished() {
        val radius = 60f
        val firePoint = screenState.value.routePoints.last()
        for (drop in screenState.value.waterTrailPoints) {
            if ((drop - firePoint).getDistance() <= radius) {
                /* firePoint.isActive = false*/
                _uiState.update { it.copy(fireProgressSize = 0f, isFireActive = false) }
                showGameResult(true)
                break
            }
        }
    }

    fun launchMagicBeamIfInRange() {
        val state = _uiState.value

        val center = Offset(
            state.canvasSize.width / 2 + state.offset.x,
            state.canvasSize.height / 2 + state.offset.y
        )

        val fighterScreen = center + state.fighterPosition * state.scale
        val enemyScreen = center + state.enemyPosition * state.scale

        val distance = fighterScreen.getDistance(enemyScreen)
        if (distance <= BULLET_RANGE * state.scale) {
            val lightningPath = generateLightningPath(fighterScreen, enemyScreen)
            _uiState.update {
                it.copy(magicBeam = MagicBeam(fighterScreen, enemyScreen, lightningPath))
            }
            // Запускаем таймер на 500 мс, чтобы скрыть молнию
            viewModelScope.launch {
                delay(500)
                _uiState.update { it.copy(magicBeam = null) }
            }
        }
    }

    fun showInfoDialog(message: String) {
        _uiState.update {
            it.copy(
                infoDialogMessage = message,
                isPaused = true // ставим на паузу игру при показе
            )
        }
    }

    fun dismissInfoDialog() {
        _uiState.update {
            it.copy(
                infoDialogMessage = null,
                isPaused = false // снимаем паузу после закрытия
            )
        }
    }

    fun updateHomingCount(count: Int) {
        viewModelScope.launch {
            repository.setHomingCount(count)
        }
    }

    fun updateFireProgress(progress: Float) {
        _uiState.update { it.copy(fireProgressSize = progress) }
    }

    fun rewardForResource(type: ResourceDepletionType?, amount: Int) {
        when (type) {
            is ResourceDepletionType.Ammo -> updateAmmoCount(screenState.value.ammoCount + amount)
            is ResourceDepletionType.Homing -> updateHomingCount(screenState.value.homingCount + amount)
            is ResourceDepletionType.Shield -> restoreShield(screenState.value.shieldCount + amount)
            is ResourceDepletionType.Afterburner -> restoreForsag(screenState.value.forsagCount + amount)
            is ResourceDepletionType.SuperRocket -> updateSuperRocketCount(screenState.value.superRocketCount + amount)
            is ResourceDepletionType.Avada -> updateAvadaCount(screenState.value.avadaCount + amount)
            is ResourceDepletionType.Turn -> updateTurnCount(screenState.value.turnCount + amount)
            is ResourceDepletionType.Reverse -> updateReverseCount(screenState.value.reverseCount + amount)
            is ResourceDepletionType.Stars -> addStars(amount)
            null -> {}
        }
    }

    fun closResultDialog() {
        _uiState.update { it.copy(showResultDialog = false) }
    }

    fun unBlockGame() {
        _uiState.update {
            it.copy(
                playerScore = 0,
                enemyScore = 0,
                isPaused = false,
                isBlockGameScreen = false,
                countdownTimer = 120, // или нужное количество секунд
                showResultDialog = false,
                isPlayerWinner = false,
                // опционально: сбрасываем другие поля, если нужно
                rockets = emptyList(),
                enemyRockets = emptyList(),
                fighterTrail = emptyList(),
                enemyTrail = emptyList(),
                explosion = null,
                isBabahState = false,
                isEnemyBabahState = false,
                playerHits = 0,
                enemyHits = 0,
            )
        }
        if (gameLevel != 0 && gameLevel != 11)
            startTimer()
        addStars(gameLevel * (-1))
        updateDepletionType(null)
    }

    fun toogleBlockGameScreen(stavka: Int, starCount: Int) {
        //  showInfoDialog("block")
        if (stavka > starCount) {
            _uiState.update {
                it.copy(
                    isBlockGameScreen = true,
                    isPaused = true // ставим на паузу игру
                )
            }
            updateDepletionType(ResourceDepletionType.Stars)
        } else {
            _uiState.update {
                it.copy(
                    playerScore = 0,
                    enemyScore = 0,
                    isPaused = false,
                    isBlockGameScreen = false,
                    countdownTimer = 120, // или нужное количество секунд
                    showResultDialog = false,
                    isPlayerWinner = false,
                    // опционально: сбрасываем другие поля, если нужно
                    rockets = emptyList(),
                    enemyRockets = emptyList(),
                    fighterTrail = emptyList(),
                    enemyTrail = emptyList(),
                    explosion = null,
                    isBabahState = false,
                    isEnemyBabahState = false,
                    playerHits = 0,
                    enemyHits = 0,
                )
            }
            if (gameLevel != 0 && gameLevel != 11)
                startTimer()
            addStars(stavka * (-1))
            updateDepletionType(null)
        }
    }

    fun decreaseHoming() {
        viewModelScope.launch {
            val current = screenState.value.homingCount
            if (current > 0) {
                repository.setHomingCount(current - 1)
            }
        }
    }

    fun useShield() {
        if (screenState.value.shieldCount > 0) {
            viewModelScope.launch {
                repository.setShieldCount(screenState.value.shieldCount - 1)
                activateShield()
            }
        } else {
            updateDepletionType(ResourceDepletionType.Shield)
        }
    }

    fun restoreShield(count: Int = 1) {
        viewModelScope.launch {
            repository.setShieldCount(screenState.value.shieldCount + count)
        }
    }

    fun addStars(amount: Int) {
        viewModelScope.launch {
            repository.addStarCount(amount)
        }
    }

    fun decreaseStar() {
        addStars(amount = -1)
    }

    fun updateIsInLegalRoute(isInLegalRoute: Boolean) {
        _uiState.update { it.copy(isInLegalRoute = isInLegalRoute) }
    }

    fun restartGame() {
        Log.d("myroute", "restartGame()")
        //для маршрутов
        if (gameLevel == 11) {
            var route = emptyList<Offset>()
            //  updateFireProgress(1f)
            viewModelScope.launch(Dispatchers.Default) {
                Log.d("myroute", "launch(Dispatchers.Default)")
                try {
                    route = generateRandomRoute()
                    _uiState.update { it.copy(routePoints = route) }
                    /*    val firePoints =generateFirePoints(
                            screenState.value.routePoints[screenState.value.routePoints.size - 2],
                            screenState.value.routePoints.last()
                        )
                        _uiState.update { it.copy(firePoints = firePoints) }*/
                    Log.d("myroute", "route=$route")

                } catch (e: Exception) {
                    Log.d("myroute", "$e")
                }
            }
        }
        val stavkaStars = if (gameLevel == 11) 0 else gameLevel
        if (gameLevel in 1..5)
            if (stavkaStars > screenState.value.starCount) {
                toogleBlockGameScreen(stavkaStars, screenState.value.starCount)
                return
            }

        addStars(stavkaStars * (-1))
        _uiState.update {
            it.copy(
                fireProgressSize = 0f,
                isFireActive = true,
                playerScore = 0,
                enemyScore = 0,
                isPaused = false,
                countdownTimer = 120, // или нужное количество секунд
                showResultDialog = false,
                isPlayerWinner = false,
                // опционально: сбрасываем другие поля, если нужно
                rockets = emptyList(),
                enemyRockets = emptyList(),
                fighterTrail = emptyList(),
                enemyTrail = emptyList(),
                explosion = null,
                isBabahState = false,
                isEnemyBabahState = false,
                playerHits = 0,
                enemyHits = 0,
            )
        }
        if (gameLevel != 0 && gameLevel != 11)
            startTimer()
    }

    fun updateAmmoCount(count: Int) {
        viewModelScope.launch {
            repository.setAmmoCount(count)
        }
    }

    fun decreaseAmmo() {
        viewModelScope.launch {
            val current = screenState.value.ammoCount
            repository.setAmmoCount(current - 1)
        }
    }

    fun toggleAfterburner() {
        val state = screenState.value

        if (state.isAfterburnerActive) {
            _uiState.update {
                it.copy(
                    isAfterburnerActive = false,
                    fighterSpeed = 2 * it.throttleSlider,
                    afterburnerProgress = 0f
                )
            }
            return
        }

        if (state.forsagCount <= 0) {
            updateDepletionType(ResourceDepletionType.Afterburner)
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isAfterburnerActive = true,
                    fighterSpeed = state.fighterSpeed * 7f,
                )
            }
            decreaseForsag()
            for (step in 0..100) {
                if (!screenState.value.isAfterburnerActive) {
                    _uiState.update {
                        it.copy(afterburnerProgress = 0f)
                    }
                    return@launch
                }
                delay(50)
                _uiState.update {
                    it.copy(afterburnerProgress = 1f - step / 100f)
                }
            }
            _uiState.update {
                it.copy(
                    isAfterburnerActive = false,
                    fighterSpeed = 2 * it.throttleSlider,
                    afterburnerProgress = 0f
                )
            }
        }

    }


    fun decreaseForsag() {
        viewModelScope.launch {
            val current = screenState.value.forsagCount
            repository.setForsagCount(current - 1)
        }
    }

    fun restoreForsag(count: Int) {
        viewModelScope.launch {
            repository.setForsagCount(count)
        }
    }

    // Timer
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.countdownTimer > 0) {
                delay(1000L)
                if (!_uiState.value.isPaused) {
                    _uiState.update { it.copy(countdownTimer = it.countdownTimer - 1) }
                }
            }
            if (_uiState.value.countdownTimer == 0) {
                setPause(true)
                val isWin = when {
                    _uiState.value.playerScore > _uiState.value.enemyScore -> true
                    _uiState.value.playerScore < _uiState.value.enemyScore -> false
                    else -> null
                }
                showGameResult(
                    isWinner =
                        isWin
                )
            }
        }
    }

    fun resetTimer(seconds: Int = 120) {
        _uiState.update { it.copy(countdownTimer = seconds) }
        startTimer()
    }

    fun togglePause() {
        _uiState.update { it.copy(isPaused = !it.isPaused) }
    }

    fun setPause(paused: Boolean) {
        _uiState.update { it.copy(isPaused = paused) }
    }

    fun stopTimer() {
        timerJob?.cancel()
    }

    fun incrementPlayerScore() {
        _uiState.update { it.copy(playerScore = it.playerScore + 1) }
    }

    fun incrementEnemyScore() {
        _uiState.update { it.copy(enemyScore = it.enemyScore + 1) }
    }

    // Throttle
    fun updateThrottleFromSlider(value: Float) {
        _uiState.update {
            it.copy(
                fighterSpeed = 2 * value,
                throttleSlider = value
            )
        }
    }

    fun updateThrottleFromVoice(value: Float) {
        _uiState.update {
            it.copy(
                fighterSpeed = 2 * value,
                throttleSlider = value
            )
        }
    }

    // Fire Button
    fun onFireButtonSelected(model: NeonButtonModel) {
        _uiState.update { it.copy(selectedFireButton = model) }
    }

    // Image
    fun onImageSelected(uri: Uri?) {
        _uiState.update { it.copy(selectedImageUri = uri) }
    }

    // Root click → выстрел на любой элемент
    fun onRootClick() {
        viewModelScope.launch {
            if (screenState.value.ammoCount <= 0) {
                updateDepletionType(ResourceDepletionType.Ammo)
            } else _singleShotFlow.emit("Unit")
        }
    }
    // Root click → выстрел на кнопку огонь
    fun onFireButtonClick() {
        viewModelScope.launch {
            if (screenState.value.ammoCount <= 0) {
                updateDepletionType(ResourceDepletionType.Ammo)
            } else _singleShotFlow.emit("FireButton")
        }
    }

    //захват фокуса
    fun updateFocusState(focus: Boolean?) {
        _uiState.update {
            it.copy(fighterFocus = focus)
        }
    }

    fun calculateAngle(from: Offset, to: Offset): Float {
        val dx = to.x - from.x
        val dy = to.y - from.y
        val angleRad = atan2(dy, dx)
        val angle = Math.toDegrees(angleRad.toDouble()).toFloat()
        return (angle + 450f) % 360f // +90, чтобы 0° стало "вверх"
    }

    fun canLaunchHomingRocket(fighterAngle: Float, toTargetAngle: Float): Boolean {
        val diff = ((toTargetAngle - fighterAngle + 540f) % 360f) - 180f
        return abs(diff) <= 30f
    }

    fun onHomingButtonClick() {
        if (screenState.value.homingCount > 0) {
            viewModelScope.launch {
                _singleHomingShotFlow.emit("Unit")
            }
        } else {
            updateDepletionType(ResourceDepletionType.Homing)
        }
    }

    fun onSupperRocketButtonClick() {
        if (screenState.value.superRocketCount > 0) {
            viewModelScope.launch {
                _singleHomingShotFlow.emit("Unit")
            }
        } else {
            updateDepletionType(ResourceDepletionType.SuperRocket)
        }
    }

    /* ---------- AVADA (магический луч) ---------- */
    fun onAvadaClick() {
        if (screenState.value.avadaCount > 0) {
            launchMagicBeamIfInRange()
            viewModelScope.launch { repository.setAvadaCount(screenState.value.avadaCount - 1) }
        } else {
            updateDepletionType(ResourceDepletionType.Avada)
        }
    }

    /* ---------- БОЕВОЙ РАЗВОРОТ (turn) ---------- */
    fun onBattleTurnClick() {
        if (screenState.value.turnCount > 0) {
            setCourse(screenState.value.course - 180)           // разворот своего
            viewModelScope.launch { repository.setTurnCount(screenState.value.turnCount - 1) }
        } else {
            updateDepletionType(ResourceDepletionType.Turn)
        }
    }

    /* ---------- РЕВЕРС ЦЕЛИ (reverse) ---------- */
    fun onEnemyReverseClick() {
        if (screenState.value.reverseCount > 0) {
            updateEnemyAngle(screenState.value.enemyAngle - 180) // разворот врага
            viewModelScope.launch { repository.setReverseCount(screenState.value.reverseCount - 1) }
        } else {
            updateDepletionType(ResourceDepletionType.Reverse)
        }
    }


    // Когда заканчивается ресурс чего-нибудь
    fun updateDepletionType(type: ResourceDepletionType?) {
        viewModelScope.launch {
            _uiState.update { it.copy(depletionType = type) }
        }
    }

    // Voice command
    fun updateCommand(command: String) {
        viewModelScope.launch {
            _commandFlow.emit(command)
        }
    }

    val course = repository.course.stateIn(
        viewModelScope, SharingStarted.Lazily, 0f
    )

    fun setCourse(value: Float) {
        repository.setCourse(value)
    }

    // Update helpers
    fun updateRollAngle(angle: Float) {
        viewModelScope.launch {
            repository.updateRollAngle(angle)
        }
    }

    fun updateDragStateFromJoystick(isDragStart: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDragStart = isDragStart) }
        }
    }

    fun updateScale(scale: Float) = _uiState.update { it.copy(scale = scale) }
    fun updateOffset(offset: Offset) = _uiState.update { it.copy(offset = offset) }

    fun updateFighterPosition(pos: Offset) {
        val toTargetAngle =
            calculateAngle(_uiState.value.fighterPosition, _uiState.value.enemyPosition)
        val isInLockZone = canLaunchHomingRocket(_uiState.value.fighterAngle, toTargetAngle)

        // дистанция до врага
        val distance = pos.getDistance(screenState.value.enemyPosition)
        val inAvada = distance <= BULLET_RANGE
        _uiState.update {
            it.copy(fighterPosition = pos, isInLockZone = isInLockZone, isInAvadaRange = inAvada)
        }
    }

    fun updateFighterAngle(angle: Float) = _uiState.update {
        val toTargetAngle =
            calculateAngle(_uiState.value.fighterPosition, _uiState.value.enemyPosition)
        val isInLockZone = canLaunchHomingRocket(_uiState.value.fighterAngle, toTargetAngle)
        it.copy(fighterAngle = angle, isInLockZone = isInLockZone)
    }

    fun updateFighterSpeed(speed: Float) =
        _uiState.update { it.copy(fighterSpeed = speed) }


    fun updateFighterTrail(list: List<FadingTrailPoint>) =
        _uiState.update { it.copy(fighterTrail = list) }

    fun updateEnemyPosition(pos: Offset) = _uiState.update { it.copy(enemyPosition = pos) }
    fun updateEnemyAngle(angle: Float) = _uiState.update { it.copy(enemyAngle = angle) }
    fun updateEnemyAzimuth(angle: Float) = _uiState.update { it.copy(enemyAzimuth = angle) }
    fun updateEnemySpeed(speed: Float) =
        _uiState.update { it.copy(enemySpeed = speed) }

    fun updateEnemyTrail(list: List<FadingTrailPoint>) =
        _uiState.update { it.copy(enemyTrail = list) }

    fun updateRockets(list: List<Rocket>) = _uiState.update { it.copy(rockets = list) }
    fun updateEnemyRockets(list: List<Rocket>) =
        _uiState.update { it.copy(enemyRockets = list) }

    fun updateExplosion(exp: Explosion?) = _uiState.update { it.copy(explosion = exp) }
    fun updateExplosionPosition(pos: Offset) =
        _uiState.update { it.copy(explosionPosition = pos) }

    fun updateHits(value: Int) = _uiState.update { it.copy(playerHits = value) }
    fun updateEnemyHits(value: Int) = _uiState.update { it.copy(enemyHits = value) }

    fun setBabahState(value: Boolean) = _uiState.update { it.copy(isBabahState = value) }
    fun setEnemyBabahState(value: Boolean) =
        _uiState.update { it.copy(isEnemyBabahState = value) }

    fun updateTargetPosition(pos: Offset) = _uiState.update { it.copy(targetPosition = pos) }
    fun updateNoisePoints(list: List<Pair<Offset, Float>>) =
        _uiState.update { it.copy(noisePoints = list) }

    fun updateBeamAngle(angle: Float) = _uiState.update { it.copy(beamAngle = angle) }
    fun updateProgress(progress: Float) = _uiState.update { it.copy(progress = progress) }

    fun updateLottieKey(key: String) = _uiState.update { it.copy(lottieKey = key) }
    fun updateIsLeft(isLeft: Boolean) = _uiState.update { it.copy(isLeft = isLeft) }


    fun showGameResult(isWinner: Boolean?) {
        _uiState.update {
            it.copy(
                starCountAfterStartGame = it.starCount
            )
        }
        addStars(
            when (isWinner) {
                true -> {
                    if (gameLevel == 11) 1 else gameLevel * 2
                }

                false -> 0
                null -> {
                    if (gameLevel == 11) 0 else gameLevel
                }
            }
        )

        _uiState.update {
            it.copy(
                showResultDialog = true,
                isPlayerWinner = isWinner,
            )
        }
    }

    fun dismissGameResult() {
        _uiState.update {
            it.copy(showResultDialog = false)
        }
    }

}

@AssistedFactory
interface RadarViewModelFactory {
    fun create(gameLevel: Int): RadarViewModel
}

@EntryPoint
@InstallIn(ActivityComponent::class)
interface RadarViewModelFactoryProvider {
    fun radarViewModelFactory(): RadarViewModelFactory
}


class RadarViewModelFactoryImpl(
    private val assistedFactory: RadarViewModelFactory,
    private val gameLevel: Int,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return assistedFactory.create(gameLevel) as T
    }
}