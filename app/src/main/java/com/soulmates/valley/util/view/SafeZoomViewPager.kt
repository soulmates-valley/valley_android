package com.soulmates.valley.util.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class SafeZoomViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean = try {
        super.onInterceptTouchEvent(ev)
    } catch (e: java.lang.IllegalArgumentException) {
        false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean = try {
        super.onTouchEvent(ev)
    } catch (e: java.lang.IllegalArgumentException) {
        false
    }
}