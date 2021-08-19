package com.jambosoft.bangbang.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.jambosoft.bangbang.Fragment.*

class MainFragmentStatePagerAdapter(fm : FragmentManager, val fragmentCount : Int) : FragmentStatePagerAdapter(fm) {
    var fragments : ArrayList<Fragment> = ArrayList()
    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> return HomeFragment()
            1 -> return LocationFragment()
            2 -> return LikeFragment()
            3 -> return CommunityFragment()
            else -> return HomeFragment()
        }
    }
    override fun getCount(): Int = fragmentCount // 자바에서는 { return fragmentCount }

}
