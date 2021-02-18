package com.soulmates.valley.util.listener

import android.app.Activity
import android.util.Log
import android.widget.Toast

class BackPressHandler(val activity: Activity) {
    private var backKeyPressedTime : Long = 0
    lateinit var toast: Toast

    fun onBackPressed(msg: String = "뒤로 버튼을 한번 더 누르시면 종료됩니다."){
        if(System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis()
            showGuide(msg)

            return
        }else if(System.currentTimeMillis() <= backKeyPressedTime + 2000){

            activity.finish()
            toast.cancel()
        }
    }

    private fun showGuide(msg : String){
        toast = Toast.makeText(activity, msg, Toast.LENGTH_SHORT)
        toast.show()
    }
}