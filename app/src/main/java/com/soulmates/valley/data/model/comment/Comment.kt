package com.soulmates.valley.data.model.comment

import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("id")
    val commentId: String,
    val userId: Int,
    @SerializedName("profileImg")
    val userImage: String?,
    val nickname: String?,
    val content: String?,
    val createDt: String?
)