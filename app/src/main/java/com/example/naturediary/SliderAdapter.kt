package com.example.naturediary

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class SliderAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> FragmentMain()
            1 -> FragmentSpeech()
            2 -> FragmentRecord()
            3 -> FragmentList()
            else -> FragmentList()
        }
}