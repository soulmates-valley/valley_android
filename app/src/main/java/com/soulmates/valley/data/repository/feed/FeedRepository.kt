package com.soulmates.valley.data.repository.feed

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.soulmates.valley.data.repository.feed.home.HomeFeedDataSourceFactory
import com.soulmates.valley.data.repository.feed.recommend.RecommendFeedDataSourceFactory
import com.soulmates.valley.data.model.post.Post
import com.soulmates.valley.data.remote.ValleyService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors

class FeedRepository(
    private val service: ValleyService,
    private val homeFeedDataSourceFactory: HomeFeedDataSourceFactory,
    private val recommendFeedDataSourceFactory: RecommendFeedDataSourceFactory
) {
    private val executors = Executors.newFixedThreadPool(5)
    private val config = PagedList.Config.Builder()
        .setInitialLoadSizeHint(20)
        .setPageSize(20)
        .setPrefetchDistance(10)
        .setEnablePlaceholders(false)
        .build()

    val homeFeedFactory = homeFeedDataSourceFactory
    val recommendFeedFactory = recommendFeedDataSourceFactory

    fun getHomeFeed(): LiveData<PagedList<Post>>{
        homeFeedDataSourceFactory.create()
        return LivePagedListBuilder(homeFeedDataSourceFactory, config)
            .setInitialLoadKey(0)
            .setFetchExecutor(executors)
            .build()
    }

    fun getRecommendFeed(): Observable<PagedList<Post>> {
        recommendFeedDataSourceFactory.create()
        return RxPagedListBuilder(recommendFeedFactory, config)
            .setInitialLoadKey(0)
            .setFetchScheduler(Schedulers.io())
            .setNotifyScheduler(AndroidSchedulers.mainThread())
            .buildObservable()
    }
}