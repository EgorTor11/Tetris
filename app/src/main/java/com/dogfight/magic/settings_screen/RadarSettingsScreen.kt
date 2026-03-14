package com.dogfight.magic.settings_screen

import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.dogfight.magic.R
import com.dogfight.magic.settings_screen.widjets.BackgroundChooser
import com.dogfight.magic.settings_screen.widjets.BurstFireSlider
import com.dogfight.magic.settings_screen.widjets.KrenSlider
import com.dogfight.magic.settings_screen.widjets.NeonEditCommandDialog
import com.dogfight.magic.settings_screen.widjets.PricelChooser
import com.dogfight.magic.settings_screen.widjets.TargetSizeSetting
import com.dogfight.magic.settings_screen.widjets.TrailFadeSlider
import com.dogfight.magic.settings_screen.widjets.VoiceCommandRow
import com.dogfight.magic.settings_screen.widjets.VoiceCommandType
import com.dogfight.magic.settings_screen.widjets.defaultVoiceMap
import com.dogfight.magic.utils.LockScreenOrientation


@Composable
fun RadarSettingsScreen(modifier: Modifier = Modifier) {
    val viewModel: RadarSettingsViewModel = hiltViewModel()
    val uiState by viewModel.settingsUiState.collectAsState()
    //   var isButtonSelected by rememberSaveable { mutableStateOf(false) }
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    LaunchedEffect(Unit) {

    }
    Box {
        Box {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(stringResource(R.string.settings_title), fontSize = 20.sp)

                // Выбор масштаба
                Text(
                    stringResource(R.string.scale_only_trening_title),
                    style = MaterialTheme.typography.bodyLarge,
                )
                RadarScaleSelector(
                    selectedScale = uiState.scaleFactorForSpeed,
                    onScaleChange = { viewModel.setRadarScale(it) }
                )

                // Стиль отображения
                Text(
                    stringResource(R.string.style_target_show_title),
                    style = MaterialTheme.typography.bodyLarge
                )
                Column {
                    listOf(
                        DisplayStyle.CHARACTERS to stringResource(R.string.characters),
                        DisplayStyle.FIGHTER to stringResource(R.string.aircraft),
                        DisplayStyle.DOTS_AND_TRAILS to stringResource(R.string.dots_and_traces),
                        DisplayStyle.RAYS_ONLY to stringResource(R.string.only_traces)
                    ).forEach { (style, label) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = uiState.displayStyle == style,
                                onClick = { viewModel.setDisplayStyle(style) }
                            )
                            Text(label)
                        }
                    }
                    TargetSizeSetting(
                        uiState,
                        scale = uiState.aircraftSizeFactor,
                        onScaleChange = { viewModel.setAircraftSizeFactor(it) })
                }

                TrailFadeSlider(
                    fadeFactor = uiState.trailFadeFactor,
                    onFadeFactorChange = viewModel::updateTrailFadeFactor
                )
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        stringResource(R.string.number_of_rounds_in_queue),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    BurstFireSlider(
                        text = stringResource(R.string.fire_button),
                        burstCount = uiState.shellsInSalvoFireButton,
                        onBurstCountChange = viewModel::setShellsInSalvoFireButton
                    )
                    BurstFireSlider(
                        text = stringResource(R.string.joystick_and_others),
                        burstCount = uiState.shellsInSalvoRoot,
                        onBurstCountChange = viewModel::setShellsInSalvoRoot
                    )
                    KrenSlider(
                        text = stringResource(R.string.voice_kren_title),
                        burstCount = uiState.krenVoice,
                        onBurstCountChange = viewModel::setKrenVoice
                    )
                }


                val coroutineScope = rememberCoroutineScope()

                /*     BackgroundImagePicker(
                         currentUri = uiState.backgroundUri,
                         onImageSelected = { uri ->
                             coroutineScope.launch {
                                 viewModel.setBackgroundUri(uri.toString())
                             }
                         }
                     )*/





                BackgroundChooser(
                    selectedUri = uiState.backgroundUri?.toUri(),
                    selectedColor = uiState.backgroundColor?.let { Color(it.toLong()) },
                    onBackgroundSelected = { viewModel.setBackgroundUri(it.toString()) },
                    onColorSelected = { it?.let { color -> viewModel.setBackgroundColor(color) } }
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        stringResource(R.string.show_direction_sight),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Checkbox(
                        uiState.isPricelActive,
                        { viewModel.setPricelActive(it) })
                }
                PricelChooser(
                    selectedScreenBgUri = uiState.backgroundUri?.toUri(),
                    selectedColor = uiState.backgroundColor?.let { Color(it.toLong()) },
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        stringResource(R.string.enemy_shot_sound),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )
                    Checkbox(
                        uiState.isEnemyShotSoundActive,
                        { viewModel.setEnemyShotSoundActive(it) })
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        stringResource(R.string.show_radar_beam),
                        style = MaterialTheme.typography.bodyLarge, color = Color.Black
                    )
                    Checkbox(
                        uiState.isRadarLuchVisible,
                        { viewModel.setLuchVisible(it) })
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        stringResource(R.string.reset_button_layout),
                        style = MaterialTheme.typography.bodyLarge, color = Color.Black
                    )
                    Checkbox(
                        uiState.isCenterDrop,
                        { viewModel.setIsCenterDrop(it) })
                }

                //  VoiceCommandSettingsBlock(viewModel = viewModel)
                var dialogVisible by remember { mutableStateOf(false) }
                var selectedCommand by remember { mutableStateOf<VoiceCommandType?>(null) }
                var inputText by remember { mutableStateOf("") }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.setting_up_voice_commands), fontSize = 20.sp)

                    VoiceCommandType.values().forEach { type ->
                        val phrase = uiState.voiceCommands[type]
                            ?: defaultVoiceMap.getValue(type).defaultPhrase

                        VoiceCommandRow(
                            type = type,
                            phrase = phrase,
                            onEditClick = {
                                selectedCommand = type
                                inputText = phrase
                                dialogVisible = true
                            }
                        )
                    }
                }

                selectedCommand?.let { command ->
                    NeonEditCommandDialog(
                        visible = dialogVisible,
                        command = command,
                        initial = inputText,
                        onDismiss = { dialogVisible = false },
                        onConfirm = {
                            viewModel.updateVoiceCommand(command, it)
                            dialogVisible = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RadarScaleSelector(
    selectedScale: Float,
    onScaleChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(1f to stringResource(R.string.km_8), 0.1f to stringResource(R.string.km_80)).forEach { (scale, label) ->
            val isSelected = selectedScale == scale
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) Color.Blue.copy(alpha = 0.4f) else Color.DarkGray.copy(
                            alpha = 0.3f
                        )
                    )
                    .clickable { onScaleChange(scale) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = label,
                    color = if (isSelected) Color.Black else Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}
