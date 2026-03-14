package com.dogfight.magic.home_screen.top_bar

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun SetSystemBars() {
    SetStatusBarColor(color = Color.White)
    SetNavigationBarColor(color = Color.White)
    val context = LocalContext.current
    val activity = context as ComponentActivity
    showBars(activity = activity)
    /*val windowInsetsController =
        remember { WindowInsetsControllerCompat(activity.window, activity.window.decorView) }
    // Показываем статус-бар и нав-бар снова
    windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
    // Снимаем listener, чтобы панели больше не прятались автоматически
    ViewCompat.setOnApplyWindowInsetsListener(activity.window.decorView, null)*/
}

fun showBars(activity: ComponentActivity) {
    val window = activity.window
    val decorView = window.decorView
    val controller = WindowInsetsControllerCompat(window, decorView)

    // Восстанавливаем нормальное поведение системных баров
    WindowCompat.setDecorFitsSystemWindows(window, true)

    controller.show(WindowInsetsCompat.Type.systemBars())

    // Убираем listener, чтобы не скрывались снова
    ViewCompat.setOnApplyWindowInsetsListener(decorView, null)

    // Возвращаем фон (если нужен белый)
    decorView.setBackgroundColor(android.graphics.Color.WHITE)
}