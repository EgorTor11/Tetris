package com.dogfight.magic.game_ui.radar.mvi_radar

import android.net.Uri
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.dogfight.magic.game_ui.radar.upravlenie.buttons.NeonButtonModel
import com.dogfight.magic.game_ui.radar.upravlenie.buttons.defaultFireButtonModel
import com.dogfight.magic.settings_screen.DisplayStyle
import com.dogfight.magic.settings_screen.widjets.VoiceCommandType
import com.dogfight.magic.unity_ads.ResourceDepletionType

data class RadarScreenState(
    val starCount: Int = 0,
    val starCountAfterStartGame: Int = 0,
    val rollAngle: Float = 0f,
    val course: Float = 0f,
    val isWaterDropping: Boolean = false,
    val waterDropStartTime: Long = 0,
    val waterTrailPoints: List<Offset> = emptyList(),
    val routePoints: List<Offset> = emptyList<Offset>(), //generateRandomRoute(),// маршруты для режима заданий
    val hasStarted: Boolean = false,// стартовал ли маршрут
    val isInLegalRoute: Boolean = false,
    val currentRouteSegmentIndex: Int = 1,
    val isPaused: Boolean = false,
    val countdownTimer: Int = 120,
    val fireProgressSize: Float = 0f,
    val isFireActive: Boolean = false,
    val playerScore: Int = 0,
    val enemyScore: Int = 0,

    val throttleSlider: Float = 0.25f,
    val throttleVoice: Float = 0.25f,

    val avadaCount: Int = 5,
    val turnCount: Int = 3,
    val reverseCount: Int = 3,

    val selectedFireButton: NeonButtonModel? = defaultFireButtonModel,
    val selectedImageUri: Uri? = null,

    val scale: Float = 1.8f,
    val offset: Offset = Offset.Zero,

    val isInLockZone: Boolean = false,
    val isInAvadaRange: Boolean = false,   // близость для «авады»
    val isDragStart: Boolean = false,

    val fighterPosition: Offset = Offset.Zero,
    val fighterAngle: Float = 0f,
    val fighterSpeed: Float = 0.5f,
    val fighterTrail: List<FadingTrailPoint> = emptyList(),

    val enemyPosition: Offset = Offset.Zero,
    val isEnemyShotSoundActive: Boolean = false,
    val isPricelActive: Boolean = false,
    val isRadarLuchVisible: Boolean = false,
    val isCenterDrop: Boolean = false,
    val enemyAngle: Float = 0f,
    val enemyAzimuth: Float = 0f,
    val enemySpeed: Float = 0.9f,
    val enemyTrail: List<FadingTrailPoint> = emptyList(),

    val rockets: List<Rocket> = emptyList(),
    val enemyRockets: List<Rocket> = emptyList(),

    val explosion: Explosion? = null,
    val explosionPosition: Offset = Offset.Zero,

    val isBabahState: Boolean = false,
    val isEnemyBabahState: Boolean = false,

    val playerHits: Int = 0,
    val enemyHits: Int = 0,

    val targetPosition: Offset = Offset.Zero,
    val noisePoints: List<Pair<Offset, Float>> = emptyList(),

    val beamAngle: Float = 0f,
    val progress: Float = 0f,

    val lottieKey: String = "initial",
    val isLeft: Boolean = false,

    val forsagCount: Int = 5,

    val ammoCount: Int = 100,
    val fighterFocus: Boolean? = null,

    val depletionType: ResourceDepletionType? = null,

    val showResultDialog: Boolean = false,
    val isPlayerWinner: Boolean? = false,
    val isShieldActive: Boolean = false,
    val shieldCount: Int = 0,
    val shieldTimeLeft: Float = 0f, // от 0f до 1f — для прогресса

    val homingCount: Int = 0,

    val superRocketCount: Int = 0,
    val infoDialogMessage: String? = null,
    val isBlockGameScreen: Boolean = false,
    val isAfterburnerActive: Boolean = false,
    val afterburnerProgress: Float = 0f,
    val scaleFactorForSpeed: Float = 1f,
    val aircraftSizeFactor: Float = 1f,
    val displayStyle: DisplayStyle = DisplayStyle.CHARACTERS,
    val trailFadeFactor: Float = 0.98f,
    val shellsInSalvoFireButton: Int = 5,
    val shellsInSalvoRoot: Int = 5,
    val backgroundUri: String? = null,
    val backgroundColor: Int? = null,
    val voiceCommands: Map<VoiceCommandType, String> = VoiceCommandType.values()
        .associateWith {   it.defaultPhrase },
    val canvasSize: Size = Size.Zero,
    val magicBeam: MagicBeam? = null,
    val krenVoice: Float= 60f
)

