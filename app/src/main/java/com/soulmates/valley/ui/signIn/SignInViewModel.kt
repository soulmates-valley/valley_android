package com.soulmates.valley.ui.signIn

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.soulmates.valley.ValleyApplication
import com.soulmates.valley.base.BaseViewModel
import com.soulmates.valley.data.local.room.dao.UserInfoDao
import com.soulmates.valley.data.local.room.entity.UserProfile
import com.soulmates.valley.data.model.user.UserInfo
import com.soulmates.valley.data.repository.sign.SignInRepository
import com.soulmates.valley.util.extension.with
import com.soulmates.valley.util.extension.ioThread
import timber.log.Timber


class SignInViewModel(private val repository: SignInRepository, private val dao: UserInfoDao) :
    BaseViewModel() {

    private val tag = "SignInViewModel ->"

    private var _signInStatus = MutableLiveData<Int>()
    val signInStatus: LiveData<Int> get() = _signInStatus

    fun signIn(jsonObject: JsonObject) {
        addDisposable(
            disposable = repository.signIn(jsonObject)
            .with()
            .subscribe({
                Timber.tag("$tag 통신 성공 res : ").d(it.toString())
                when (it.code) {
                    200 -> {
                        _signInStatus.postValue(it.code)
                        ValleyApplication.prefManager.accessToken = it.data!!.accessToken
                        ValleyApplication.prefManager.refreshToken = it.data.refreshToken
                        ValleyApplication.prefManager.userId = it.data.userId.toString()
                        insertUserInfo(it.data)

                    }
                    else -> Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }) {
                android.os.Handler().post {
                    Timber.tag("$tag 통신 실패 error : ").d(it.message!!)
                    Toast.makeText(context, "서버 점검 중입니다.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun insertUserInfo(data: UserInfo) =
        ioThread {
            dao.deleteAll()
            dao.insert(UserProfile.to(data))
        }

}