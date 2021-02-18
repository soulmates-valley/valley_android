package com.soulmates.valley.data.model.comment.response

import com.soulmates.valley.data.model.comment.Comment

data class CommentResponse(
    val code: Int,
    val data: Comment?
)