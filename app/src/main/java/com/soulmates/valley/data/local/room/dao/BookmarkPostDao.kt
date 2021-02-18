package com.soulmates.valley.data.local.room.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.soulmates.valley.data.local.room.base.BaseDao
import com.soulmates.valley.data.local.room.entity.BookmarkPost

@Dao
interface BookmarkPostDao :
    BaseDao<BookmarkPost> {
    @Query("SELECT * FROM BookmarkPost")
    fun getPostList(): DataSource.Factory<Int, BookmarkPost>

    @Query("SELECT * FROM BookmarkPost WHERE postId = :postId")
    fun getPost(postId: Int): BookmarkPost?

    @Query("DELETE FROM BookmarkPost WHERE postId = :postId")
    fun deletePost(postId: Int)
}