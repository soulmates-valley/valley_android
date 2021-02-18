package com.soulmates.valley.util.view

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.soulmates.valley.ValleyApplication.Companion.globalApplication
import timber.log.Timber


class WrapContentLinearLayoutManager(orientation: Int = RecyclerView.VERTICAL) : LinearLayoutManager(
    globalApplication, orientation, false) {

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            Timber.tag("WrapContentLinearLayoutManager e : ").d("IndexOutOfBoundsException")
        }

    }
}