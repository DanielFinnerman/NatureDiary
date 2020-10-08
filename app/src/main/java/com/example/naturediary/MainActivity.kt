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
import kotlinx.android.synthetic.main.activity_main.*

const val TAG = "Nature Diary DBG"

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var deviceId: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getPermissions()

        //Init context to Fragments
        FragmentMain.init(this)
        FragmentList.init(this)

        //Init AudioPlayer
        Recorder.init(this)

        //Init SpeechEngine
        SpeechAndText.init(this, this)

        //Init Location
        Location.init(this, getSystemService(Context.LOCATION_SERVICE) as LocationManager, this)
        Location().checkLocation()

        //Init ViewPager
        val pagerAdapter = SliderAdapter(this)
        pager.adapter = pagerAdapter

        //Getting Device ID so we can separate users without the need of login.
        deviceId =
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        Log.d(TAG, deviceId)

        //Anonymous authentication.
        Firebase().authenticate()

        //Test upload and load.
        Firebase().upload(deviceId, "Ville", Location.locationString)
        Log.d("asd", "maini upattu")
        Firebase().findAllById(deviceId)

        fab.setOnClickListener {
            Recorder().stop()
            SpeechAndText().speechToText()
        }

    }

    //phones back-button to go back 1 page
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

    //update location when active
    override fun onResume() {
        Location().startLocationUpdates()
        super.onResume()
    }

    //Avoid STT/TTS memory leaks
    //stop updating location when app not in foreground
    override fun onPause() {
        SpeechAndText.speechEngine.stop()
        Location().stopLocationUpdates()
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
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
        }
    }

}