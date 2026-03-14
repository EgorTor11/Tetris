package com.dogfight.magic.game_ui.widgets.text_to_speach

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.*
import java.util.Locale

@Composable
fun rememberTextToSpeech(context: Context): (String) -> Unit {
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        val textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("ru", "RU") // 🇷🇺 Русский язык
            }
        }
        tts = textToSpeech

        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    return remember {
        { text: String ->
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }
}
