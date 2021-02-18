package com.soulmates.valley.data.model.friend

import com.google.gson.annotations.SerializedName


data class Follow(
    val userId: Int,
    val nickname: String,
    @SerializedName("profileImg")
    val userImage: String?,
    val description: String?,
    var isFollowed: Boolean
)