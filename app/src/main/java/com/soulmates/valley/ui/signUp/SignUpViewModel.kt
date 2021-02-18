package com.soulmates.valley.ui.signUp

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.soulmates.valley.base.BaseViewModel
import com.soulmates.valley.data.repository.sign.SignUpRepository
import com.soulmates.valley.util.extension.with
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File
import java.net.URI
import java.net.URLEncoder


class SignUpViewModel(
    private val repository: SignUpRepository
) : BaseViewModel() {

    private val tag = "SignUpViewModel ->"

    private var _emailStatus = MutableLiveData<Int>()
    val emailStatus: LiveData<Int> get() = _emailStatus

    fun emailVerify(body: JsonObject) {
        addDisposable(
            disposable = repository.emailVerify(body)
            .with()
            .subscribe({
                Timber.tag("$tag 이메일 전송 통신 성공 res : ").d(it.toString())
                _emailStatus.postValue(it.code)
                if (it.code == 200) {
                    _verifyCode.postValue(it.data.verifyCode)
                }
            }) {
                Timber.tag("$tag 이메일 전송 통신 실패 error : ").d(it)
                Toast.makeText(context, "서버 점검 중입니다.", Toast.LENGTH_SHORT).show()
            })
    }

    private var _verifyCode = MutableLiveData<String>()
    private var _verifyCodeStatus = MutableLiveData<Int>()
    val verifyCodeStatus: LiveData<Int> get() = _verifyCodeStatus

    fun codeVerify(inputCode: String) {
        if (inputCode == _verifyCode.value) {
            _verifyCodeStatus.postValue(200)
        } else {
            _verifyCodeStatus.postValue(400)
        }
    }

    private var _nickNameStatus = MutableLiveData<Int>()
    val nickNameStatus: LiveData<Int> get() = _nickNameStatus

    fun nickNameVerify(body: JsonObject) {
        addDisposable(
            disposable = repository.nickNameVerify(body)
            .with()
            .subscribe({
                Timber.tag("$tag 닉네임 확인 통신 성공 res : ").d(it.toString())
                _nickNameStatus.postValue(it.code)
            }) {
                Timber.tag("$tag 닉네임 확인 통신 실패 error : ").d(it)
                Toast.makeText(context, "서버 점검 중입니다.", Toast.LENGTH_SHORT).show()
            })
    }

    private var _signUpStatus = MutableLiveData<Int>()
    val signUpStatus: LiveData<Int> get() = _signUpStatus

    private fun makeRequestBody(value: String?): RequestBody? {
        value?.let { return RequestBody.create(MediaType.parse("text/plain"), value) }
        return null
    }

    fun postSignUp(body: JsonObject, interest: ArrayList<Int>, profileImg: String?) {
        val map: HashMap<String, RequestBody?> = HashMap()
        map.apply {
            put("email", makeRequestBody(body.get("email").asString))
            put("password", makeRequestBody(body.get("password").asString))
            put("nickname", makeRequestBody(body.get("nickname").asString))
            put("description", makeRequestBody(body.get("description").asString))
            put("profileLink", makeRequestBody(body.get("profileLink").asString))
            put("birthYear", makeRequestBody(body.get("birthYear").asString))
        }

        var imageData: MultipartBody.Part? = null
        profileImg?.let {
            val uri = URI.create(it)
            val file = File(uri.path)
            val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            imageData = MultipartBody.Part.createFormData("profileImg", URLEncoder.encode(file.name, "UTF-8"), requestFile)
        }

        addDisposable(
            disposable = repository.signUp(map, interest, if(profileImg.isNullOrBlank()) null else imageData)
            .with()
            .subscribe({
                Timber.tag("$tag 통신 성공 res : ").d(it.toString())
                _signUpStatus.postValue(it.code)
            }) {
                Timber.tag("$tag 통신 실패 error : ").d(it.message!!)
                Toast.makeText(context, "서버 점검 중입니다.", Toast.LENGTH_SHORT).show()
            })
    }
}