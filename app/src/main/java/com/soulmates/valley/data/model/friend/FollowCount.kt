package com.soulmates.valley.data.model.friend

import com.google.gson.annotations.SerializedName

data class FollowCount(
    @SerializedName("inCnt")
    val followerCount: Int,
    @SerializedName("outCnt")
    val followingCount: Int
)