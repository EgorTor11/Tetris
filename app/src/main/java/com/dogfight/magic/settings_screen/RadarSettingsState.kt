package com.dogfight.magic.settings_screen

import com.dogfight.magic.settings_screen.widjets.VoiceCommandType

data class RadarSettingsState(
    val scaleFactorForSpeed: Float = 1f,
    val displayStyle: DisplayStyle = DisplayStyle.FIGHTER,
    val aircraftSizeFactor: Float = 1f,
    val shellsInSalvoFireButton: Int = 5,
    val shellsInSalvoRoot: Int = 5,
    val krenVoice: Int = 60,
    val trailFadeFactor: Float = 0.98f,
    val backgroundUri: String? = null,
    val backgroundColor: Int? = null,
    val isEnemyShotSoundActive: Boolean = false,
    val isPricelActive: Boolean = false,
    val isRadarLuchVisible: Boolean = false,
    val isCenterDrop: Boolean = false,
    val voiceCommands: Map<VoiceCommandType, String> = VoiceCommandType.values()
        .associateWith { it.defaultPhrase },
)