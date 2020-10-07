package com.example.naturediary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.minilist.view.*

class MiniListFileAdapter(
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
        val rw2 = inflater.inflate(R.layout.minilist, parent, false)
        val thisPos = ListFiles.files[pos]
        val threeLast = ListFiles.files.takeLast(3)


        rw2.tvMiniList.text = "id: ${thisPos.id}, name: ${thisPos.name}, location: ${thisPos.location}"

        return rw2
    }

}

