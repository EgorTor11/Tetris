package com.dogfight.magic.game_ui.radar.mvi_radar

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.dogfight.magic.R
import com.dogfight.magic.game_ui.radar.mvi_radar.mode_mission.FireProgressRow
import com.dogfight.magic.game_ui.radar.upravlenie.avia_gorizont.AttitudeIndicator
import com.dogfight.magic.game_ui.radar.upravlenie.avia_gorizont.Pricel
import com.dogfight.magic.game_ui.radar.upravlenie.buttons.MagicActionBar
import com.dogfight.magic.game_ui.radar.upravlenie.buttons.NeonActionButton
import com.dogfight.magic.game_ui.radar.upravlenie.buttons.shield.ShieldButton
import com.dogfight.magic.game_ui.radar.upravlenie.buttons.target_fokus.FocusPanel
import com.dogfight.magic.game_ui.radar.upravlenie.neon_battle_top_panel.NeonTopBattlePanel
import com.dogfight.magic.game_ui.radar.upravlenie.rud_design.NeonRUDControl
import com.dogfight.magic.game_ui.radar.upravlenie.rud_design.forsag.NeonAfterburnerButton
import com.dogfight.magic.game_ui.radar.upravlenie.rus_disign.JoystickControl
import com.dogfight.magic.game_ui.radar.upravlenie.super_container.DragContainer
import com.dogfight.magic.game_ui.theme.AirCombatTheme
import com.dogfight.magic.game_ui.widgets.HideSystemBars
import com.dogfight.magic.game_ui.widgets.MyComposableWithOnPause
import com.dogfight.magic.game_ui.widgets.NeonInfoDialog
import com.dogfight.magic.game_ui.widgets.toast.NeonToast
import com.dogfight.magic.game_ui.widgets.voice.SuperRequestMicPermission
import com.dogfight.magic.navigationwithcompose.presentation.composition_local.LocalNavController
import com.dogfight.magic.settings_screen.widjets.uriToImageBitmap
import com.dogfight.magic.unity_ads.NeonChoiceDialog
import com.dogfight.magic.unity_ads.ResourceDepletionType
import com.dogfight.magic.unity_ads.loadRewardedAd
import com.dogfight.magic.unity_ads.showRewardedAd
import com.dogfight.magic.utils.LockScreenOrientation
import com.dogfight.magic.utils.isLowEndDevice
import dagger.hilt.android.EntryPointAccessors

@Composable
fun TestGameScreen(gameLevel: Int) {
    val activity = LocalActivity.current
    val context = LocalContext.current
    val isLowEndDevice = rememberSaveable { isLowEndDevice(context) }
    if (activity != null) {
        val factoryProvider = EntryPointAccessors.fromActivity(
            activity,
            RadarViewModelFactoryProvider::class.java
        )
        val viewModelFactory = RadarViewModelFactoryImpl(
            assistedFactory = factoryProvider.radarViewModelFactory(),
            gameLevel = gameLevel
        )
        val viewModel: RadarViewModel = viewModel(factory = viewModelFactory)


        // Теперь используешь viewModel дальше
        // val viewModel = hiltViewModel<RadarViewModel>() этот вариант был без асистед инжекта
        val radarUiState by viewModel.screenState.collectAsState()

        val backgroundUri = radarUiState.backgroundUri?.let { Uri.parse(it) }
        var showToast by rememberSaveable { mutableStateOf<Boolean?>(false) }
        var isFreeStarsAllredyGeted by rememberSaveable { mutableStateOf<Boolean>(false) }
        val navController = LocalNavController.current
        val portraitOffsetList =
            convertOffsetListToDpList(portraitOffsetList)
        val landscapeOffsetList =
            convertOffsetListToDpList(landscapeOffsetList)
        SuperRequestMicPermission { granted -> }




        AirCombatTheme {
            MyComposableWithOnPause {
                viewModel.setPause(true)
            }
            LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            HideSystemBars()


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black) // фон, чтобы можно было отличить от неона
            ) {
                // Фон
                /*                if (backgroundUri != null) {
                                    AsyncImage(
                                        model = backgroundUri,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop // масштабировать, чтобы заполнить весь экран
                                    )
                                }*/
                when {
                    radarUiState.backgroundUri != null && radarUiState.backgroundUri != "null" -> {
                        AsyncImage(
                            model = backgroundUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop // масштабировать, чтобы заполнить весь экран
                        )
                        Log.d(
                            "setBackgroundColor",
                            "AsyncImage: radarUiState.backgroundUri= ${radarUiState.backgroundUri} "
                        )
                    }

                    radarUiState.backgroundColor != null -> {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color(radarUiState.backgroundColor!!.toLong()))
                        )
                        Log.d(
                            "setBackgroundColor",
                            "Box: radarUiState.backgroundColor= ${radarUiState.backgroundColor} "
                        )
                    }

                    else -> {
                        Image(
                            painter = painterResource(R.drawable.img_bg_kosmos),
                            contentDescription = "app_background",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Log.d(
                            "setBackgroundColor",
                            "else: radarUiState.backgroundColor= ${radarUiState.backgroundColor} "
                        )
                    }
                }

                // TestRadarScreen(viewModel = viewModel)
                RadarScreen(viewModel, gameLevel, isLowEndDevice)
                if (gameLevel == 11)
                    DragContainer(
                        isCenterDrop = radarUiState.isCenterDrop,
                        onChangeIsCenterDrop = { viewModel.setIsCenterDrop(it) },
                        portraitOffset = convertDpOffsetToPx(portraitOffsetList[6]),
                        landscapeOffset = convertDpOffsetToPx(landscapeOffsetList[6]),
                        prefsKey = "Family_Row",
                        onClick = { /*viewModel.onRootClick()*/ },
                        content = {
                            FireProgressRow(fireProgress = radarUiState.fireProgressSize)
                        }
                    )
                DragContainer(
                    isCenterDrop = radarUiState.isCenterDrop,
                    onChangeIsCenterDrop = { viewModel.setIsCenterDrop(it) },
                    portraitOffset = convertDpOffsetToPx(portraitOffsetList[3]),
                    landscapeOffset = convertDpOffsetToPx(landscapeOffsetList[3]),
                    prefsKey = "RealisticRUDControl2",
                    onClick = { /*viewModel.onRootClick()*/ },
                    content = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            NeonRUDControl(
                                onThrottleChange = { value ->
                                    Log.d("speed", "onThrottleChange value=$value")
                                    viewModel.updateThrottleFromSlider(
                                        value
                                    )
                                },
                                scaleImage = null,
                                knobImage = null,
                                glowColor = Color.Cyan,
                                modifier = it,
                                viewModel = viewModel,
                                onClick = { viewModel.onRootClick() }
                            )
                            NeonAfterburnerButton(
                                viewModel = viewModel,
                                onShowAdRequest = {
                                    viewModel.updateDepletionType(
                                        ResourceDepletionType.Afterburner
                                    )
                                },
                            )
                        }
                    }
                )

                DragContainer(
                    isCenterDrop = radarUiState.isCenterDrop,
                    onChangeIsCenterDrop = { viewModel.setIsCenterDrop(it) },
                    portraitOffset = convertDpOffsetToPx(portraitOffsetList[4]),
                    landscapeOffset = convertDpOffsetToPx(landscapeOffsetList[4]),
                    prefsKey = "fire_button_prefs",
                    onClick = {
                        // viewModel.onRootClick()
                    },
                    content = { modifier ->
                        /*  radarUiState.selectedFireButton?.let {
                              Row(modifier = Modifier.clickable { viewModel.onRootClick() }) {
                                  NeonFireButtonWithoutOnClick(
                                      ammoCount = radarUiState.ammoCount,
                                      text = it.text,
                                      shape = it.shape,
                                      glowColor = it.neonColor,
                                      backgroundColor = it.backgroundColor,
                                      imageUri = it.imageUri,
                                      modifier = modifier
                                  )
                              }
                          }*/
                        NeonActionButton(
                            iconRes = R.drawable.img_puska,
                            countText = radarUiState.ammoCount.toString(),
                            onClick = {
                                viewModel.onFireButtonClick()
                            })
                    },
                )
                if (gameLevel == 11)
                    DragContainer(
                        isCenterDrop = radarUiState.isCenterDrop,
                        onChangeIsCenterDrop = { viewModel.setIsCenterDrop(it) },
                        portraitOffset = convertDpOffsetToPx(portraitOffsetList[9]),
                        landscapeOffset = convertDpOffsetToPx(landscapeOffsetList[9]),
                        prefsKey = "Watter_button_prefs",
                        onClick = {
                            // viewModel.onRootClick()
                        },
                        content = { modifier ->
                            NeonActionButton(
                                enabled = radarUiState.isInLegalRoute,
                                iconRes = R.drawable.img_sbros_vody,
                                countText = "",
                                onClick = {
                                    if (radarUiState.isInLegalRoute)
                                        viewModel.startWaterDrop()
                                })
                        },
                    )


                DragContainer(
                    isCenterDrop = radarUiState.isCenterDrop,
                    onChangeIsCenterDrop = { viewModel.setIsCenterDrop(it) },
                    portraitOffset = convertDpOffsetToPx(portraitOffsetList[8]),
                    landscapeOffset = convertDpOffsetToPx(landscapeOffsetList[8]),
                    prefsKey = "MagicActionBar",
                    onClick = {},
                    content = {
                        MagicActionBar(
                            viewModel = viewModel,
                            onTurn = { viewModel.onBattleTurnClick() },
                            onAvada = { viewModel.onAvadaClick() },
                            onEnemyReverse = { viewModel.onEnemyReverseClick() },

                            onSupperRocketClick = {
                                viewModel.onSupperRocketButtonClick()
                                viewModel.decreaseSuperRocket()
                            },
                            onRocketClick = {
                                if (radarUiState.isInLockZone) {
                                    viewModel.onHomingButtonClick()
                                    viewModel.decreaseHoming()
                                }
                            }
                        )
                    }
                )



                DragContainer(
                    isCenterDrop = radarUiState.isCenterDrop,
                    onChangeIsCenterDrop = { viewModel.setIsCenterDrop(it) },
                    portraitOffset = convertDpOffsetToPx(portraitOffsetList[0]),
                    landscapeOffset = convertDpOffsetToPx(landscapeOffsetList[0]),
                    prefsKey = "AttitudeIndicator",
                    onClick = { viewModel.onRootClick() },
                    content = {
                        AttitudeIndicator(
                            roll = radarUiState.rollAngle,
                            course = radarUiState.course,
                            modifier = it
                        )
                    }
                )

                DragContainer(
                    isCenterDrop = radarUiState.isCenterDrop,
                    onChangeIsCenterDrop = { viewModel.setIsCenterDrop(it) },
                    prefsKey = "JoystickControl",
                    portraitOffset = convertDpOffsetToPx(portraitOffsetList[2]),
                    landscapeOffset = convertDpOffsetToPx(landscapeOffsetList[2]),
                    //   viewModel = viewModel,
                    onClick = {/* viewModel.onRootClick()*/ },
                    content = {
                        JoystickControl(
                            viewModel = viewModel,
                            imageModifier = it,
                            onClick = { viewModel.onRootClick() })
                    }
                )
                DragContainer(
                    isCenterDrop = radarUiState.isCenterDrop,
                    onChangeIsCenterDrop = { viewModel.setIsCenterDrop(it) },
                    prefsKey = "FocusPanel",
                    portraitOffset = convertDpOffsetToPx(portraitOffsetList[7]),
                    landscapeOffset = convertDpOffsetToPx(landscapeOffsetList[7]),
                    //   viewModel = viewModel,
                    onClick = {/* viewModel.onRootClick()*/ },
                    content = {
                        FocusPanel(
                            fighterFocus = radarUiState.fighterFocus,
                            onFighterFocusClick = {
                                if (radarUiState.fighterFocus != true)
                                    viewModel.updateFocusState(true)
                                else viewModel.updateFocusState(null)
                            },
                            onEnemyFocusClick = {
                                if (radarUiState.fighterFocus != false)
                                    viewModel.updateFocusState(false)
                                else viewModel.updateFocusState(null)
                            })
                    }
                )
                DragContainer(
                    isCenterDrop = radarUiState.isCenterDrop,
                    onChangeIsCenterDrop = { viewModel.setIsCenterDrop(it) },
                    portraitOffset = convertDpOffsetToPx(portraitOffsetList[5]),
                    landscapeOffset = convertDpOffsetToPx(landscapeOffsetList[5]),
                    prefsKey = "ShieldButton",
                    onClick = {
                        /*if (!radarUiState.isShieldActive)
                            viewModel.useShield()*/
                    },
                    content = {
                        ShieldButton(
                            count = radarUiState.shieldCount,
                            modifier = it,
                            isActive = radarUiState.isShieldActive,
                            progress = radarUiState.shieldTimeLeft,
                            onClick = {
                                if (!radarUiState.isShieldActive)
                                    viewModel.useShield()
                            })
                    }
                )
                /*      DragContainer(
                          portraitOffset = convertDpOffsetToPx(portraitOffsetList[8]),
                          landscapeOffset = convertDpOffsetToPx(landscapeOffsetList[8]),
                          prefsKey = "MagicActionBar",
                          onClick = {},
                          content = {
                              MagicActionBar(
                                  viewModel = viewModel,
                                  onTurn = { viewModel.onBattleTurnClick() },
                                  onAvada = { viewModel.onAvadaClick() },
                                  onEnemyReverse = { viewModel.onEnemyReverseClick() },

                                  onSupperRocketClick = {
                                      viewModel.onSupperRocketButtonClick()
                                      viewModel.decreaseSuperRocket()
                                  },
                                  onRocketClick = {
                                      if (radarUiState.isInLockZone) {
                                          viewModel.onHomingButtonClick()
                                          viewModel.decreaseHoming()
                                      }
                                  }
                              )
                          }
                      )*/
                val prefs = remember {
                    context.getSharedPreferences("aiming_prefs", Context.MODE_PRIVATE)
                }
                /*                DragContainer(onClick = {}, prefsKey = "ElonHorizon") {
                                    ElonHorizon(heading = radarUiState.course)
                                }
                                DragContainer(onClick = {}, prefsKey = "EnemyAzimuthRing") {
                                    EnemyAzimuthRing(
                                        azimuth = radarUiState.enemyAzimuth, // враг справа
                                          = ImageBitmap.imageResource(
                                            context.resources,
                                            R.drawable.img_rocket_main
                                        ),
                                        ringRadius = 120.dp,
                                        enemySize = 32.dp
                                    )
                                }*/
                var bodyUri by remember { mutableStateOf("".toUri()) }
                var handUri by remember { mutableStateOf("".toUri()) }
                var enemyUri by remember { mutableStateOf("".toUri()) }
                var bodyBitmap by remember { mutableStateOf(uriToImageBitmap(context, bodyUri)) }
                var handBitmap by remember { mutableStateOf(uriToImageBitmap(context, handUri)) }
                var enemyBitmap by remember { mutableStateOf(uriToImageBitmap(context, enemyUri)) }
                LaunchedEffect(Unit) {
                    val dir = context.filesDir
                    dir.listFiles()?.forEach { file ->
                        when {
                            file.name.startsWith("selected_hand_") -> handUri = Uri.fromFile(file)
                            file.name.startsWith("selected_body_") -> bodyUri = Uri.fromFile(file)
                            file.name.startsWith("selected_enemy_") -> enemyUri = Uri.fromFile(file)
                        }
                        bodyBitmap= uriToImageBitmap(context, bodyUri)
                        handBitmap = uriToImageBitmap(context, handUri)
                        enemyBitmap = uriToImageBitmap(context, enemyUri)
                    }
                }

                if (radarUiState.isPricelActive) {
                    DragContainer(onClick = {}, prefsKey = "Pricel",
                        portraitOffset = convertDpOffsetToPx(portraitOffsetList[10]),
                        landscapeOffset = convertDpOffsetToPx(landscapeOffsetList[10]),) {
                        Pricel(
                            heading = radarUiState.course,
                            azimuth = radarUiState.enemyAzimuth, // враг справа,
                            bodyBitmap = bodyBitmap,
                            handBitmap = handBitmap,
                            enemyBitmap = enemyBitmap,

                            )
                    }
                }

                DragContainer(
                    isCenterDrop = radarUiState.isCenterDrop,
                    onChangeIsCenterDrop = { viewModel.setIsCenterDrop(it) },
                    portraitOffset = convertDpOffsetToPx(portraitOffsetList[1]),
                    landscapeOffset = convertDpOffsetToPx(landscapeOffsetList[1]),
                    onClick = {},
                    onDoubleTap = {},
                    prefsKey = "CompactBattleScorePanel"
                ) {
                    if (gameLevel != 0 && gameLevel != 11) NeonTopBattlePanel(
                        modifier = it,
                        playerName = stringResource(R.string.you),
                        enemyName = stringResource(R.string.enemy),
                        playerScore = radarUiState.playerScore,
                        enemyScore = radarUiState.enemyScore,
                        enemyHealth = radarUiState.playerHits,
                        playerHealth = radarUiState.enemyHits, // чета ранения наоборот
                        timerText = formatSeconds(radarUiState.countdownTimer),
                        playerAvatar = R.drawable.img_top_gan,
                        enemyAvatar = R.drawable.img_hard,
                        isPaused = radarUiState.isPaused,
                        onPauseToggle = { viewModel.togglePause() },
                    ) else Icon(
                        imageVector = if (radarUiState.isPaused) Icons.Default.PlayArrow else Icons.Default.PauseCircle,
                        contentDescription = if (radarUiState.isPaused) "Play" else "Pause",
                        tint = if (radarUiState.isPaused) Color.Green else Color.Cyan,
                        modifier = Modifier
                            .size(36.dp)
                            .padding(horizontal = 4.dp)
                            .clickable { viewModel.togglePause() }
                    )

                }
                if (radarUiState.isBlockGameScreen)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(onClick = {})
                            .background(Color.Black.copy(alpha = 0.0f)),
                    )
                if (radarUiState.depletionType != null) {
                    viewModel.setPause(true)
                    NeonChoiceDialog(
                        type = radarUiState.depletionType!!,
                        visible = true,
                        starCount = radarUiState.starCount,
                        onDismiss = {
                            if (radarUiState.depletionType != ResourceDepletionType.Stars) {
                                viewModel.updateDepletionType(null)
                                viewModel.togglePause()
                            }
                        },
                        onChooseStar = {
                            viewModel.decreaseStar()
                            viewModel.rewardForResource(
                                radarUiState.depletionType,
                                radarUiState.depletionType?.rewardForStar ?: 0
                            )
                            viewModel.togglePause()
                            viewModel.updateDepletionType(null)
                        },
                        onChooseAd = {
                            val activity = context as Activity
                            val placementId = "Rewarded_Android"
                            showRewardedAd(
                                activity, placementId,
                                onReward = {
                                    viewModel.rewardForResource(
                                        radarUiState.depletionType,
                                        radarUiState.depletionType?.rewardForAd ?: 0
                                    )
                                    viewModel.togglePause()
                                    viewModel.updateDepletionType(null)
                                    loadRewardedAd(placementId)

                                    if (radarUiState.depletionType == ResourceDepletionType.Stars) {
                                        viewModel.unBlockGame()
                                    }
                                },
                                onFallback = {
                                    if (radarUiState.depletionType != ResourceDepletionType.Stars) {
                                        viewModel.rewardForResource(
                                            radarUiState.depletionType,
                                            radarUiState.depletionType?.rewardForFail ?: 0
                                        )
                                        viewModel.showInfoDialog(
                                            message = if (radarUiState.depletionType?.rewardForFail != 0)
                                                getString(
                                                    context,
                                                    R.string.video_not_loadet_but_resurce
                                                )
                                            else getString(context, R.string.video_not_loadet_toast)
                                        )
                                        viewModel.updateDepletionType(null)
                                    } else {
                                        if (!isFreeStarsAllredyGeted) {
                                            viewModel.rewardForResource(
                                                radarUiState.depletionType,
                                                radarUiState.depletionType?.rewardForFail ?: 0
                                            )
                                        }
                                        showToast = true
                                        viewModel.toogleBlockGameScreen(
                                            gameLevel,
                                            starCount = radarUiState.starCount + 1
                                        )
                                    }
                                }
                            )
                        }
                    )
                }
                radarUiState.infoDialogMessage?.let { message ->
                    NeonInfoDialog(
                        message = message,
                        visible = true,
                        onDismiss = {
                            viewModel.dismissInfoDialog()
                        }
                    )
                }
                if (radarUiState.showResultDialog)
                    GameResultDialog(
                        gameLevel = gameLevel,
                        viewModel = viewModel,
                        currentStarCount = radarUiState.starCount,
                        isVisible = radarUiState.showResultDialog,
                        isWinner = radarUiState.isPlayerWinner,
                        onPlayAgain = {
                            viewModel.closResultDialog()
                            viewModel.restartGame()
                        },
                        onExit = {
                            viewModel.dismissGameResult()
                            navController.popBackStack()
                        }
                    )
                NeonToast(
                    message = if (isFreeStarsAllredyGeted) stringResource(R.string.video_not_loadet_toast) else
                        stringResource(R.string.video_not_loadet_but_resurce),
                    visible = showToast == true,
                    onDismiss = {
                        showToast = null
                        isFreeStarsAllredyGeted = true
                    }
                )
            }
        }
    }
}

fun formatSeconds(seconds: Int): String {
    val minutes = seconds / 60
    val sec = seconds % 60
    return "%02d:%02d".format(minutes, sec)
}

@Composable
fun convertDpOffsetToPx(dpOffset: DpOffset): Offset {
    val density = LocalDensity.current
    return with(density) {
        Offset(dpOffset.x.toPx(), dpOffset.y.toPx())
    }
}

@Composable
fun convertOffsetToDp(offset: Offset): DpOffset {
    val density = LocalDensity.current
    return with(density) {
        DpOffset(offset.x.toDp(), offset.y.toDp())
    }
}

@Composable
fun convertOffsetListToDpList(offsetList: List<Offset>): List<DpOffset> {
    return offsetList.map {
        convertOffsetToDp(it)
    }
}

val portraitOffsetList = listOf<Offset>(
    Offset(252f, -235f),// горизонт 0
    Offset(60f, 60f), // panel 1
    Offset(248f, 1687f),// joystick 2
    Offset(18f, 1616f), // rud 3
    Offset(18f, 1400f), // fier 4
    Offset(234f, 2136f), // shield 5
    Offset(30f, 10f), // Family 6
    Offset(519f, 2163f), // focus panel 7
    Offset(878f, 1363f), // 8 vertical magik buttons bar
    Offset(251f, 1400f), // 9 watter button
    Offset(752f, 30f), // 10 pricel
)

val landscapeOffsetList = listOf<Offset>(
    Offset(1536f, -282f),// горизонт 0
    Offset(63f, 16f),// panel 1
    Offset(1543f, 513f),// joystick 2
    Offset(5f, 355f), // rud 3
    Offset(242f, 868f), // fier 4
    Offset(469f, 870f), // shield 5
    Offset(41f, 8f), // Family 6
    Offset(1795f, 914f), // focus panel 7
    Offset(2141f, 96f), // 8 vertical magik buttons bar
    Offset(240f, 10f), // 9 watter button
    Offset(165f, 333f), // 10 pricel
)