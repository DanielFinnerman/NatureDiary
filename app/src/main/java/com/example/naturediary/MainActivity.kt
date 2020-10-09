package com.example.naturediary

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognizerIntent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*
import org.osmdroid.config.Configuration

const val TAG = "Nature Diary DBG"

//MainActivity
class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var deviceId: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getPermissions()

        //Init context to Fragments
        FragmentRecord.init(this, this, pager)
        FragmentList.init(this)

        //Init AudioPlayer
        Recorder.init(this)

        //Init SpeechEngine
        SpeechAndText.init(this, this)

        //Init Location
        Location.init(this, this, getSystemService(Context.LOCATION_SERVICE) as LocationManager)
        Location().getLastLocation()

        //Init map
        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )

        //Init ViewPager
        val pagerAdapter = SliderAdapter(this)
        pager.adapter = pagerAdapter

        //Getting Device ID so we can separate users without the need of login.
        deviceId =
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        Log.d(TAG, deviceId)

        //Init Firebase
        FirebaseClass().authenticate()
        FirebaseClass().updateList()

        //Command button, stops recorder and starts to listen commands
        btnCommand.setOnClickListener {
            Recorder().stop()
            SpeechAndText().speechToText()
        }
    }

    //Back-button to go back 1 page
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
                        SpeechAndText().speechCommands(pager, recognizedText.toUpperCase())
                        SpeechAndText.lastString = recognizedText
                    }
                }
            }
        }
    }


    //Avoid STT/TTS memory leaks
    //stop updating location when app not in foreground
    override fun onPause() {
        SpeechAndText.speechEngine.stop()
        super.onPause()
    }

    //Avoid STT/TTS memory leaks
    override fun onDestroy() {
        SpeechAndText.speechEngine.shutdown()
        super.onDestroy()
    }

    //Get needed permissions
    private fun getPermissions() {
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE

                ),
                1
            )
        }
    }

}