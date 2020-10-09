package com.example.naturediary

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.observe
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.fragment_record.view.*

class FragmentRecord : Fragment() {

    companion object {
        lateinit var mainContext: Context
        lateinit var mainLifecycleOwner: LifecycleOwner
        lateinit var mainPager: ViewPager2

        fun init(context: Context, lifecycleOwner: LifecycleOwner, pager2: ViewPager2) {
            mainContext = context
            mainLifecycleOwner = lifecycleOwner
            mainPager = pager2
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_record, container, false)
        view.btnRecord.setOnClickListener {
            view.btnPlayStop.text = getString(R.string.command_stop)
            view.btnPlayStop.setIconResource(R.drawable.ic_baseline_stop_24)
            Recorder().record()
            view.chronometer.base = SystemClock.elapsedRealtime()
            view.chronometer.start()
        }
        view.btnPlayStop.setOnClickListener {
            when (view.btnPlayStop.text) {
                getString(R.string.command_play) -> {
                    view.btnPlayStop.text = getString(R.string.command_stop)
                    view.btnPlayStop.setIconResource(R.drawable.ic_baseline_stop_24)
                    view.chronometer.base = SystemClock.elapsedRealtime()
                    view.chronometer.start()
                    Recorder().play()
                    Recorder.isPlaying.observe(mainLifecycleOwner) {
                        Log.d(TAG, "IT is $it")
                        when (it) {
                            false -> {
                                view.chronometer.stop()
                                view.btnPlayStop.text = getString(R.string.command_play)
                                view.btnPlayStop.setIconResource(R.drawable.ic_baseline_play_arrow_24)
                            }
                            true -> {
                                view.chronometer.start()
                                view.btnPlayStop.text = getString(R.string.command_stop)
                                view.btnPlayStop.setIconResource(R.drawable.ic_baseline_stop_24)
                            }
                        }
                    }
                }
                getString(R.string.command_stop) -> {
                    view.btnPlayStop.text = getString(R.string.command_play)
                    view.btnPlayStop.setIconResource(R.drawable.ic_baseline_play_arrow_24)
                    view.chronometer.stop()
                    Recorder().stop()
                }
            }
        }

        Recorder.fileName.observe(mainLifecycleOwner) {
            if (it.isNotEmpty()) {
                view.tvRecordName.text = "Filename - $it"
            } else view.tvRecordName.text = ""
        }

        view.btnSave.setOnClickListener {
            Location().getLastLocation()
            FirebaseClass().uploadRecording(MainActivity.deviceId, Recorder.currentFile)
            FirebaseClass().upload(
                MainActivity.deviceId,
                "${view.etTitle.text}",
                Location.locationString.value.toString(),
                Recorder.currentFile.name
            )
            FirebaseClass().updateList()
            mainPager.currentItem = 2
        }
        return view
    }
}
