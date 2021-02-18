package com.soulmates.valley.data.repository.friend.following

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.soulmates.valley.data.model.friend.Follow
import com.soulmates.valley.data.remote.ValleyService

class FollowingDataSourceFactory(private val service: ValleyService) :
    DataSource.Factory<Int, Follow>() {
    private val liveDataSource: MutableLiveData<FollowingDataSource> = MutableLiveData()

    override fun create(): DataSource<Int, Follow> {
        val dataSource =
            FollowingDataSource(
                service
            )
        liveDataSource.postValue(dataSource)
        return dataSource
    }

    fun getLiveDataSource(): MutableLiveData<FollowingDataSource> {
        return liveDataSource
    }
}