package com.soulmates.valley.data.model.friend.response

import com.soulmates.valley.data.model.friend.RecommendFollow

data class LikeFollowResponse(
    val code: Int,
    val data: List<RecommendFollow>
)
