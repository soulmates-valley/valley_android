package com.soulmates.valley.util.extension

import android.view.View
import com.soulmates.valley.util.listener.SafeClickListener

fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener =
        SafeClickListener {
            onSafeClick(it)
        }
    setOnClickListener(safeClickListener)

}