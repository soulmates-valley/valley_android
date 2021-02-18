package com.soulmates.valley.data.repository.post.comment

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.soulmates.valley.data.model.comment.Comment
import com.soulmates.valley.data.remote.ValleyService

class CommentDataSourceFactory (private val service: ValleyService): DataSource.Factory<Int, Comment>(){
    private val liveDataSource : MutableLiveData<CommentDataSource> = MutableLiveData()
    var postId = -1

    override fun create(): DataSource<Int, Comment> {
        val dataSource =
            CommentDataSource(
                service,
                postId
            )
        liveDataSource.postValue(dataSource)
        return dataSource
    }

    fun getLiveDataSource(): MutableLiveData<CommentDataSource> {
        return liveDataSource
    }
}