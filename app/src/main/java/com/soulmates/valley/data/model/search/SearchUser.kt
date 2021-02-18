package com.soulmates.valley.data.model.search

import com.google.gson.annotations.SerializedName

data class SearchUser(
    val userId: Int,
    val nickname: String,
    @SerializedName("profileImg")
    val userImage: String?,
    val interest: ArrayList<Int>?
)