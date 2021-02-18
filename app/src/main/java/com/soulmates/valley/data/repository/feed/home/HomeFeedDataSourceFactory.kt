package com.soulmates.valley.data.repository.feed.home

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.soulmates.valley.data.model.post.Post
import com.soulmates.valley.data.model.PaginationStatus
import com.soulmates.valley.data.remote.ValleyService

class HomeFeedDataSourceFactory (private val service: ValleyService): DataSource.Factory<Int, Post>(){
    private val liveDataSource : MutableLiveData<HomeFeedDataSource> = MutableLiveData()
    private val paginationStatus: MutableLiveData<PaginationStatus> = MutableLiveData()

    override fun create(): DataSource<Int, Post> {
        val dataSource =
            HomeFeedDataSource(
                service,
                paginationStatus
            )
        liveDataSource.postValue(dataSource)
        return dataSource
    }

    fun getLiveDataSource(): MutableLiveData<HomeFeedDataSource> {
        return liveDataSource
    }

    fun getPaginationStatus(): MutableLiveData<PaginationStatus>{
        return paginationStatus
    }
}