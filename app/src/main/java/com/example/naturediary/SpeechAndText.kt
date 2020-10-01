package com.example.naturediary

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.EditText
import java.util.*

class SpeechAndText {
    companion object {
        const val REQUEST_CODE_STT = 1
        lateinit var speechEngine: TextToSpeech
    }

    fun initTextToSpeechEngine(context: Context) {
        speechEngine = TextToSpeech(
            context
        ) { status ->
            if (status == TextToSpeech.SUCCESS) {
                speechEngine.language = Locale.ENGLISH
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

    fun stringToSpeech() {

    }
}