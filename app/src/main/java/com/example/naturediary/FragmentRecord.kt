package com.example.naturediary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_record.view.*

class FragmentRecord : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_record, container, false)
        view.btnRecord.setOnClickListener { AudioPlayer().record() }
        view.btnPlay.setOnClickListener { AudioPlayer().play() }
        view.btnStop.setOnClickListener { AudioPlayer().stop() }
        return view
    }
}
