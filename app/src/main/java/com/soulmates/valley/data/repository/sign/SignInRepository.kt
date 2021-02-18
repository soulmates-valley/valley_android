package com.soulmates.valley.data.repository.sign

import com.soulmates.valley.data.model.user.response.SignInResponse
import com.google.gson.JsonObject
import com.soulmates.valley.data.remote.ValleyService
import io.reactivex.Single

class SignInRepository(private val service: ValleyService) {
    fun signIn(jsonObject: JsonObject): Single<SignInResponse> =
        service.signIn("application/json;charset=utf-8", jsonObject).map { it }
}