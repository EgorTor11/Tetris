package com.dogfight.magic.game_ui.widgets.voice

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun VoiceSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val trackColor = if (checked)  MaterialTheme.colorScheme.tertiary else Color(0xFFE0E0E0)
    val thumbColor = if (checked) MaterialTheme.colorScheme.onTertiaryContainer else Color.LightGray
    val iconColor = if (checked) Color.White else Color.DarkGray

    Switch(
        modifier = Modifier,
        checked = checked,
        onCheckedChange = onCheckedChange,
        thumbContent = {
            Icon(
                imageVector = if (checked) Icons.Default.Mic else Icons.Default.MicOff,
                contentDescription = "Voice",
                tint = iconColor,
                modifier = Modifier.size(16.dp)
            )
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor = thumbColor,
            checkedTrackColor = trackColor,
            uncheckedThumbColor = thumbColor,
            uncheckedTrackColor = trackColor
        )
    )
}
