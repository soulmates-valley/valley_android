package com.soulmates.valley.data.repository.friend

import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.soulmates.valley.data.model.DefaultResponse
import com.soulmates.valley.data.model.friend.*
import com.soulmates.valley.data.repository.friend.follower.FollowerDataSourceFactory
import com.soulmates.valley.data.repository.friend.following.FollowingDataSourceFactory
import com.soulmates.valley.data.model.friend.response.FollowCountResponse
import com.soulmates.valley.data.model.friend.response.KnowFollowResponse
import com.soulmates.valley.data.model.friend.response.LikeFollowResponse
import com.soulmates.valley.data.remote.ValleyService
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class FriendRepository(
    private val service: ValleyService,
    private val followingDataSourceFactory: FollowingDataSourceFactory,
    private val followerDataSourceFactory: FollowerDataSourceFactory
) {
    fun getFollowCount(userId: Int): Single<FollowCountResponse> =
        service.getFollowCount("application/json;charset=utf-8", userId).map { it }

    fun postFollow(toUserId: Int): Single<DefaultResponse> =
        service.postFollow("application/json;charset=utf-8", toUserId).map { it }

    fun deleteFollow(toUserId: Int): Single<DefaultResponse> =
        service.deleteFollow("application/json;charset=utf-8", toUserId).map { it }

    private val config = PagedList.Config.Builder()
        .setInitialLoadSizeHint(10)
        .setPageSize(10)
        .setPrefetchDistance(10)
        .setEnablePlaceholders(false)
        .build()

    val followingFactory = followingDataSourceFactory
    val followerFactory = followerDataSourceFactory

    fun getFollowingList(): Observable<PagedList<Follow>> {
        return RxPagedListBuilder(followingFactory, config)
            .setInitialLoadKey(0)
            .setFetchScheduler(Schedulers.io())
            .setNotifyScheduler(AndroidSchedulers.mainThread())
            .buildObservable()
    }

    fun getFollowerList(): Observable<PagedList<Follow>> {
        return RxPagedListBuilder(followerFactory, config)
            .setInitialLoadKey(0)
            .setFetchScheduler(Schedulers.io())
            .setNotifyScheduler(AndroidSchedulers.mainThread())
            .buildObservable()
    }

    fun getKnowFollowList(userId: Int): Single<KnowFollowResponse> =
        service.getKnowFollowList(userId).map { it }

    fun getLikeFollowList(userId: Int): Single<LikeFollowResponse> =
        service.getLikeFollowList(userId).map { it }
}