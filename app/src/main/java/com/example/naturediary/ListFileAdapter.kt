package com.example.naturediary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.naturediary.ListFiles.files
import kotlinx.android.synthetic.main.list.view.*

class ListFileAdapter(
    context: Context,
    private val dataSource: MutableList<ListFile>
) :
    BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(pos: Int): Any {
        return dataSource[pos]
    }

    override fun getItemId(pos: Int): Long {
        return pos.toLong()
    }

    override fun getView(pos: Int, conView: View?, parent: ViewGroup): View {
        val view = inflater.inflate(R.layout.list, parent, false)
        val thisPos = files[pos]

        view.tvTitle.text = "${thisPos.title}."
        view.tvSubTitle.text = "${thisPos.location}"
        view.btnPlayFromList.setOnClickListener {
            FirebaseClass().downloadRecording(MainActivity.deviceId, "${thisPos.fileName}")
        }
        return view
    }

}