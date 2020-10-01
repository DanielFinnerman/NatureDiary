package com.example.naturediary

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognizerIntent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

const val TAG = "Nature Diary DBG"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Init SpeechEngine
        Speech().initTextToSpeechEngine(this)

        //Getting Device ID so we can separate users without the need of login.
        val deviceId =
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        Log.d(TAG, deviceId)

        //Anonymous authentication.
        FB().authenticate()

        //Test upload and load.
        FB().upload(deviceId, "kalle", "dasd")
        FB().findAllById(deviceId)

    }

    //STT Results to TextView
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Speech.REQUEST_CODE_STT -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    if (!result.isNullOrEmpty()) {
                        val recognizedText = result[0]
                        Log.d(TAG, recognizedText)
                        //textView.text = recognizedText
                    }
                }
            }
        }
    }

    //Avoid STT/TTS memory leaks
    override fun onPause() {
        Speech.speechEngine.stop()
        super.onPause()
    }

    //Avoid STT/TTS memory leaks
    override fun onDestroy() {
        Speech.speechEngine.shutdown()
        super.onDestroy()
    }
}