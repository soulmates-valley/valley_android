package com.soulmates.valley.data.repository.friend.follower

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.soulmates.valley.data.model.friend.Follow
import com.soulmates.valley.data.remote.ValleyService

class FollowerDataSourceFactory (private val service: ValleyService): DataSource.Factory<Int, Follow>(){
    private val liveDataSource : MutableLiveData<FollowerDataSource> = MutableLiveData()

    override fun create(): DataSource<Int, Follow> {
        val dataSource =
            FollowerDataSource(
                service
            )
        liveDataSource.postValue(dataSource)
        return dataSource
    }

    fun getLiveDataSource(): MutableLiveData<FollowerDataSource> {
        return liveDataSource
    }
}