package com.dogfight.magic.navigationwithcompose.presentation.routes

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.navDeepLink

import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
data object HomeGraph {
    @Serializable
    data object HomeRout

    @Serializable
    data object AddItemRout

    @Serializable
    data class EditItemRout(val id: Int) {
        companion object {
            val Link = navDeepLink { uriPattern = "nav://items/{id}" }
        }
    }

    @Serializable
    data class GameRout(val gameLevel: Int) {
        companion object {
            val Link = navDeepLink { uriPattern = "nav://game/{gameLevel}" }
        }
    }
}

@Serializable
data object SettingsGraph {
    val Link = navDeepLink { uriPattern = "nav://settings" }

    @Serializable
    data object SettingsRout
}

@Serializable
data object ProfileGraph {
    @Serializable
    data object ProfileRout
}

fun NavBackStackEntry?.routeClass(): KClass<*>? {
    return this?.destination.routeClass()
}

fun NavDestination?.routeClass(): KClass<*>? {
    return this?.route
        ?.split("/")
        ?.first()
        ?.let { className ->
            generateSequence(className, ::replaceLastDotByDollar)
                .mapNotNull(::tryParsClass)
                .firstOrNull()
        }
}

private fun tryParsClass(className: String): KClass<*>? {
    return runCatching { Class.forName(className).kotlin }.getOrNull()
}

private fun replaceLastDotByDollar(input: String): String? {
    val index = input.lastIndexOf('.')
    return if (index != 1) {
        String(input.toCharArray().apply { set(index, '$') })
    } else {
        null
    }
}
