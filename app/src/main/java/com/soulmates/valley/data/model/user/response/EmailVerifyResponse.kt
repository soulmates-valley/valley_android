package com.soulmates.valley.data.model.user.response

import com.google.gson.annotations.SerializedName

data class EmailVerifyResponse(
    val code: Int,
    val data: Code
)

data class Code(
    @SerializedName("verifycode")
    val verifyCode: String?
)