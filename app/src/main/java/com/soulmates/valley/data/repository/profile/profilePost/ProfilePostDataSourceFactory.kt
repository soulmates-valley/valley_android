package com.soulmates.valley.data.repository.profile.profilePost

import androidx.lifecycle.MutableLiveData
import com.soulmates.valley.data.model.post.Post
import androidx.paging.DataSource
import com.soulmates.valley.data.model.PaginationStatus
import com.soulmates.valley.data.remote.ValleyService
import com.soulmates.valley.data.repository.profile.profilePost.ProfilePostDataSource

class ProfilePostDataSourceFactory(private val service: ValleyService): DataSource.Factory<Int, Post>(){
    private val liveDataSource: MutableLiveData<ProfilePostDataSource> = MutableLiveData()
    private val paginationStatus: MutableLiveData<PaginationStatus> = MutableLiveData()

    var userId = -1
    override fun create(): DataSource<Int, Post> {
        val dataSource =
            ProfilePostDataSource(
                service,
                userId,
                paginationStatus
            )
        liveDataSource.postValue(dataSource)
        return dataSource
    }

    fun getPaginationStatus(): MutableLiveData<PaginationStatus>{
        return paginationStatus
    }

    fun getLiveDataSource(): MutableLiveData<ProfilePostDataSource>{
        return liveDataSource
    }
}