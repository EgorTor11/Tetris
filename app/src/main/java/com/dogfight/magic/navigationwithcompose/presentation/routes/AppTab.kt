package com.dogfight.magic.navigationwithcompose.presentation.routes

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.collections.immutable.persistentListOf
import com.dogfight.magic.R

data class AppTab(
    val icon: ImageVector,
    @StringRes val label: Int,
    val graph: Any,
)

val MainTabs= persistentListOf(
    AppTab(
        icon = Icons.Default.Home,
        label = R.string.home,
        graph = HomeGraph
    ),
/*    AppTab(
        icon = Icons.Default.Person,
        label = R.string.profile,
        graph = ProfileGraph
    ),*/
    AppTab(
        icon = Icons.Default.Settings,
        label = R.string.settings,
        graph = SettingsGraph
    )

)