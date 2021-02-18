package com.soulmates.valley.data.model.search.response

import com.soulmates.valley.data.model.post.Post

data class SearchPostResponse(
    val code: Int,
    val data: List<Post>
)