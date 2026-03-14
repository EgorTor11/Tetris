package com.dogfight.magic.game_ui.radar.mvi_radar.mode_mission

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import com.dogfight.magic.game_ui.radar.mvi_radar.getDistance
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

fun generateRandomRoute(
    pointCount: Int = 15,
    radius: Float = 300f,
    minSegmentLength: Float = 40f,
    maxAngleDelta: Float = 65f,
    lastSegmentLength: Float = 100f,
): List<Offset> {
    val route = mutableListOf<Offset>()
    val random = Random

    var current = generateRandomPointInsideCircle(radius)
    route.add(current)

    var currentAngle = random.nextFloat() * 360f

    while (route.size < pointCount - 1) {
        var attempts = 0
        var pointAdded = false

        while (attempts < 100 && !pointAdded) {
            attempts++
            val angleOffset = random.nextFloat() * 2 * maxAngleDelta - maxAngleDelta
            val newAngle = currentAngle + angleOffset

            val rad = Math.toRadians(newAngle.toDouble())
            val dx = (minSegmentLength + random.nextFloat() * 40f) * cos(rad).toFloat()
            val dy = (minSegmentLength + random.nextFloat() * 40f) * sin(rad).toFloat()
            val next = current + Offset(dx, dy)

            if (next.getDistance(Offset.Zero) <= radius - 20f) {
                route.add(next)
                current = next
                currentAngle = newAngle
                pointAdded = true
            }
        }

        if (!pointAdded) break
    }

    val directionToCenter = (Offset.Zero - current).normalize()
    val fixedSegmentLength = 200f

    val lastPoint = current + directionToCenter * fixedSegmentLength
    if (lastPoint.getDistance(Offset.Zero) <= radius - 20f) {
        route.add(lastPoint)
    }

    return route
}

fun getHelpPoint(
    route: List<Offset>,
    extraDistance: Float = 10f,
): Offset? {
    if (route.size < 2) return null

    val beforeLast = route[route.size - 2]
    val last = route.last()
    val direction = (last - beforeLast).normalize()

    return last + direction * extraDistance
}


fun generateRandomPointInsideCircle(radius: Float): Offset {
    val angle = kotlin.random.Random.nextDouble(0.0, 2 * PI)
    val r = sqrt(kotlin.random.Random.nextDouble(0.0, 1.0)) * radius
    return Offset(
        (r * cos(angle)).toFloat(),
        (r * sin(angle)).toFloat()
    )
}

fun DrawScope.drawRouteWithDeviationZone(
    route: List<Offset>,
    center: Offset,
    scale: Float = 1f,
    deviationRadius: Float = 20f,
    started: Boolean,
    isInWrongZone: Boolean,
    isInStart: Boolean,
    isInFinish: Boolean,
    fireProgress: Float = 0f, // от 0f до 1f
) {
    if (route.size < 2) return

    // 🔹 Отрисовка основного маршрута
    val path = Path().apply {
        val start = center + route.first() * scale
        moveTo(start.x, start.y)
        for (i in 1 until route.size) {
            lineTo((center + route[i] * scale).x, (center + route[i] * scale).y)
        }
    }

    val deviationColor = if (isInWrongZone) Color.Red.copy(alpha = 0.3f)
    else Color.Cyan.copy(alpha = 0.2f)

    drawPath(path, deviationColor, style = Stroke(width = deviationRadius * 2))
    drawPath(path, if (isInWrongZone) Color.Red else Color.Cyan, style = Stroke(width = 3f))

    // 🔹 Отрисовка всех точек
    route.forEach {
        val p = center + it * scale
        drawCircle(Color.Magenta, 5f, p)
    }

    // 🔹 Старт и финиш
    val startPoint = center + route.first() * scale
    val finishPoint = center + route.last() * scale

    drawCircle(
        color = if (started && !isInStart) Color.Red.copy(alpha = 0.6f) else Color.Green,
        center = startPoint,
        radius = 15f
    )
    drawCircle(
        color = if (started && isInFinish) Color.Yellow else Color.Blue,
        center = finishPoint,
        radius = 15f
    )

    // 🔥 🔥 🔥 Отрисовка очага пожара
    if (!isInWrongZone && route.size >= 2) {
        val fireStart = center + route[route.size - 2] * scale
        val fireEnd = center + route[route.size - 1] * scale
        val direction = (fireEnd - fireStart).normalize()
        val fireLength = (fireEnd - fireStart).getDistance() * fireProgress
        val fireEndAnimated = fireStart + direction * fireLength

        val fireGradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFFFF5722), // ярко-оранжевый
                Color.Red,
                Color(0xFFFFA000)  // светло-оранжевый
            ),
            start = fireStart,
            end = fireEndAnimated
        )

        drawLine(
            brush = fireGradient,
            start = fireStart,
            end = fireEndAnimated,
            strokeWidth = deviationRadius * 2
        )

    }


    if (isInWrongZone) {
        val timeMillis = System.currentTimeMillis() % 1000
        val alpha = if (timeMillis < 500) 1f else 0f // мигает раз в полсекунды

        drawIntoCanvas { canvas ->
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.GREEN
                textSize = 28f * scale
                textAlign = android.graphics.Paint.Align.LEFT
                isAntiAlias = true
                this.alpha = (alpha * 255).toInt()
            }

            canvas.nativeCanvas.drawText(
                "START", // можно заменить на "START" если хочешь
                startPoint.x + 20f, // смещаем чуть вправо
                startPoint.y - 20f, // и чуть вверх
                paint
            )
        }
    }

}

fun isWithinDeviationZone(
    route: List<Offset>,
    fighterPos: Offset,
    deviationRadius: Float,
): Boolean {
    for (i in 0 until route.size - 1) {
        val a = route[i]
        val b = route[i + 1]
        val distance = distanceToSegment(fighterPos, a, b)
        if (distance <= deviationRadius) return true
    }
    return false
}

fun isInStartCircle(
    fighterPos: Offset,
    startPoint: Offset,
    radius: Float,
): Boolean {
    return (fighterPos - startPoint).getDistance() <= radius
}

fun distanceToSegment(p: Offset, a: Offset, b: Offset): Float {
    val ab = b - a
    val ap = p - a
    val t = (ap dot ab) / (ab dot ab)
    val clampedT = t.coerceIn(0f, 1f)
    val closest = a + ab * clampedT
    return (p - closest).getDistance()
}

infix fun Offset.dot(other: Offset): Float {
    return this.x * other.x + this.y * other.y
}


fun Offset.normalize(): Offset {
    val len = getDistance()
    return if (len != 0f) this / len else Offset.Zero
}

data class FirePoint(
    val position: Offset,
    var isActive: Boolean = false,
    var isStartProgress: Boolean = false,
    var progress: Float = 0f, // от 0f до 1f
    var timeSinceActivated: Long = 0L, // миллисекунды с момента активации
)

fun generateFirePoints(lastSegmentStart: Offset, lastSegmentEnd: Offset): List<FirePoint> {
    val points = mutableListOf<FirePoint>()
    for (i in 1..10) {
        val t = i / 10f
        val pos = Offset(
            x = lastSegmentStart.x + t * (lastSegmentEnd.x - lastSegmentStart.x),
            y = lastSegmentStart.y + t * (lastSegmentEnd.y - lastSegmentStart.y)
        )
        //сделаем только один огонек в конце маршрута на самомо домике
        points.add(FirePoint(pos))
    }

    points.first().isActive=true
    return points
}
