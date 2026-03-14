package com.dogfight.magic.game_ui.widgets.voice

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale
class VoiceCommandListener(
    private val context: Context,
    private val onCommandRecognized: (String) -> Unit
) {

    private var speechRecognizer: SpeechRecognizer? = null
    private var savedSystemVolume: Int? = null

    /* ───────── PUBLIC ───────── */

    fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Log.e(TAG, "Speech recognizer unavailable")
            return
        }
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(recListener)
            }
        }
        muteSystemSounds()
        restartListening()
    }

    fun stopListening() {
        restoreSystemSounds()
        speechRecognizer?.apply {
            stopListening(); cancel(); destroy()
        }
        speechRecognizer = null
    }

    /* ───────── RECOGNITION LISTENER ───────── */

    private val recListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) { Log.d(TAG, "READY") }
        override fun onResults(results: Bundle?) {
            results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.firstOrNull()
                ?.let { onCommandRecognized(it) }
            restartListening()
        }
        override fun onError(error: Int) {
            if (error == SpeechRecognizer.ERROR_NO_MATCH ||
                error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) restartListening()
        }
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    /* ───────── HELPERS ───────── */

    private fun restartListening() {
        speechRecognizer?.startListening(createIntent())
    }

    private fun createIntent() = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
        //putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
        // Попытка указать Google‑пакет (безопасно: проигнорируется, если нет)
        `package` = "com.google.android.googlequicksearchbox"
    }

    /* ───────── SOUND CONTROL ───────── */

    private fun muteSystemSounds() {
        val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (savedSystemVolume == null)
            savedSystemVolume = audio.getStreamVolume(AudioManager.STREAM_SYSTEM)
        audio.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, 0)
    }

    private fun restoreSystemSounds() {
        val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        savedSystemVolume?.let { audio.setStreamVolume(AudioManager.STREAM_SYSTEM, it, 0) }
        savedSystemVolume = null
    }

    companion object { private const val TAG = "VoiceListener" }
}
