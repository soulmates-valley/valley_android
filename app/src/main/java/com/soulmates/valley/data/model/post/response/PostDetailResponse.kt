package com.soulmates.valley.data.model.post.response

import com.soulmates.valley.data.model.post.Post

data class PostDetailResponse(
    val code: Int,
    val data: Post
)