package com.soulmates.valley.data.model.user.response

import com.soulmates.valley.data.model.user.UserInfo

data class SignInResponse(
    val code: Int,
    val message: String,
    val data: UserInfo?
)
