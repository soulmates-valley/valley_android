package com.soulmates.valley.data.model.profile.response

import com.soulmates.valley.data.model.post.Post

data class ProfilePostResponse(
    val code : Int,
    val data: List<Post>
)