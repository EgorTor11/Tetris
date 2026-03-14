package com.dogfight.magic.game_ui.radar.mvi_radar

import androidx.compose.ui.geometry.Offset


data class MagicBeam(
    val from: Offset,
    val to: Offset,
    val path: List<Offset>
)

fun generateLightningPath(from: Offset, to: Offset, segments: Int = 20, jaggedness: Float = 12f): List<Offset> {
    val dx = (to.x - from.x) / segments
    val dy = (to.y - from.y) / segments

    val random = java.util.Random()
    return (0..segments).map { i ->
        val offsetX = random.nextFloat() * jaggedness * 2 - jaggedness
        val offsetY = random.nextFloat() * jaggedness * 2 - jaggedness
        Offset(from.x + i * dx + offsetX, from.y + i * dy + offsetY)
    }
}


