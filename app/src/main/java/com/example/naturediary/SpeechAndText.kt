package com.example.naturediary

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.EditText
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class SpeechAndText {
    companion object {
        const val REQUEST_CODE_STT = 1
        lateinit var speechEngine: TextToSpeech

        fun init(context: Context) {
            speechEngine = TextToSpeech(
                context
            ) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    speechEngine.language = Locale.ENGLISH
                }
            }
        }
    }

    fun speechToText(activity: Activity) {
        val sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        sttIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        sttIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now!")

        activity.startActivityForResult(sttIntent, REQUEST_CODE_STT)
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
        when(string) {
            "go to main" -> pager2.currentItem = 0
            "go to speech" -> pager2.currentItem = 1
            "go to record" -> pager2.currentItem = 2
            "go to list" -> pager2.currentItem = 4
            else -> speechEngine.speak("I can not understand that command.", TextToSpeech.QUEUE_FLUSH, null, "sc")
        }
    }
}