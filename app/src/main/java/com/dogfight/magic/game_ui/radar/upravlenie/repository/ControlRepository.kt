package com.dogfight.magic.game_ui.radar.upravlenie.repository

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dogfight.magic.settings_screen.DisplayStyle
import com.dogfight.magic.settings_screen.RadarSettingsState
import com.dogfight.magic.settings_screen.widjets.VoiceCommandType
import com.dogfight.magic.settings_screen.widjets.defaultVoiceMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.tan

class ControlRepository(private val context: Context) {

    companion object {
        private val Context.dataStore by preferencesDataStore(name = "game_stats_prefs")
        private val STAR_COUNT = intPreferencesKey("star_count")
        private val AVADA_COUNT = intPreferencesKey("avada_count")
        private val TURN_COUNT = intPreferencesKey("turn_count")
        private val REVERSE_COUNT = intPreferencesKey("reverse_count")
        private val FORSAG_COUNT = intPreferencesKey("forsag_count")
        private val AMMO_COUNT = intPreferencesKey("ammo_count")
        private val SHIELD_COUNT = intPreferencesKey("shield_count")
        private val HOMING_COUNT = intPreferencesKey("homing_count")
        private val SUPER_ROCKET_COUNT = intPreferencesKey("super_rocket_count")
        private val SCALE_FACTOR_FOR_SPEED = floatPreferencesKey("radar_scale")
        private val DISPLAY_STYLE = stringPreferencesKey("display_style")
        private val AIRCRAFT_SIZE_FACTOR = floatPreferencesKey("aircraft_size_factor")
        private val SHELLS_IN_SALVO_FIRE_BUTTON = intPreferencesKey("shells_in_salvo_fire_button")
        private val SHELLS_IN_SALVO_ROOT = intPreferencesKey("shells_in_salvo_root")
        private val KREN_VOICE = intPreferencesKey("kren_voice")
        private val TRAIL_FADE_FACTOR = floatPreferencesKey("trail_fade_factor")
        private val BACKGROUND_URI = stringPreferencesKey("background_uri")
        private val BACKGROUND_COLOR = intPreferencesKey("background_color")
        private val ENEMY_SHOT_SOUND = booleanPreferencesKey("enemy_shot_sound")
        private val PRICEL = booleanPreferencesKey("pricel")
        private val LUCH_VISIBLE = booleanPreferencesKey("luch_visible")
        private val IS_CENTER_DROP = booleanPreferencesKey("is_center_drop")
        private val voiceCommandKeys = VoiceCommandType.values().associateWith {
            stringPreferencesKey("vc_${it.name.lowercase()}")
        }
    }


    val radarSettingsFlow: Flow<RadarSettingsState> = context.dataStore.data
        .map { prefs ->
            RadarSettingsState(
                scaleFactorForSpeed = prefs[SCALE_FACTOR_FOR_SPEED] ?: 1f,
                displayStyle = prefs[DISPLAY_STYLE]?.let { DisplayStyle.valueOf(it) }
                    ?: DisplayStyle.CHARACTERS,
                aircraftSizeFactor = prefs[AIRCRAFT_SIZE_FACTOR] ?: 1f,
                shellsInSalvoFireButton = prefs[SHELLS_IN_SALVO_FIRE_BUTTON] ?: 5,
                shellsInSalvoRoot = prefs[SHELLS_IN_SALVO_ROOT] ?: 5,
                krenVoice = prefs[KREN_VOICE] ?: 60,
                trailFadeFactor = prefs[TRAIL_FADE_FACTOR] ?: 0.98f,
                backgroundUri = prefs[BACKGROUND_URI],
                backgroundColor = prefs[BACKGROUND_COLOR],
                isEnemyShotSoundActive = prefs[ENEMY_SHOT_SOUND] == true,
                isPricelActive = prefs[PRICEL] == true,
                isRadarLuchVisible = prefs[LUCH_VISIBLE] == true,
                isCenterDrop = prefs[IS_CENTER_DROP] == true,
                voiceCommands = VoiceCommandType.values().associateWith { type ->
                    prefs[voiceCommandKeys[type]!!] ?: defaultVoiceMap.getValue(type).defaultPhrase
                }
            )
        }

    suspend fun setBackgroundUri(uri: String) {
        context.dataStore.edit { it[BACKGROUND_URI] = uri }
    }

    suspend fun setBackgroundColor(color: Color?) {
        context.dataStore.edit {
            if (color == null) it.remove(BACKGROUND_COLOR)
            else it[BACKGROUND_COLOR] = color.toArgb()
            Log.d("setBackgroundColor","setBackgroundColor repo  it[BACKGROUND_COLOR]=${ it[BACKGROUND_COLOR]}")
        }
    }
    suspend fun setEnemyShotSoundActive(isActive: Boolean) {
        context.dataStore.edit {
             it[ENEMY_SHOT_SOUND] = isActive
        }
    }
    suspend fun setPricelActive(isActive: Boolean) {
        context.dataStore.edit {
            it[PRICEL] = isActive
        }
    }
    suspend fun setLuchVisible(isVisible: Boolean) {
        context.dataStore.edit {
            it[LUCH_VISIBLE] = isVisible
        }
    }
    suspend fun setIsCenterDrop(isCenterDrop: Boolean) {
        context.dataStore.edit {
            it[IS_CENTER_DROP] = isCenterDrop
        }
    }

    suspend fun setVoiceCommand(type: VoiceCommandType, phrase: String) {
        context.dataStore.edit { prefs ->
            prefs[voiceCommandKeys[type]!!] = phrase
        }
    }

    suspend fun updateTrailFadeFactor(value: Float) {
        context.dataStore.edit { prefs ->
            prefs[TRAIL_FADE_FACTOR] = value
        }
    }

    suspend fun setAircraftSizeFactor(factor: Float) {
        context.dataStore.edit { it[AIRCRAFT_SIZE_FACTOR] = factor }
    }

    suspend fun setFireButtonShellsInSalvo(count: Int) {
        context.dataStore.edit { it[SHELLS_IN_SALVO_FIRE_BUTTON] = count }
    }

    suspend fun setRootShellsInSalvo(count: Int) {
        context.dataStore.edit { it[SHELLS_IN_SALVO_ROOT] = count }
    }
    suspend fun setKrenVoice(kren: Int) {
        context.dataStore.edit { it[KREN_VOICE] = kren }
    }

    suspend fun saveRadarScale(scale: Float) {
        context.dataStore.edit { it[SCALE_FACTOR_FOR_SPEED] = scale }
    }

    suspend fun saveDisplayStyle(style: DisplayStyle) {
        context.dataStore.edit { it[DISPLAY_STYLE] = style.name }
    }

    private val _voiceCommandFlow = MutableSharedFlow<String>()
    val voiceCommandFlow: SharedFlow<String> = _voiceCommandFlow

    suspend fun emitVoiceCommand(command: String) {
        _voiceCommandFlow.emit(command)
    }

    val superRocketCountFlow: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[SUPER_ROCKET_COUNT] ?: StarterPack.SUPERROCKET }

    suspend fun setSuperRocketCount(count: Int) {
        context.dataStore.edit { prefs ->
            prefs[SUPER_ROCKET_COUNT] = count
        }
    }

    val homingCountFlow: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[HOMING_COUNT] ?: StarterPack.HOMING }

    suspend fun setHomingCount(count: Int) {
        context.dataStore.edit { prefs ->
            prefs[HOMING_COUNT] = count
        }
    }

    val shieldCountFlow: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[SHIELD_COUNT] ?: StarterPack.SHIELD }

    suspend fun setShieldCount(count: Int) {
        context.dataStore.edit { prefs ->
            prefs[SHIELD_COUNT] = count
        }
    }

    val avadaCountFlow: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[AVADA_COUNT] ?: StarterPack.AVADA }

    suspend fun setAvadaCount(count: Int) {
        context.dataStore.edit { prefs ->
            prefs[AVADA_COUNT] = count
        }
    }

    val turnCountFlow: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[TURN_COUNT] ?: StarterPack.TURN }

    suspend fun setTurnCount(count: Int) {
        context.dataStore.edit { prefs ->
            prefs[TURN_COUNT] = count
        }
    }

    val reverseCountFlow: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[REVERSE_COUNT] ?: StarterPack.REVERSE }

    suspend fun setReverseCount(count: Int) {
        context.dataStore.edit { prefs ->
            prefs[REVERSE_COUNT] = count
        }
    }

    val starCountFlow: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[STAR_COUNT] ?: StarterPack.STAR }

    suspend fun addStarCount(delta: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[STAR_COUNT] ?: 10
            if (current + delta >= 0)
                prefs[STAR_COUNT] = current + delta
        }
    }

    suspend fun setStarCount(count: Int) {
        context.dataStore.edit { prefs ->
            prefs[STAR_COUNT] = count
        }
    }


    val forsagCountFlow: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[FORSAG_COUNT] ?: StarterPack.FORSAG }

    val ammoCountFlow: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[AMMO_COUNT] ?: StarterPack.AMMO }

    suspend fun setForsagCount(count: Int) {
        context.dataStore.edit { prefs ->
            prefs[FORSAG_COUNT] = count
        }
    }

    suspend fun setAmmoCount(count: Int) {
        context.dataStore.edit { prefs ->
            prefs[AMMO_COUNT] = count
        }
    }

    private val _rollAngle = MutableStateFlow(0f) // Угол крена
    val rollAngle: StateFlow<Float> = _rollAngle.asStateFlow()

    private val _course = MutableStateFlow(0f) // Курс в градусах
    val course: StateFlow<Float> = _course.asStateFlow()

    fun setCourse(value: Float) {
        _course.value = value
    }

    private val speedKnots = 324f // Скорость в узлах (600 км/ч)
    private val g = 9.81f // Ускорение свободного падения

    init {
        // Запускаем обновление курса на основе крена
        CoroutineScope(Dispatchers.Default).launch {
            rollAngle.collectLatest { bankAngle ->
                val turnRate =
                    (g * tan(Math.toRadians(bankAngle.toDouble())) / (600000 / 3600) * (180 / Math.PI)).toFloat()
                var count = 0f
                var deltaTime = 0L
                var time = if (bankAngle != 0f) {
                    System.currentTimeMillis()
                } else {
                    0
                }
                while (bankAngle != 0f) {
                    deltaTime = System.currentTimeMillis() - time
                    time = System.currentTimeMillis()
                    if (deltaTime == 0L) deltaTime =
                        30 // костыль когда идет само двежение джойстиком
                    var deltaCourse = ((turnRate * deltaTime).toDouble() / 1000) * 3
                    Log.d(
                        "myLog",
                        "запуск while, deltaTime=$deltaTime, deltaCourse=$deltaCourse,turnRate= $turnRate"
                    )
                    count = (_course.value + deltaCourse.toFloat()) % 360
                    if (count < 0) count = count + 360f
                    _course.value = count
                    // (_course.value + turnRate) % 360
                    delay(30) // Обновляем курс каждые 30 мс
                }
            }
        }
    }

    fun updateRollAngle(angle: Float) {
        _rollAngle.value = angle * 1.5f
    }
}

object StarterPack {
    const val AMMO = 500
    const val FORSAG = 30
    const val SHIELD = 10
    const val SUPERROCKET = 20
    const val HOMING = 30
    const val STAR = 10
    const val AVADA = 10
    const val TURN = 30
    const val REVERSE = 30
}