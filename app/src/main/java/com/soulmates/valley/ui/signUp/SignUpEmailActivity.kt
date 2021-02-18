package com.soulmates.valley.ui.signUp

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.soulmates.valley.R
import com.soulmates.valley.base.BaseActivity
import com.soulmates.valley.databinding.ActivitySignUpEmailBinding
import com.soulmates.valley.util.extension.buttonActiveGreen
import com.soulmates.valley.util.extension.buttonInactive
import com.soulmates.valley.util.extension.setSafeOnClickListener
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.regex.Pattern


class SignUpEmailActivity : BaseActivity<ActivitySignUpEmailBinding, SignUpViewModel>() {

    override val layoutResID: Int = R.layout.activity_sign_up_email
    override val viewModel: SignUpViewModel by viewModel()
    private val pwdPattern: Pattern? =
        Pattern.compile("(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{6,20}", Pattern.CASE_INSENSITIVE)
    private var email = ""
    private var pwd = ""
    private var emailFlag = false
    private var pwdFlag = false
    private var pwdCheckFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewDataBinding.actSignUpEmailHeader.layoutHeaderBackIvBack.setSafeOnClickListener { finish() }
        viewDataBinding.actSignUpEmailHeader.layoutHeaderBackTvTitle.text = "회원가입"

        viewDataBinding.vm = viewModel
        setClick()
        observe()
        setTextChange()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val focusView = currentFocus
        if (focusView != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev.x.toInt()
            val y = ev.y.toInt()
            if (!rect.contains(x, y)) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(focusView.windowToken, 0)
                focusView.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun setTextChange() {
        viewDataBinding.actSignUpEmailEtEmail.addTextChangedListener {
            if (!it.isNullOrBlank() && Patterns.EMAIL_ADDRESS.matcher(it.toString()).matches()) {
                viewDataBinding.actSignUpEmailTvSendNumber.buttonActiveGreen(5)
            } else {
                viewDataBinding.actSignUpEmailTvSendNumber.buttonInactive(5)
            }
        }
        viewDataBinding.actSignUpEmailEtNumber.addTextChangedListener {
            if (!it.isNullOrBlank()) {
                viewDataBinding.actSignUpEmailTvVerify.buttonActiveGreen(5)
            } else {
                viewDataBinding.actSignUpEmailTvVerify.buttonInactive(5)
            }
        }
        viewDataBinding.actSignUpEmailEtPwd.addTextChangedListener {
            if (!(pwdPattern!!.matcher(it.toString()).matches())) {
                viewDataBinding.actSignUpEmailTvPwdWarn.visibility = View.VISIBLE
                pwdFlag = false
            } else {
                pwdFlag = true
                viewDataBinding.actSignUpEmailTvPwdWarn.visibility = View.INVISIBLE
            }
            setButton()
        }

        viewDataBinding.actSignUpEmailEtPwdCheck.addTextChangedListener {
            pwd = viewDataBinding.actSignUpEmailEtPwd.text.toString()
            if (pwd != it.toString()) {
                viewDataBinding.actSignUpEmailTvPwdCheckWarn.visibility = View.VISIBLE
                viewDataBinding.actSignUpEmailTvPwdCheck.visibility = View.INVISIBLE
                pwdCheckFlag = false
            } else {
                viewDataBinding.actSignUpEmailTvPwdCheckWarn.visibility = View.INVISIBLE
                viewDataBinding.actSignUpEmailTvPwdCheck.visibility = View.VISIBLE
                pwdCheckFlag = true
            }
            setButton()
        }
    }

    private fun setButton() {
        viewDataBinding.actSignUpEmailTvNext.apply {
            if (emailFlag && pwdFlag && pwdCheckFlag) {
                buttonActiveGreen(10)
            } else if (!emailFlag || !pwdFlag || !pwdCheckFlag) {
                buttonInactive(10)
            }
        }
    }

    private fun setClick() {
        viewDataBinding.actSignUpEmailEtPwdCheck.setOnKeyListener(View.OnKeyListener { v, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                v.clearFocus()
                val keyboard: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                keyboard.hideSoftInputFromWindow(
                    viewDataBinding.actSignUpEmailEtPwdCheck.windowToken,
                    0
                )
                return@OnKeyListener true
            }
            false
        })

        viewDataBinding.actSignUpEmailTvSendNumber.setSafeOnClickListener {
            sendEmail()
        }

        viewDataBinding.actSignUpEmailTvVerify.setSafeOnClickListener {
            viewModel.codeVerify(viewDataBinding.actSignUpEmailEtNumber.text.toString())
        }

        viewDataBinding.actSignUpEmailTvNext.setSafeOnClickListener {
            val intent = Intent(this, SignUpProfileActivity::class.java)
            val jsonObject = JSONObject().apply {
                put("email", email)
                put("password", pwd)
            }
            intent.putExtra("json", jsonObject.toString())
            startActivity(intent)
        }
    }

    private fun sendEmail() {
        val jsonObject = JSONObject()
        email = viewDataBinding.actSignUpEmailEtEmail.text.toString()
        jsonObject.put("email", email)
        val body = JsonParser.parseString(jsonObject.toString()) as JsonObject
        viewModel.emailVerify(body)
    }

    private fun observe() {
        viewModel.verifyCodeStatus.observe(this, Observer {
            emailFlag = it == 200
            setButton()
        })
    }
}