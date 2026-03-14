package com.dogfight.magic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogfight.magic.game_ui.radar.upravlenie.repository.ControlRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class VoiceCommandViewModel @Inject constructor(
    private val repository: ControlRepository
) : ViewModel() {

    fun updateCommand(command: String) {
        viewModelScope.launch {
            repository.emitVoiceCommand(command)
        }
    }
}