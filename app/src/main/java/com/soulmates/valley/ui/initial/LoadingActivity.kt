package com.soulmates.valley.ui.initial

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.soulmates.valley.R


object Loading {
    var activity: Activity = LoadingActivity()

    fun showLoading(context: Context) {
        val intent = Intent(context, LoadingActivity::class.java)
        context.startActivity(intent)
    }

    fun stopLoading() {
        val handler = Handler()
        handler.postDelayed({
            activity.finish()
        }, 300)
    }
}

class LoadingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        Loading.activity = this
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }
}