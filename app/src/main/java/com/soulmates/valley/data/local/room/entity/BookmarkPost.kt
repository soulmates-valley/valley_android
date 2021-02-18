package com.soulmates.valley.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.soulmates.valley.data.model.post.Post

@Entity(tableName = "BookmarkPost")
data class BookmarkPost(
    @PrimaryKey(autoGenerate = false) val postId: Int,
    @ColumnInfo(name = "userId") val userId: Int,
    @ColumnInfo(name = "nickname") val nickname: String,
    @ColumnInfo(name = "userImage") val userImage: String?,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "images") val images: List<String>?,
    @ColumnInfo(name = "link") val link: String?,
    @ColumnInfo(name = "linkTitle") val linkTitle: String?,
    @ColumnInfo(name = "linkImage") val linkImage: String?,
    @ColumnInfo(name = "linkContent") val linkContent: String?,
    @ColumnInfo(name = "linkSiteName") val linkSiteName: String?,
    @ColumnInfo(name = "languageIdx") val languageIdx: String?,
    @ColumnInfo(name = "code") val code: String?,
    @ColumnInfo(name = "hashTag") val hashTag: List<String>?,
    @ColumnInfo(name = "likeCount") val likeCount: Int,
    @ColumnInfo(name = "commentCount") val commentCount: Int,
    @ColumnInfo(name = "isLiked") var isLiked: Boolean,
    @ColumnInfo(name = "createDt") val createDt: String
)

{
    companion object {
        fun postToBookmark(postData: Post): BookmarkPost {
            return BookmarkPost(
                postId = postData.postId,
                userId = postData.userId,
                nickname = postData.nickname,
                userImage = postData.userImage,
                content = postData.content,
                images = postData.images,
                link = postData.link,
                linkTitle = postData.linkTitle,
                linkImage = postData.linkImage,
                linkContent = postData.linkContent,
                linkSiteName = postData.linkSiteName,
                languageIdx = postData.languageIdx,
                code = postData.code,
                hashTag = postData.hashTag,
                likeCount = postData.likeCount,
                commentCount = postData.commentCount,
                isLiked = postData.isLiked,
                createDt = postData.createDt
            )
        }
    }
}