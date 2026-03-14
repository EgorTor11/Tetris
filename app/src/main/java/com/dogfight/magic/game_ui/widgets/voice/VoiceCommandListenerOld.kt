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
/////хороший рабочий класс но без учета запоминания системной громкости
class VoiceCommandListenerOld(
    private val context: Context,
    private val onCommandRecognized: (String) -> Unit // Лямбда, которая срабатывает после распознавания
) {

    private var speechRecognizer: SpeechRecognizer? = null

    fun startListening() {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onResults(results: Bundle?) {
                        val matches =
                            results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        matches?.firstOrNull()?.let { command ->
                            onCommandRecognized.invoke(command)
                            Log.d("VoiceCommand", "Распознано: $command")
                            /*  if (command.equals("огонь", ignoreCase = true)) {
                                  Toast.makeText(
                                      context,
                                      "Слово 'пуск' распознано!",
                                      Toast.LENGTH_SHORT
                                  ).show()
                                  Log.d("VoiceCommand", "Распознано: $command")
                              }
                              if (command.equals("правым 340", ignoreCase = true)) {
                                  Toast.makeText(
                                      context,
                                      "Слово 'правым 340' распознано!",
                                      Toast.LENGTH_SHORT
                                  ).show()
                                  Log.d("VoiceCommand", "Распознано: $command")
                              }*/
                        }
                        restartListening() // Перезапуск прослушивания
                    }

                    override fun onError(error: Int) {
                        Log.e("VoiceCommand", "Ошибка распознавания: $error")
                        if (error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                            restartListening() // Перезапуск при ошибке
                        }
                    }

                    override fun onReadyForSpeech(params: Bundle?) {
                        Log.d("VoiceCommand", "Готов к распознаванию")
                    }

                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {}
                    override fun onPartialResults(partialResults: Bundle?) {}
                    override fun onEvent(eventType: Int, params: Bundle?) {}

                })
            }
        }
        // Отключаем звуки начала/окончания прослушивания
        muteSystemSounds(context)

        restartListening()
    }

    private fun restartListening() {
        speechRecognizer?.startListening(createSpeechIntent())
    }

    fun stopListening() {
        // Восстанавливаем звуки после завершения прослушивания
        restoreSystemSounds(context)

        speechRecognizer?.stopListening()
        speechRecognizer?.cancel()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }

    private fun createSpeechIntent(): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(
                RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                true
            ) // Для более плавного распознавания
            // putExtra(RecognizerIntent.EXTRA_SILENT, true) // Отключает звуки начала/конца прослушивания
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
            //  putExtra(RecognizerIntent.EXTRA_SILENT, true) // Отключает звуки начала/конца прослушивания
        }
    }

    fun muteSystemSounds(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, 0) // Отключаем системные звуки
    }

    fun restoreSystemSounds(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(
            AudioManager.STREAM_SYSTEM,
            audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM),
            0
        ) // Восстанавливаем звуки
    }

}
