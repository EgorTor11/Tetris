package com.dogfight.magic.home_screen.home_widgets

import android.content.Context
import com.dogfight.magic.R

data class ModeButtonData(
    val id: Int,
    val name: String,
    val description: String,
    val image: Int? = null,
    val lottieAsset: String? = null,
    val optionLottieAsset: String?=null,
   // val gameMode: GameMode,
)


object ButtonsData {
    val buttons = listOf(
        ModeButtonData(
            id = 1,
            name = ""/*context.getString(R.string.training_flight)*/,
            description = "",
            lottieAsset = "animation_turn.json",
            optionLottieAsset = "animation_vedma.json"
        ),
        ModeButtonData(
            id = 2,
            name = ""/*context.getString(R.string.air_battle)*/,
            description = "",
            lottieAsset = "animation_vb.json",
        ),
        ModeButtonData(
            id = 3,
            name ="" /*context.getString(R.string.online_skirmish)*/,
            description = "",
            lottieAsset = "animation_astronaut_online.json",

        ),
        ModeButtonData(
            id = 11,
            name ="" /*context.getString(R.string.mission_run)*/,
            description = "",
            lottieAsset = "animation_run_missions.json",
        ),
    )
}

// Вместо object — функция
fun getButtonsData(context: Context): List<ModeButtonData> {
    return listOf(
        ModeButtonData(
            id = 1,
            name = context.getString(R.string.training_flight),
            description = "",
            lottieAsset = "animation_turn.json",
            optionLottieAsset = "animation_vedma.json"
        ),
        ModeButtonData(
            id = 2,
            name = context.getString(R.string.air_battle), // Лучше использовать ресурсы и для остальных
            description = "",
            lottieAsset = "animation_vb.json",
        ),
        ModeButtonData(
            id = 3,
            name = context.getString(R.string.online_skirmish),
            description = "",
            lottieAsset = "animation_astronaut_online.json",
        ),
        ModeButtonData(
            id = 11,
            name = context.getString(R.string.mission_run),
            description = "",
            lottieAsset = "animation_run_missions.json",
        ),
    )
}
