package com.soulmates.valley.data.repository.feed.recommend

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.soulmates.valley.data.model.post.Post
import com.soulmates.valley.data.model.PaginationStatus
import com.soulmates.valley.data.remote.ValleyService

class RecommendFeedDataSourceFactory(private val service: ValleyService): DataSource.Factory<Int, Post>(){
    private val liveDataSource : MutableLiveData<RecommendFeedDataSource> = MutableLiveData()
    private val paginationStatus: MutableLiveData<PaginationStatus> = MutableLiveData()

    override fun create(): DataSource<Int, Post> {
        val dataSource =
            RecommendFeedDataSource(
                service,
                paginationStatus
            )
        liveDataSource.postValue(dataSource)
        return dataSource
    }

    fun getLiveDataSource(): MutableLiveData<RecommendFeedDataSource> {
        return liveDataSource
    }

    fun getPaginationStatus(): MutableLiveData<PaginationStatus>{
        return paginationStatus
    }
}