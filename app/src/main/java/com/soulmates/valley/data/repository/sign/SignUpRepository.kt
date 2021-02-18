package com.soulmates.valley.data.repository.sign

import com.soulmates.valley.data.model.DefaultResponse
import com.google.gson.JsonObject
import com.soulmates.valley.data.model.user.response.EmailVerifyResponse
import com.soulmates.valley.data.remote.ValleyService
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody

class SignUpRepository(private val service: ValleyService) {
    fun signUp(map: HashMap<String, RequestBody?>, interest: ArrayList<Int>, image: MultipartBody.Part?): Single<DefaultResponse> =
        service.signUp(map, interest, image).map { it }

    fun emailVerify(jsonObject: JsonObject): Single<EmailVerifyResponse> =
        service.emailVerify("application/json;charset=utf-8", jsonObject).map { it }

    fun nickNameVerify(jsonObject: JsonObject): Single<DefaultResponse> =
        service.nickNameVerify("application/json;charset=utf-8", jsonObject).map { it }
}