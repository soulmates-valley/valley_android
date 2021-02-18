package com.soulmates.valley.data.repository.profile

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.soulmates.valley.data.model.post.Post
import com.soulmates.valley.data.repository.profile.profilePost.ProfilePostDataSourceFactory
import com.soulmates.valley.data.model.profile.response.ProfileResponse
import com.soulmates.valley.data.remote.ValleyService
import io.reactivex.Completable
import io.reactivex.Single
import java.util.concurrent.Executors


class ProfileRepository(
    private val service: ValleyService,
    private val dataSourceFactory: ProfilePostDataSourceFactory
) {
    fun getProfileInfo(userId: Int): Single<ProfileResponse> =
        service.getProfileInfo(userId).map { it }

    fun logout(): Completable = service.logout()

    private val executors = Executors.newFixedThreadPool(5)
    private val config = PagedList.Config.Builder()
        .setInitialLoadSizeHint(20)
        .setPageSize(20)
        .setPrefetchDistance(10)
        .setEnablePlaceholders(false)
        .build()

    val profilePostDataSourceFactory = dataSourceFactory
    fun getProfilePost(userId: Int): LiveData<PagedList<Post>> {
        dataSourceFactory.run {
            this.userId = userId
            create()
        }

        return LivePagedListBuilder(profilePostDataSourceFactory, config)
            .setInitialLoadKey(0)
            .setFetchExecutor(executors)
            .build()
    }
}