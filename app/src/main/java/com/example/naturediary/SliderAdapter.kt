package com.example.naturediary

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class SliderAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> FragmentMain()
            1 -> FragmentRecord()
            2 -> FragmentList()
            3 -> FragmentSettings()
            else -> FragmentList()
        }
}