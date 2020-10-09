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
        lateinit var mainContext: Context

        var lastString: String = ""

        fun init(activity: Activity, context: Context) {
            mainActivity = activity
            mainContext = context
            speechEngine = TextToSpeech(
                context
            ) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    speechEngine.language = Locale.UK
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
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.UK)
        sttIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "speak")

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
            //"GO TO" commands.
            string.contains(mainContext.getString(R.string.command_go)) -> {
                when {
                    string.contains(mainContext.getString(R.string.page_main)) -> {
                        pager2.currentItem = 0
                        SpeechAndText().stringToSpeech(
                            "${mainContext.getString(R.string.command_assistant_location)} " +
                                    "${mainContext.getString(R.string.page_main)}"
                        )
                    }
                    string.contains(mainContext.getString(R.string.page_record)) -> {
                        pager2.currentItem = 1
                        SpeechAndText().stringToSpeech(
                            "${mainContext.getString(R.string.command_assistant_location)} " +
                                    "${mainContext.getString(R.string.page_record)}"
                        )

                    }
                    string.contains(mainContext.getString(R.string.page_list)) -> {
                        pager2.currentItem = 2
                        SpeechAndText().stringToSpeech(
                            "${mainContext.getString(R.string.command_assistant_location)} " +
                                    "${mainContext.getString(R.string.page_list)}"
                        )
                    }
                    string.contains(mainContext.getString(R.string.page_settings)) -> {
                        pager2.currentItem = 3
                        SpeechAndText().stringToSpeech(
                            "${mainContext.getString(R.string.command_assistant_location)} " +
                                    "${mainContext.getString(R.string.page_settings)}"
                        )
                    }
                }
            }
            //Other commands
            /*
            string.contains(mainContext.getString(R.string.command_record)) -> {
                pager2.currentItem = 1

            }
            string.contains(mainContext.getString(R.string.command_play)) -> {
                pager2.currentItem = 1

            }
            */
            else -> speechEngine.speak(
                mainContext.getString(R.string.command_assistant_error),
                TextToSpeech.QUEUE_FLUSH,
                null,
                "sc"
            )
        }
    }
}