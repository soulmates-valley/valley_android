package com.soulmates.valley.data.model.friend

data class RecommendFollow(
    val userId: Int,
    val nickname: String,
    val profileImg: String?,
    val description: String?,
    val interest: ArrayList<Int>,
    var isFollowed: Boolean
)