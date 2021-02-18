package com.soulmates.valley.ui.signIn

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.soulmates.valley.R
import com.soulmates.valley.ValleyApplication
import com.soulmates.valley.base.BaseActivity
import com.soulmates.valley.databinding.ActivitySignInBinding
import com.soulmates.valley.ui.tab.MainActivity
import com.soulmates.valley.util.extension.buttonActiveGreen
import com.soulmates.valley.util.extension.buttonInactive
import com.soulmates.valley.util.extension.hideKeyboardWhenFocusOut
import com.soulmates.valley.util.extension.setSafeOnClickListener
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class SignInActivity : BaseActivity<ActivitySignInBinding, SignInViewModel>() {
    override val layoutResID: Int = R.layout.activity_sign_in
    override val viewModel: SignInViewModel by viewModel()
    private var emailFlag = false
    private var pwdFlag = false
    private var deviceToken: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setClick()
        setText()
        observe()
        getDeviceToken()
    }

    private fun setClick() {
        viewDataBinding.actSignInTvLogin.setSafeOnClickListener {
            if (viewDataBinding.actSignInEtEmail.text.isEmpty() || viewDataBinding.actSignInEtPwd.text.isEmpty()) {
                Toast.makeText(this, "아이디와 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {
                postSignIn()
            }
        }
        viewDataBinding.actSignInHeader.layoutHeaderBackIvBack.setSafeOnClickListener { finish() }
    }

    private fun setText() {
        viewDataBinding.actSignInHeader.layoutHeaderBackTvTitle.text = "로그인"

        viewDataBinding.actSignInEtEmail.apply {
            addTextChangedListener {
                emailFlag = !it.isNullOrBlank()
                setButton()
            }
            hideKeyboardWhenFocusOut()
        }

        viewDataBinding.actSignInEtPwd.apply {
            addTextChangedListener {
                pwdFlag = !it.isNullOrBlank()
                setButton()
            }
            hideKeyboardWhenFocusOut()
        }
    }

    private fun setButton() {
        viewDataBinding.actSignInTvLogin.apply {
            if (emailFlag && pwdFlag) {
                buttonActiveGreen(10)
            } else {
                buttonInactive(10)
            }
        }
    }

    private fun getDeviceToken(){
        FirebaseApp.initializeApp(this)
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if(!task.isSuccessful) {
                    Timber.tag("getFireBase").w(task.exception, "getInstanceId failed")
                    return@OnCompleteListener
                }
                deviceToken = ValleyApplication.prefManager.deviceToken
            })
    }

    private fun postSignIn() {
        val email = viewDataBinding.actSignInEtEmail.text.toString()
        val pwd = viewDataBinding.actSignInEtPwd.text.toString()
        val jsonObject = JSONObject()
        jsonObject.put("email", email)
        jsonObject.put("password", pwd)
        jsonObject.put("deviceToken", deviceToken)

        val body = JsonParser.parseString(jsonObject.toString()) as JsonObject
        viewModel.signIn(body)
    }

    private fun observe() {
        viewModel.signInStatus.observe(this, Observer {
            if (it == 200) {
                val intent = Intent(this@SignInActivity, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
        })
    }
}