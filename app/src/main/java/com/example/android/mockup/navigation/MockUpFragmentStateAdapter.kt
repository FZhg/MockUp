package com.example.android.mockup.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.android.mockup.R

class MockUpFragmentStateAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return  4
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 ->  FlipboardFragment()
            1 -> InstantFragment()
            2 -> MintHealthFragment()
            3 -> XiaoMiSportsFragment()
            else -> FlipboardFragment()
        }
    }
}