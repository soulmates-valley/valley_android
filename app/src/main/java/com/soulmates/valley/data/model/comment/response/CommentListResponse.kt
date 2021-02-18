package com.soulmates.valley.data.model.comment.response

import com.soulmates.valley.data.model.comment.Comment


data class CommentListResponse(
    val code: Int,
    val data: ArrayList<Comment>
)