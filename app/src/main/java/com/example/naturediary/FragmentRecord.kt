package com.example.naturediary

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_record.*
import kotlinx.android.synthetic.main.fragment_record.view.*

class FragmentRecord : Fragment() {


    var timeWhenStopped = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_record, container, false)
        view.btnStop.isClickable = false
        view.btnPlay.isClickable = false
        view.btnRecord.setOnClickListener {
            Recorder().record()
            chronometer.base = SystemClock.elapsedRealtime() + timeWhenStopped
            chronometer.start()
            view.btnRecord.isClickable = false
            view.btnStop.isClickable = true}
        view.btnPlay.setOnClickListener { Recorder().play() }
        view.btnStop.setOnClickListener {
            view.btnRecord.isClickable = true
            view.btnRecord.isClickable = true
            Recorder().stop()
            timeWhenStopped = chronometer.base - SystemClock.elapsedRealtime()
            chronometer.stop()
            chronometer.base = SystemClock.elapsedRealtime()
            timeWhenStopped = 0
            btnStop.isClickable = false}
        return view
    }
}
