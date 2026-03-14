package com.dogfight.magic.settings_screen

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogfight.magic.game_ui.radar.upravlenie.repository.ControlRepository
import com.dogfight.magic.settings_screen.widjets.VoiceCommandType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RadarSettingsViewModel @Inject constructor(
    private val repository: ControlRepository,
) : ViewModel() {

    private val _settingsUiState = MutableStateFlow(RadarSettingsState())
    val settingsUiState = _settingsUiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.radarSettingsFlow.collect { settings ->
                _settingsUiState.value = settings
            }
        }
    }

    fun updateVoiceCommand(type: VoiceCommandType, phrase: String) {
        viewModelScope.launch {
            repository.setVoiceCommand(type, phrase)
        }
    }

    fun updateTrailFadeFactor(value: Float) {
        viewModelScope.launch {
            repository.updateTrailFadeFactor(value)
        }
    }

    fun setRadarScale(scale: Float) {
        viewModelScope.launch {
            repository.saveRadarScale(scale)
        }
    }

    fun setAircraftSizeFactor(factor: Float) {
        viewModelScope.launch {
            repository.setAircraftSizeFactor(factor)
        }
    }
    fun setShellsInSalvoFireButton(count: Int) {
        viewModelScope.launch {
            repository.setFireButtonShellsInSalvo(count)
        }
    }
    fun setShellsInSalvoRoot(count: Int) {
        viewModelScope.launch {
            repository.setRootShellsInSalvo(count)
        }
    }
    fun setKrenVoice(kren: Int) {
        viewModelScope.launch {
            repository.setKrenVoice(kren)
        }
    }

    fun setBackgroundUri(uri: String) {
        viewModelScope.launch {
            repository.setBackgroundUri(uri)
        }
    }
    fun setBackgroundColor(color: Color) {
        viewModelScope.launch {
            Log.d("setBackgroundColor","settings vm: setBackgroundColor color= $color")
            repository.setBackgroundColor(color)
        }
    }
    fun setEnemyShotSoundActive(isActive: Boolean) {
        viewModelScope.launch {
            repository.setEnemyShotSoundActive(isActive)
        }
    }
    fun setPricelActive(isActive: Boolean) {
        viewModelScope.launch {
            repository.setPricelActive(isActive)
        }
    }
    fun setLuchVisible(isVisible: Boolean) {
        viewModelScope.launch {
            repository.setLuchVisible(isVisible)
        }
    }
    fun setIsCenterDrop(isCenterDrop: Boolean) {
        viewModelScope.launch {
            repository.setIsCenterDrop(isCenterDrop)
        }
    }

    fun setDisplayStyle(style: DisplayStyle) {
        viewModelScope.launch {
            repository.saveDisplayStyle(style)
        }
    }
}

enum class DisplayStyle {
    FIGHTER,
    CHARACTERS,
    DOTS_AND_TRAILS,
    RAYS_ONLY
}
