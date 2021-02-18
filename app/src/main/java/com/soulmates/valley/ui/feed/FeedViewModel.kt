package com.soulmates.valley.ui.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.PagedList
import com.soulmates.valley.base.BaseViewModel
import com.soulmates.valley.data.local.room.dao.BookmarkPostDao
import com.soulmates.valley.data.local.room.entity.BookmarkPost
import com.soulmates.valley.data.model.post.Post
import com.soulmates.valley.data.repository.feed.FeedRepository
import com.soulmates.valley.util.extension.ioThread
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class FeedViewModel(private val feedRepository: FeedRepository, private val dao: BookmarkPostDao) : BaseViewModel() {
    val tag = "FeedViewModel ->"

    private val homeFeedState = MutableLiveData<String>()
    val paginationStatus = feedRepository.homeFeedFactory.getPaginationStatus()
    private val homeFeedLiveData = Transformations.map(homeFeedState) { feedRepository.getHomeFeed() }
    val homeFeed = Transformations.switchMap(homeFeedLiveData) { it }!!
    fun showHomeFeed(state: String): Boolean {
        if (homeFeedState.value == state) return false

        homeFeedState.value = state
        return true
    }

    fun currentHomeFeed(): String? = homeFeedState.value

    fun refreshHomeFeed() {
        feedRepository.homeFeedFactory.getLiveDataSource().value?.invalidate()
    }

    val recoPaginationStatus = feedRepository.recommendFeedFactory.getPaginationStatus()
    fun getRecommendFeed(): LiveData<PagedList<Post>> {
        feedRepository.recommendFeedFactory.create()
        val result = feedRepository.getRecommendFeed()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .toFlowable(BackpressureStrategy.BUFFER)

        return LiveDataReactiveStreams.fromPublisher(result)
    }

    fun refreshRecommendFeed() {
        feedRepository.recommendFeedFactory.getLiveDataSource().value?.invalidate()
    }

    fun insertBookmarkPost(post: Post) =
        ioThread {
            dao.insert(BookmarkPost.postToBookmark(post))
        }
}