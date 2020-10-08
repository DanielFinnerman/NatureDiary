package com.example.naturediary

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.fragment_record.*
import kotlinx.android.synthetic.main.fragment_record.view.*

class FragmentRecord : Fragment() {

    companion object {
        lateinit var mainPager: ViewPager2

        fun init(pager2: ViewPager2) {
            mainPager = pager2
        }
    }

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
            view.btnStop.isClickable = true
        }
        view.btnPlay.setOnClickListener { Recorder().play() }
        view.btnStop.setOnClickListener { Recorder().stop() }
        view.btnSave.setOnClickListener {
            Location().getLastLocation()
            Firebase().uploadRecording(MainActivity.deviceId, Recorder.currentFile)
            Firebase().upload(
                MainActivity.deviceId,
                "${view.etTitle.text}",
                Location.locationString,
                Recorder.currentFile.name
            )
            Firebase().updateList()
            mainPager.currentItem = 2
        }
        return view
    }
}