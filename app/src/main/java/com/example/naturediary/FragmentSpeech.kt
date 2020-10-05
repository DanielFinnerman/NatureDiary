package com.example.naturediary

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.fragment_speech.view.*

class FragmentSpeech : Fragment() {

    companion object {
        lateinit var mainContext: Context
        lateinit var mainPager: ViewPager2

        fun init(context: Context, viewPager2: ViewPager2) {
            mainContext = context
            mainPager = viewPager2
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_speech, container, false)
        view.btnSpeak.setOnClickListener { SpeechAndText().speechToText() }
        view.btnCommand.setOnClickListener { SpeechAndText().speechCommands(mainPager, view.etText.text.toString()) }
        view.btnListen.setOnClickListener { SpeechAndText().textToSpeech(view.etText) }
        return view
    }
}
