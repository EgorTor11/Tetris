package com.dogfight.magic.settings_screen.widjets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dogfight.magic.R
import kotlin.math.roundToInt

//${"%.3f".format(fadeFactor)} расчет показа цифры затухания
@Composable
fun TrailFadeSlider(fadeFactor: Float, onFadeFactorChange: (Float) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(stringResource(R.string.trace_fading), color = Color.Black, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(
                stringResource(R.string.fast),
                modifier = Modifier.weight(1f),
                color = Color.Blue.copy(alpha = 0.4f)
            )
            Text(
                stringResource(R.string.slow),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End,
                color = Color.Blue.copy(alpha = 0.4f)
            )
        }
        Slider(
            value = fadeFactor,
            onValueChange = onFadeFactorChange,
            valueRange = 0.97f..0.999f,
            steps = 9
        )
    }
}

@Composable
fun BurstFireSlider(
    text: String = "",
    burstCount: Int,
    onBurstCountChange: (Int) -> Unit,
) {
    Column {
        Text(
            text = "$text: $burstCount",
            color = Color.Blue.copy(alpha = 0.4f),
            style = MaterialTheme.typography.bodyLarge,
        )
        Slider(
            value = burstCount.toFloat(),
            onValueChange = { newValue ->
                onBurstCountChange(newValue.roundToInt())
            },
            valueRange = 1f..100f,
            steps = 98 // 100 - 1 - 1
        )
    }
}
@Composable
fun KrenSlider(
    text: String = "",
    burstCount: Int,
    onBurstCountChange: (Int) -> Unit,
) {
    Column {
        Text(
            text = "$text: $burstCount",
            color = Color.Blue.copy(alpha = 0.4f),
            style = MaterialTheme.typography.bodyLarge,
        )
        Slider(
            value = burstCount.toFloat(),
            onValueChange = { newValue ->
                onBurstCountChange(newValue.roundToInt())
            },
            valueRange = 1f..80f,
            steps = 78  // 100 - 1 - 1
        )
    }
}
