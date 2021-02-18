package com.soulmates.valley.base

import android.os.Parcelable
import android.view.MotionEvent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class BasePagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){

    private val setOfFragments: MutableSet<Fragment> = mutableSetOf()

    override fun getItem(position: Int): Fragment = setOfFragments.elementAt(position)

    override fun getCount(): Int = setOfFragments.size

    override fun saveState(): Parcelable? = null

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
    }

    fun addFragment(fragment: Fragment) {
        setOfFragments.add(fragment)
        notifyDataSetChanged()
    }
}