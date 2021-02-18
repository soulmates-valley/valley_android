package com.soulmates.valley.data.model.profile

import com.google.gson.annotations.SerializedName

data class Profile(
    val profileImg: String?,
    val nickname: String,
    val follower: Int,
    val following: Int,
    val interest: ArrayList<Int>,
    @SerializedName("profileLink")
    val link: String?,
    val description: String?,
    @SerializedName("isFollow")
    var isFollowing: Boolean
)