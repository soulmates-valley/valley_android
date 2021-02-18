package com.soulmates.valley.data.repository.search.post

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.soulmates.valley.data.model.post.Post
import com.soulmates.valley.data.model.PaginationStatus
import com.soulmates.valley.data.remote.ValleyService

class SearchPostDataSourceFactory(private val service: ValleyService): DataSource.Factory<Int, Post>(){
    private val liveDataSource: MutableLiveData<SearchPostDataSource> = MutableLiveData()
    private val paginationStatus: MutableLiveData<PaginationStatus> = MutableLiveData()
    var keyword = ""

    override fun create(): DataSource<Int, Post> {
        val dataSource = SearchPostDataSource(service, keyword, paginationStatus)
        liveDataSource.postValue(dataSource)
        return dataSource
    }

    fun getLiveDataSource(): MutableLiveData<SearchPostDataSource> {
        return liveDataSource
    }

    fun getPaginationStatus(): MutableLiveData<PaginationStatus>{
        return paginationStatus
    }
}