package com.soulmates.valley.data.repository.search.tag

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.soulmates.valley.data.model.post.Post
import com.soulmates.valley.data.remote.ValleyService

class SearchTagDetailDataSourceFactory(private val service: ValleyService): DataSource.Factory<Int, Post>(){
    private val liveDataSource: MutableLiveData<SearchTagDetailDataSource> = MutableLiveData()
    var keyword = ""

    override fun create(): DataSource<Int, Post> {
        val dataSource = SearchTagDetailDataSource(service, keyword)
        liveDataSource.postValue(dataSource)
        return dataSource
    }

    fun getLiveDataSource(): MutableLiveData<SearchTagDetailDataSource> {
        return liveDataSource
    }
}