package com.dogfight.magic.game_ui.widgets

import android.content.Context
import android.media.SoundPool
import com.dogfight.magic.R

object SoundManager {
    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<SoundType, Int>()
    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .build()

        soundMap[SoundType.GUN_SHOT] = load(context, R.raw.gun_shot)
        soundMap[SoundType.HIT_BODY] = load(context, R.raw.hit_body)
        soundMap[SoundType.EXPLOSION] = load(context, R.raw.explosion)
        soundMap[SoundType.AVADA_KEDAVRA] = load(context, R.raw.avada_kedavra)
        soundMap[SoundType.HIT_SHIELD] = load(context, R.raw.hit_shield)
        soundMap[SoundType.AVADA_SHIELD] = load(context, R.raw.avada_shield)
        soundMap[SoundType.MISSILE_LAUNCH] = load(context, R.raw.missile_launch)
        soundMap[SoundType.WATER] = load(context, R.raw.water)
        soundMap[SoundType.ENEMY_SHOT] = load(context, R.raw.enemy_shot)

        isInitialized = true
    }

    private fun load(context: Context, resId: Int): Int {
        return soundPool?.load(context, resId, 1) ?: 0
    }

    fun play(type: SoundType, speed: Float = 1.0f) {
        val rate = speed.coerceIn(0.5f, 2.0f) // Безопасный диапазон
        soundMap[type]?.let {
            soundPool?.play(
                it,
                1f, // leftVolume
                1f, // rightVolume
                1,  // priority
                0,  // no loop
                rate // скорость воспроизведения
            )
        }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        isInitialized = false
    }
}


enum class SoundType {
    GUN_SHOT,
    HIT_BODY,
    EXPLOSION,
    AVADA_KEDAVRA,
    HIT_SHIELD,
    AVADA_SHIELD,
    MISSILE_LAUNCH,
    WATER,
    ENEMY_SHOT
}
