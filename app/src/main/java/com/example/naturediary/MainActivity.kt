package com.example.naturediary

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognizerIntent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

const val TAG = "Nature Diary DBG"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getPermissions()

        //Init context to Fragments
        FragmentList.init(this)
        FragmentSpeech.init(this, pager)

        //Init AudioPlayer
        AudioPlayer.init(this)

        //Init SpeechEngine
        SpeechAndText.init(this, this)

        //Init ViewPager
        val pagerAdapter = SliderAdapter(this)
        pager.adapter = pagerAdapter

        //Getting Device ID so we can separate users without the need of login.
        val deviceId =
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        Log.d(TAG, deviceId)

        //Anonymous authentication.
        Firebase().authenticate()

        //Test upload and load.
        Firebase().upload(deviceId, "Ville", "Myllikkä")
        Firebase().findAllById(deviceId)

        btnSpeechCommand.setOnClickListener {
            AudioPlayer().stop()
            SpeechAndText().speechToText()
        }

    }

    override fun onBackPressed() {
        if (pager.currentItem == 0) {
            super.onBackPressed()
        } else {
            pager.currentItem = pager.currentItem - 1
        }
    }

    //STT Results to TextView
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SpeechAndText.REQUEST_CODE_STT -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    if (!result.isNullOrEmpty()) {
                        val recognizedText = result[0]
                        if (recognizedText.contains("perse" )) {
                            SpeechAndText().speechCommands(pager, recognizedText.toUpperCase())
                        } else {
                            SpeechAndText.lastString = recognizedText
                        }
                    }
                }
            }
        }
    }

    //Avoid STT/TTS memory leaks
    override fun onPause() {
        SpeechAndText.speechEngine.stop()
        super.onPause()
    }

    //Avoid STT/TTS memory leaks
    override fun onDestroy() {
        SpeechAndText.speechEngine.shutdown()
        super.onDestroy()
    }

    private fun getPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                1
            )
        }
    }

}