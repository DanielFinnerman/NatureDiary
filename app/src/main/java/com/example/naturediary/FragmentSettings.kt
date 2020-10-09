package com.example.naturediary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_settings.view.*

class FragmentSettings : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        view.btnRemoveAll.setOnClickListener {
            for (item in ListFiles.files){
                FirebaseClass().deleteFile(item.createdAt)
            }
        }

        return view
    }
}
