package com.soulmates.valley.ui.initial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.soulmates.valley.R
import com.soulmates.valley.ui.signIn.SignInActivity
import com.soulmates.valley.ui.signUp.SignUpEmailActivity
import com.soulmates.valley.util.extension.setSafeOnClickListener
import kotlinx.android.synthetic.main.activity_initial.*

class InitialActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial)

        setClick()
    }

    private fun setClick(){
        act_initial_tv_sign_in.setSafeOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        act_initial_tv_sign_up.setSafeOnClickListener {
            val intent = Intent(this, SignUpEmailActivity::class.java)
            startActivity(intent)
        }

    }
}