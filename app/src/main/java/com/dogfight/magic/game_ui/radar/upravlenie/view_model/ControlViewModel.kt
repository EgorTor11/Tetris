package com.dogfight.magic.game_ui.radar.upravlenie.view_model

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogfight.magic.game_ui.radar.upravlenie.buttons.NeonButtonModel
import com.dogfight.magic.game_ui.radar.upravlenie.buttons.defaultFireButtonModel
import com.dogfight.magic.game_ui.radar.upravlenie.repository.ControlRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ControlViewModel @Inject constructor(private val repository: ControlRepository) :
    ViewModel() {

    // счет игрока
    private val _playerScore = MutableStateFlow(0)
    val playerScore: StateFlow<Int> = _playerScore

    // счет врага
    private val _enemyScore = MutableStateFlow(0)
    val enemyScore: StateFlow<Int> = _enemyScore


    // Таймер боя
    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused

    private val _timerSeconds = MutableStateFlow(120) // 2 минуты
    val timerSeconds: StateFlow<Int> = _timerSeconds

    private var timerJob: Job? = null

    init {
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerSeconds.value > 0) {
                delay(1000)
                if (!_isPaused.value) {
                    _timerSeconds.update { it - 1 }
                }
            }
        }
    }

    fun togglePause() {
        _isPaused.update { !it }
    }

    fun setPause(paused: Boolean) {
        _isPaused.value = paused
    }

    fun resetTimer(seconds: Int = 120) {
        _timerSeconds.value = seconds
        startTimer()
    }

    fun stopTimer() {
        timerJob?.cancel()
    }


    // Flow для хранения текущей распознанной команды
    private val _commandFlow = MutableSharedFlow<String>()
    val commandFlow = _commandFlow.asSharedFlow()

    private val _singleShotFlow = MutableSharedFlow<String>()
    val singleShotFlow = _singleShotFlow.asSharedFlow()

    private val _throttleSlider = MutableStateFlow(0.25f) // Значение РУД (0.0 - 1.0)
    val throttleSlider: StateFlow<Float> = _throttleSlider.asStateFlow()

    private val _throttleVoice = MutableStateFlow(0.25f) // Значение РУД (0.0 - 1.0)
    val throttleVoice: StateFlow<Float> = _throttleVoice.asStateFlow()


    val rollAngle = repository.rollAngle.stateIn(
        viewModelScope, SharingStarted.Lazily, 0f
    )

    val course = repository.course.stateIn(
        viewModelScope, SharingStarted.Lazily, 0f
    )

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    fun onImageSelected(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    // Состояние для выбранной кнопки/пресета
    private val _selectedFireButton = MutableStateFlow<NeonButtonModel?>(defaultFireButtonModel)
    val selectedFireButton: StateFlow<NeonButtonModel?> = _selectedFireButton

    // Метод для обработки выбора пресета
    fun onFireButtonSelected(fireButtonData: NeonButtonModel) {
        // Устанавливаем выбранный пресет
        _selectedFireButton.value = fireButtonData
    }

    // Метод для обновления команды голосовой
    fun updateCommand(command: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // _commandFlow.tryEmit(command)
            _commandFlow.emit(command)
        }
    }

    fun updateRollAngle(angle: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRollAngle(angle)
        }
    }

    fun onRootClick() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("clickable", "viewModelScope.launch(Dispatchers.IO)")
            // _commandFlow.tryEmit(command)
            _singleShotFlow.emit("Unit")
        }
    }

    fun updateThrottleFromSlider(value: Float) {
        Log.d("speed", "viewModel: updateThrottle value $value")
        _throttleSlider.value = value
    }

    fun updateThrottleFromVoice(value: Float) {
        Log.d("speed", "viewModel: updateThrottle value $value")
        _throttleVoice.value = value
    }

    fun incrementPlayerScore() {
        _playerScore.value++
    }

    fun incrementEnemyScore() {
        _enemyScore.value++
    }
}


