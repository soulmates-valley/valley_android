package com.soulmates.valley.data.model.user

data class UserInfo(
    val accessToken: String,
    val refreshToken: String,
    val userId: Int,
    val nickname: String,
    val profileImg: String?,
    val description: String?,
    val link: String?,
    val interest: List<Int>
)