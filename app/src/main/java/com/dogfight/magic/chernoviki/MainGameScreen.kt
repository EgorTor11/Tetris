package com.dogfight.magic.chernoviki

/*import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.taranovegor91.tetris.R
import com.taranovegor91.tetris.chernoviki.RadarMainScreen
import com.taranovegor91.tetris.game_ui.radar.upravlenie.avia_gorizont.AttitudeIndicator
import com.taranovegor91.tetris.game_ui.radar.upravlenie.buttons.NeonButtonsRow
import com.taranovegor91.tetris.game_ui.radar.upravlenie.buttons.NeonFireButtonWithoutOnClick
import com.taranovegor91.tetris.game_ui.radar.upravlenie.neon_battle_top_panel.GameTopBar
import com.taranovegor91.tetris.game_ui.radar.upravlenie.neon_battle_top_panel.PauseTimerPanel
import com.taranovegor91.tetris.game_ui.radar.upravlenie.rud_design.NeonRUDControl
import com.taranovegor91.tetris.game_ui.radar.upravlenie.rus_disign.JoystickControl
import com.taranovegor91.tetris.game_ui.radar.upravlenie.super_container.ContentData
import com.taranovegor91.tetris.game_ui.radar.upravlenie.super_container.DragContainer
import com.taranovegor91.tetris.game_ui.radar.upravlenie.super_container.SharedContainerWithDrag
import com.taranovegor91.tetris.game_ui.radar.upravlenie.view_model.ControlViewModel
import com.taranovegor91.tetris.game_ui.theme.AirCombatTheme
import com.taranovegor91.tetris.game_ui.widgets.MyComposableWithOnResume
import com.taranovegor91.tetris.game_ui.widgets.toast.NeonToast
import com.taranovegor91.tetris.game_ui.widgets.voice.SuperRequestMicPermission
import com.taranovegor91.tetris.home_screen.GameMode
import kotlinx.coroutines.delay*/

/*
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainGameScreen(viewModel: ControlViewModel, mode: GameMode = GameMode.TRAINING) {
    val rollAngle by viewModel.rollAngle.collectAsState()
    val course by viewModel.course.collectAsState()
    val selectedFireButton by viewModel.selectedFireButton.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()
    val timerSeconds by viewModel.timerSeconds.collectAsState()
    var showToast by rememberSaveable { mutableStateOf<Boolean?>(false) }

    val playerScore by viewModel.playerScore.collectAsState()
    val enemyScore by viewModel.enemyScore.collectAsState()

    SuperRequestMicPermission { granted ->
        if (granted) {
            // 🎙 Разрешение получено — запускай распознавание
        } else {
            // 🚫 Пользователь отклонил
        }
    }

    AirCombatTheme {
        val context = LocalContext.current
        val activity = context as ComponentActivity
        val windowInsetsController =
            remember { WindowInsetsControllerCompat(activity.window, activity.window.decorView) }
        var stateOnRsume by remember { mutableStateOf(false) }
        MyComposableWithOnResume {
            // Код, который нужно выполнить при onResume
            stateOnRsume = true
            println("🟢 onResume!")
        }

        LaunchedEffect(stateOnRsume == true) {
            stateOnRsume = false
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) delay(300)

            // Убираем статус-бар и навигацию полностью
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

            // Автоматическое скрытие панелей при появлении
            ViewCompat.setOnApplyWindowInsetsListener(activity.window.decorView) { _, insets ->
                if (insets.isVisible(WindowInsetsCompat.Type.systemBars())) {
                    windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
                }
                insets
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    Log.d("clickable", "Box clickable")
                    viewModel.onRootClick()
                }
        ) {
            Image(// фоновая картинка приложения
                painter = painterResource(R.drawable.img_background),
                "app_background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                //  .padding(innerPadding)
            ) { // Здесь размещаем остальные элементы игры

                RadarMainScreen(viewModel, mode = mode)

                SharedContainerWithDrag(
                    prefsKey = "AttitudeIndicator",
                    onClick = { viewModel.onRootClick() },
                    viewModel = viewModel,
                    content = {
                        AttitudeIndicator(rollAngle, course, modifier = it)
                    },
                )

                SharedContainerWithDrag(
                    prefsKey = "RealisticRUDControl2",
                    onClick = { viewModel.onRootClick() },
                    viewModel = viewModel,
                    content = {
                        NeonRUDControl(
                            onThrottleChange = { value ->
                                viewModel.updateThrottleFromSlider(value) // Передаем в ViewModel
                            },
                            scaleImage = null, //painterResource(R.drawable.slider_schkala),
                            knobImage = null, // или painterResource(R.drawable.polzunok)
                            glowColor = Color.Cyan,
                            modifier = it,
                            viewModel = viewModel
                        )
                    },
                )

                SharedContainerWithDrag(
                    onClick = {
                        viewModel.onRootClick()
                        if (showToast != null)
                            showToast = true
                    },
                    isElementDoubleTapNeed = false,
                    viewModel = viewModel,
                    content = { modifier ->
                        selectedFireButton?.let {
                            NeonFireButtonWithoutOnClick(
                                text = it.text,
                                shape = it.shape,
                                glowColor = it.neonColor,
                                backgroundColor = it.backgroundColor,
                                imageUri = it.imageUri,
                                modifier = modifier
                            )
                        }
                    },
                    detailContentData = ContentData(
                        2,
                        "Кнопка FIRE",
                        "Это элемент для ведения стрельбы. Особенно полезен при стрельбе в развароте!",
                        R.drawable.ic_fier_btn
                    ),
                    detailContent = { modifier, onDismiss ->
                        NeonButtonsRow(viewModel, onElementClick = {
                            viewModel.onFireButtonSelected(it)
                            onDismiss.invoke()
                        }, content = {
                            selectedFireButton?.let {
                                NeonFireButtonWithoutOnClick(
                                    text = it.text,
                                    shape = it.shape,
                                    glowColor = it.neonColor,
                                    backgroundColor = it.backgroundColor,
                                    imageUri = it.imageUri,
                                )
                            }
                        })
                    }
                )
                SharedContainerWithDrag(
                    prefsKey = "JoystickControl",
                    viewModel = viewModel,
                    onClick = { viewModel.onRootClick() },
                    content = { JoystickControl(viewModel, imageModifier = it) })


                NeonToast(
                    message = "💥 Тап двумя пальцами для инфо!",
                    iconResMain = R.drawable.ic_fier_btn, // твоя иконка
                    iconResInfo = R.drawable.to_fing, // твоя иконка
                    visible = showToast == true,
                    onDismiss = { showToast = null }
                )
                DragContainer(
                    onClick = {},
                    onDoubleTap = {},
                    prefsKey = "CompactBattleScorePanel"
                ) {

                    //  ShrinkableScorePanelWithAutoCollapse(modifier = it, course = course)
*/
/*                    PauseTimerPanel(
                        isPaused = isPaused,
                        timeLeft = timerSeconds,
                        onPauseToggle = { viewModel.togglePause() }
                    )*//*

                    GameTopBar(
                        playerScore = playerScore,
                        enemyScore = enemyScore,
                        isPaused = isPaused,
                        timeLeft = timerSeconds,
                        onPauseToggle = { viewModel.togglePause() },
                        playerName = "Игрок",
                        enemyName = "Босс",
                        playerAvatar = painterResource(id = R.drawable.img_top_gan),
                        enemyAvatar = painterResource(id = R.drawable.img_pilot_vladik)
                    )
                }

            }
        }

    }
}*/
