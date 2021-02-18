package com.soulmates.valley.data.model.friend.response

import com.soulmates.valley.data.model.friend.Follow


data class FollowListResponse(
    val code: Int,
    val data: List<Follow>
)