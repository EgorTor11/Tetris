package com.dogfight.magic.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay
import android.os.Build
import android.app.ActivityManager
import android.content.Context
import android.opengl.GLES10
import java.io.RandomAccessFile
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig

@Composable
fun FpsOverlay(): State<Int> {
    val fps = remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        var frames = 0
        var lastTime = System.currentTimeMillis()

        while (true) {
            delay(16L)
            frames++
            val now = System.currentTimeMillis()
            if (now - lastTime >= 1000) {
                fps.value = frames
                frames = 0
                lastTime = now
            }
        }
    }

    return fps
}



/*
fun Context.isLowEndDevice(): Boolean {
    val totalRamMb = getTotalMemoryInMB()
    val cpuCores = Runtime.getRuntime().availableProcessors()

    return totalRamMb <= 3000 || cpuCores <= 4 || Build.VERSION.SDK_INT <= Build.VERSION_CODES.M
}
*/

// вспомогательная функция для RAM
private fun Context.getTotalMemoryInMB(): Int {
    return try {
        val memInfo = ActivityManager.MemoryInfo()
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(memInfo)
        (memInfo.totalMem / (1024 * 1024)).toInt()
    } catch (e: Exception) {
        4096 // если не удалось получить — считаем, что не слабое
    }
}

fun isDeviceLowEnd(): Boolean {
    val runtime = Runtime.getRuntime()
    val cpuCores = runtime.availableProcessors()
    val ram = getTotalRam()
    val sdkVersion = Build.VERSION.SDK_INT
    val gpu = getGpuRenderer().lowercase()

    val isWeakGpu = listOf("mali-400", "powervr sgx", "adreno 3", "tegra").any { gpu.contains(it) }

    return (cpuCores <= 4 && ram < 3000) || sdkVersion <= 28 || isWeakGpu
}
fun isLowEndDevice(context: Context): Boolean {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memoryClass = activityManager.memoryClass

    val isLowRam = memoryClass <= 3 * 1024 // до 3 ГБ
    val cpuCores = Runtime.getRuntime().availableProcessors()

    val glRenderer = android.opengl.GLES20.glGetString(android.opengl.GLES20.GL_RENDERER) ?: ""
    val lowEndGpus = listOf("Mali-400", "Mali-G31", "Mali-G52", "Adreno 3", "Adreno 2", "PowerVR SGX", "Vivante")

    val isLowGpu = lowEndGpus.any { glRenderer.contains(it, ignoreCase = true) }

    return isLowRam || cpuCores <= 4 || isLowGpu
}



fun getGpuRenderer(): String {
    return try {
        val egl = javax.microedition.khronos.egl.EGLContext.getEGL() as EGL10
        val display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
        val version = IntArray(2)
        egl.eglInitialize(display, version)

        val configs = arrayOfNulls<EGLConfig>(1)
        val numConfigs = IntArray(1)
        egl.eglChooseConfig(display, intArrayOf(EGL10.EGL_NONE), configs, 1, numConfigs)

        val context = egl.eglCreateContext(display, configs[0], EGL10.EGL_NO_CONTEXT, intArrayOf(0x3098, 2, EGL10.EGL_NONE))
        val surface = egl.eglCreatePbufferSurface(display, configs[0], intArrayOf(EGL10.EGL_WIDTH, 1, EGL10.EGL_HEIGHT, 1, EGL10.EGL_NONE))

        egl.eglMakeCurrent(display, surface, surface, context)

        val renderer = GLES10.glGetString(GLES10.GL_RENDERER)
        egl.eglDestroySurface(display, surface)
        egl.eglDestroyContext(display, context)
        egl.eglTerminate(display)

        renderer ?: "unknown"
    } catch (e: Exception) {
        "unknown"
    }
}


// Функция для получения общего объема RAM в мегабайтах
fun getTotalRam(): Long {
    try {
        val reader = RandomAccessFile("/proc/meminfo", "r")
        val load = reader.readLine()
        val memInfo = load.replace(Regex("\\D+"), "")
        reader.close()
        return memInfo.toLong() / 1024 // KB → MB
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return 0
}