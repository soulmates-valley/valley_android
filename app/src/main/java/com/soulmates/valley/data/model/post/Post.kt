package com.soulmates.valley.data.model.post

import com.google.gson.annotations.SerializedName
import com.soulmates.valley.data.local.room.entity.BookmarkPost

data class Post(
    val userId: Int,
    val nickname: String,
    val userImage: String?,
    val postId: Int,
    val content: String,
    val images: ArrayList<String>?,
    val link: String?,
    val linkTitle: String?,
    val linkImage: String?,
    val linkContent: String?,
    val linkSiteName: String?,
    @SerializedName("codeType")
    val languageIdx: String?,
    val code: String?,
    val hashTag: List<String>?,
    @SerializedName("likeCnt")
    var likeCount: Int,
    @SerializedName("commentCnt")
    val commentCount: Int,
    @SerializedName("isUserLiked")
    var isLiked: Boolean,
    val createDt: String
)