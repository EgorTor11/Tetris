package com.dogfight.magic.game_ui.radar.upravlenie.buttons

// NeonActionButtons.kt

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dogfight.magic.R
import com.dogfight.magic.game_ui.radar.mvi_radar.RadarViewModel

private val NeonShape = RoundedCornerShape(14.dp)
private val EnabledGlow = Color(0xFF00FFF0)
private val DisabledGlow = Color(0xFF444444)

/** Универсальный неоновый action‑button */
@Composable
private fun NeonActionButton2(
    count: Int = 0,
    label: String,
    iconRes: Int,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val glow = if (enabled) EnabledGlow else DisabledGlow
    Box(
        modifier = Modifier
            .width(56.dp)
            .height(56.dp)
            .padding(4.dp)
            .background(Color.Transparent, NeonShape)
            .border(2.dp, glow, NeonShape)
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painterResource(iconRes),
                contentDescription = label,
                tint = glow,
                modifier = Modifier.size(28.dp)
            )
          //  Spacer(Modifier.height(1.dp))
            Text(label, fontSize = 11.sp, color = glow)
        }
    }
}

/* ---------- Конкретные кнопки ---------- */


@Composable
fun AvadaButton(count: Int=0,enabled: Boolean = true, onClick: () -> Unit) = NeonActionButton(
    countText = "$count",
    smileText = "\uD83E\uDDD9⚡",
    iconRes = R.drawable.img_coldun,   // замени на свою иконку
    lottieAsset =null ,//"animation_magic_palka.json",   // лежит в src/main/assets/
    enabled = enabled,
    onClick = onClick
)

@Composable
fun BattleTurnButton(count: Int=0,enabled: Boolean = true, onClick: () -> Unit) = NeonActionButton(
    countText = "$count",
    iconRes = R.drawable.img_turn_svoy,    // замени
    enabled = enabled,
    onClick = onClick,
    lottieAsset = null
)

@Composable
fun ReverseEnemyButton(count: Int = 0, enabled: Boolean = true, onClick: () -> Unit) =
    NeonActionButton(
        modifier = Modifier.padding(10.dp),
        countText = count.toString(),
        iconRes = R.drawable.img_boevoy_razvorot_vrag, // замени
        enabled = enabled,
        onClick = onClick,
        lottieAsset = null
    )

@Composable
fun SuperRocketButton(count: Int = 0, enabled: Boolean = true, onClick: () -> Unit) =
    NeonActionButton(
        countText = count.toString(),
        iconRes = R.drawable.img_superrocket, // замени
        enabled = enabled,
        onClick = onClick,
        lottieAsset = null
    )

@Composable
fun RocketButton(count: Int = 0, enabled: Boolean = true, onClick: () -> Unit) =
    NeonActionButton(
        countText = count.toString(),
        iconRes = R.drawable.img_sectorrocket, // замени
        enabled = enabled,
        onClick = onClick,
        lottieAsset = null
    )

/* ---------- Пример размещения ряда кнопок ---------- */
@Composable
fun MagicActionBar(
    viewModel: RadarViewModel,
    onAvada: () -> Unit,
    onTurn: () -> Unit,
    onEnemyReverse: () -> Unit,
    onSupperRocketClick: () -> Unit,
    onRocketClick: () -> Unit,
) {
    val radarUiState by viewModel.screenState.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AvadaButton(count = radarUiState.avadaCount, enabled = radarUiState.isInAvadaRange) { onAvada() }
        BattleTurnButton(count = radarUiState.turnCount) { onTurn() }
        ReverseEnemyButton(count = radarUiState.reverseCount) { onEnemyReverse() }
        SuperRocketButton(count = radarUiState.superRocketCount) { onSupperRocketClick() }
        RocketButton(
            count = radarUiState.homingCount,
            enabled = radarUiState.isInLockZone
        ) { onRocketClick() }
    }
}
