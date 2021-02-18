package com.soulmates.valley.ui.signUp

import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.soulmates.valley.R
import com.soulmates.valley.base.BaseActivity
import com.soulmates.valley.databinding.ActivitySignUpProfileBinding
import com.soulmates.valley.util.extension.*
import com.soulmates.valley.util.extension.buttonActiveGreen
import com.soulmates.valley.util.extension.buttonInactive
import gun0912.tedimagepicker.builder.TedRxImagePicker
import gun0912.tedimagepicker.builder.type.MediaType
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignUpProfileActivity : BaseActivity<ActivitySignUpProfileBinding, SignUpViewModel>() {

    override val layoutResID: Int = R.layout.activity_sign_up_profile
    override val viewModel: SignUpViewModel by viewModel()

    private var nicknameCheckFlag = false // 중복검사 완료되면 true로 바꾸고 다음 버튼 active
    private var birthFlag = false
    private var profileImg = ""
    private lateinit var jsonObject: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewDataBinding.vm = viewModel
        jsonObject = JSONObject(intent.getStringExtra("json")!!)
        setText()
        setClick()
        setImageButton()
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

    private val onDialogFragmentDismissListener =
        object : BirthPickerDialogFragment.BirthPickerDialogDismissListener {
            override fun onDialogDismissed(year: String) {
                viewDataBinding.actSignUpProfileTvPicker.apply {
                    text = year
                    setTextColor(ContextCompat.getColor(this@SignUpProfileActivity, R.color.white))
                }
                setButton()
            }
        }

    private fun setText() {
        viewDataBinding.actSignUpProfileHeader.layoutHeaderBackIvBack.setSafeOnClickListener { finish() }
        viewDataBinding.actSignUpProfileHeader.layoutHeaderBackTvTitle.text = "회원가입"

        viewDataBinding.actSignUpProfileEtNickname.addTextChangedListener {
            viewDataBinding.actSignUpProfileTvNicknameCheck.apply {
                if (!it.isNullOrBlank()) {
                    if (it.length > 20) Toast.makeText(
                        this@SignUpProfileActivity,
                        "닉네임은 최대 20자까지 입력 가능합니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                    buttonActiveGreen(5)
                } else buttonInactive(5)
            }
        }
    }

    private fun setButton() {
        viewDataBinding.actSignUpProfileTvNext.apply {
            if (nicknameCheckFlag && birthFlag) buttonActiveGreen(10)
            else buttonInactive(10)
        }
    }

    private fun setImageButton() {
        viewDataBinding.actSignUpProfileIvImage.setSafeOnClickListener {
            if (profileImg.isBlank()) {
                TedRxImagePicker.with(this)
                    .mediaType(MediaType.IMAGE)
                    .buttonTextColor(R.color.black)
                    .start()
                    .subscribe(this::showSingleImage, Throwable::printStackTrace)
            } else {
                profileImg = ""
                viewDataBinding.actSignUpProfileCivPhoto.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@SignUpProfileActivity,
                        R.drawable.img_profile_photo
                    )
                )
                viewDataBinding.actSignUpProfileIvImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@SignUpProfileActivity,
                        R.drawable.ic_profile_photo_add
                    )
                )
                viewDataBinding.run {
                    actSignUpProfileCivPhoto.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@SignUpProfileActivity,
                            R.drawable.img_profile_photo
                        )
                    )
                    actSignUpProfileIvImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@SignUpProfileActivity,
                            R.drawable.ic_profile_photo_add
                        )
                    )
                    actSignUpProfileView.setBackgroundResource(R.drawable.circle_border_414141)
                }
            }
        }
    }

    private fun setClick() {
        viewDataBinding.actSignUpProfileTvNicknameCheck.setSafeOnClickListener {
            val nickNameObject = JSONObject()
            nickNameObject.put("nickname", viewDataBinding.actSignUpProfileEtNickname.text)
            viewModel.nickNameVerify(JsonParser.parseString(nickNameObject.toString()) as JsonObject)
            observe()
        }

        viewDataBinding.actSignUpProfileTvPicker.setSafeOnClickListener {
            birthFlag = true
            BirthPickerDialogFragment().apply {
                setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog)
                setDialogDismissListener(onDialogFragmentDismissListener)
                show(this@SignUpProfileActivity.supportFragmentManager, "birthPicker")
            }
        }

        viewDataBinding.actSignUpProfileTvNext.setSafeOnClickListener {
            jsonObject.apply {
                put("description", viewDataBinding.actSignUpProfileEtInfo.text.toString())
                put("nickname", viewDataBinding.actSignUpProfileEtNickname.text.toString())
                put("profileLink", viewDataBinding.actSignUpProfileEtLink.text.toString())
                put("birthYear", viewDataBinding.actSignUpProfileTvPicker.text.toString())
            }

            val intent = Intent(this, SignUpInterestActivity::class.java)
            intent.putExtra("json", jsonObject.toString())
            if (!profileImg.isBlank()) intent.putExtra("profileImg", profileImg)
            startActivity(intent)
        }
    }

    private fun observe() {
        viewModel.nickNameStatus.observe(this, Observer {
            when (it) {
                200 -> nicknameCheckFlag = true
                211 -> nicknameCheckFlag = false
            }
            setButton()
        })
    }

    private fun showSingleImage(uri: Uri) {
        val activity = this
        if (activity.isFinishing) {
            Toast.makeText(activity, "이미지를 로드할 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        viewDataBinding.run {
            actSignUpProfileView.setBackgroundResource(R.drawable.circle_border_main_yellow)
            actSignUpProfileIvImage.setImageDrawable(
                ContextCompat.getDrawable(
                    this@SignUpProfileActivity,
                    R.drawable.ic_profile_photo_delete
                )
            )
            if (this@SignUpProfileActivity.isFinishing) return
            Glide.with(this@SignUpProfileActivity)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(actSignUpProfileCivPhoto)
        }
        profileImg = uri.toString()
        setImageButton()
    }
}