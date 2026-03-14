package com.dogfight.magic.navigationwithcompose.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dogfight.magic.navigationwithcompose.presentation.routes.AppTab
import com.dogfight.magic.navigationwithcompose.presentation.routes.routeClass
import kotlinx.collections.immutable.ImmutableList

@Composable
fun AppNavigationBar(
    navController: NavController,
    tabs: ImmutableList<AppTab>,
) {
    NavigationBar(
        modifier = Modifier.shadow(6.dp),
        containerColor = /*Color(0xFFF4ECE2)*/ Color.White
        //  containerColor = androidx.compose.ui.graphics.Color.Transparent /*MaterialTheme.colorScheme.primaryContainer*/,
    ) {
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val closetsNavGraphDestination =
            currentBackStackEntry?.destination?.hierarchy?.first { it is NavGraph }
        val closetsNavGraphClass = closetsNavGraphDestination.routeClass()
        val currentTab = tabs.firstOrNull { it.graph::class == closetsNavGraphClass }
        tabs.forEach { tab ->
            NavigationBarItem(
                selected = currentTab == tab,
                onClick = {
                    if (currentTab != null) {
                        navController.navigate(tab.graph) {
//                            popUpTo(currentTab.graph) {
//                                inclusive = true
//                                saveState = true
//                            }
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Box {
                        Row {
                            Icon(imageVector = tab.icon, "")
                        }
                    }

                },
                label = {
                    Text(stringResource(tab.label))
                },
                colors = NavigationBarItemDefaults.colors(

                    indicatorColor = MaterialTheme.colorScheme.tertiary,
                    selectedIconColor = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            )

        }
    }
}