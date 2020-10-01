package com.example.naturediary

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognizerIntent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlinx.android.synthetic.main.activity_main.*

const val TAG = "Nature Diary DBG"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Init ViewPager
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        pager.adapter = pagerAdapter

        //Init SpeechEngine
        SpeechAndText().initTextToSpeechEngine(this)

        //Getting Device ID so we can separate users without the need of login.
        val deviceId =
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        Log.d(TAG, deviceId)

        //Anonymous authentication.
        Firebase().authenticate()

        //Test upload and load.
        Firebase().upload(deviceId, "kalle", "dasd")
        Firebase().findAllById(deviceId)

    }

    private class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment =
            when (position) {
                0 -> FragmentMain()
                1 -> FragmentList()
                else -> FragmentList()
            }
    }

    override fun onBackPressed() {
        if (pager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
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
                        Log.d(TAG, recognizedText)
                        //textView.text = recognizedText
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

}