package com.soulmates.valley.data.model.feed.response

import com.soulmates.valley.data.model.post.Post

data class FeedResponse(
    val code: Int,
    val data: List<Post>
)