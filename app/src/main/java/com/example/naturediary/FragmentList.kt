package com.example.naturediary

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_list.view.*

class FragmentList : Fragment() {

    companion object {
        lateinit var mainContext: Context

        fun init(context: Context) {
            mainContext = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        view.listView.adapter = ListFileAdapter(mainContext, ListFiles.files as ArrayList<ListFile>)
        return view
    }
}
