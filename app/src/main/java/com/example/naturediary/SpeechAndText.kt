package com.example.naturediary

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.EditText
import androidx.viewpager2.widget.ViewPager2
import java.util.*

class SpeechAndText {
    companion object {
        const val REQUEST_CODE_STT = 1
        lateinit var speechEngine: TextToSpeech
        lateinit var mainActivity: Activity
        var lastString: String = ""

        fun init(activity: Activity, context: Context) {
            mainActivity = activity
            speechEngine = TextToSpeech(
                context
            ) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    speechEngine.language = Locale.ENGLISH
                }
            }
        }
    }

    fun speechToText() {
        val sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        sttIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)
        sttIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now!")

        mainActivity.startActivityForResult(sttIntent, REQUEST_CODE_STT)
    }

    fun textToSpeech(editText: EditText) {
        val string = editText.text.toString().trim()
        if (string.isNotEmpty()) {
            speechEngine.speak(string, TextToSpeech.QUEUE_FLUSH, null, "tts")
        }
    }

    fun stringToSpeech(string: String) {
        speechEngine.speak(string, TextToSpeech.QUEUE_FLUSH, null, "stt")
    }

    fun speechCommands(pager2: ViewPager2, string: String) {
        when {
            string.contains("MAIN") -> {
                pager2.currentItem = 0
                SpeechAndText().stringToSpeech("You are in main page")
            }
            string.contains("LIST") -> pager2.currentItem = 4
            string.contains("START RECORD") -> {
                pager2.currentItem = 2
                AudioPlayer().record()
            }
            string.contains("PLAY RECORD") -> {
                pager2.currentItem = 2
                AudioPlayer().play()
            }
            else -> speechEngine.speak(
                "I can not understand that command.",
                TextToSpeech.QUEUE_FLUSH,
                null,
                "sc"
            )
        }
    }
}