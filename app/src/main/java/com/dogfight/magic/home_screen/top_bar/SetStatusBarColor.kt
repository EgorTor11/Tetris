package com.dogfight.magic.home_screen.top_bar

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun SetStatusBarColor(color: Color) {
    val view = LocalView.current
    val activity = view.context as Activity
    val window = activity.window

    SideEffect {
        val statusBarColor = /*Color(0xFFF4ECE2)*/ color// нежно-песочный
        window.statusBarColor = statusBarColor.toArgb()

        // Установим темные иконки
        WindowInsetsControllerCompat(window, view).isAppearanceLightStatusBars = true
    }
}
