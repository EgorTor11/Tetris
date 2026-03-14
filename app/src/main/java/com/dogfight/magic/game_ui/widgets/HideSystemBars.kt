package com.dogfight.magic.game_ui.widgets

import android.graphics.Color
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.dogfight.magic.home_screen.top_bar.SetNavigationBarColor
import com.dogfight.magic.home_screen.top_bar.SetStatusBarColor
import kotlinx.coroutines.delay

@Composable
fun HideSystemBars() {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val windowInsetsController = remember { WindowInsetsControllerCompat(activity.window, activity.window.decorView) }
    var stateOnResume by remember { mutableStateOf(false) }

    SetStatusBarColor(color = androidx.compose.ui.graphics.Color.Transparent)
    SetNavigationBarColor(color = androidx.compose.ui.graphics.Color.Transparent)
    LaunchedEffect(Unit) {
        hideBars(activity)
    }
    MyComposableWithOnResume {
        stateOnResume = true
    }
    LaunchedEffect(stateOnResume) {
        stateOnResume = false
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) delay(300)
        hideBars(activity)
    }
}

private fun hideBars(
    activity: ComponentActivity,
) {
    val window = activity.window
    val decorView = window.decorView
    val controller = WindowInsetsControllerCompat(window, decorView)

    // ВАЖНО: отключаем автоматическую вставку системных баров
    WindowCompat.setDecorFitsSystemWindows(window, false)

    controller.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    controller.hide(WindowInsetsCompat.Type.systemBars())

    // Обновляем фон, чтобы не было белого мигания
    decorView.setBackgroundColor(Color.BLACK)

    // Необязательно, но можно повторно скрывать при появлении
    ViewCompat.setOnApplyWindowInsetsListener(decorView) { _, insets ->
        if (insets.isVisible(WindowInsetsCompat.Type.systemBars())) {
            controller.hide(WindowInsetsCompat.Type.systemBars())
        }
        insets
    }
}


/*
private fun hideBars(
    windowInsetsController: WindowInsetsControllerCompat,
    activity: ComponentActivity,
) {
    windowInsetsController.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

    ViewCompat.setOnApplyWindowInsetsListener(activity.window.decorView) { _, insets ->
        if (insets.isVisible(WindowInsetsCompat.Type.systemBars())) {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        }
        insets
    }
    // Устанавливаем фон для decorView, чтобы не было белого блика
    activity.window.decorView.setBackgroundColor(Color.BLACK)
}*/
