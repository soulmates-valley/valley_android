package com.soulmates.valley.data.repository.feed.recommend

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.soulmates.valley.data.model.post.Post
import com.soulmates.valley.data.model.PaginationStatus
import com.soulmates.valley.data.remote.ValleyService
import com.soulmates.valley.util.extension.with
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class RecommendFeedDataSource (private val service: ValleyService, private val paginationStatus: MutableLiveData<PaginationStatus>): PageKeyedDataSource<Int, Post>() {
    private val compositeDisposable = CompositeDisposable()
    val tag = "RecommendFeedDataSource -> "

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Post>
    ) {
        compositeDisposable.add(
            service.getRecommendFeed().map { it }
                .with()
                .subscribe({
                    if (it.code == 200) {
                        paginationStatus.postValue(
                            if (it.data.isEmpty()) PaginationStatus.Empty else PaginationStatus.NotEmpty
                        )
                        callback.onResult(it.data, null, null)
                    }
                    else paginationStatus.postValue(PaginationStatus.Empty)
                }) {
                    Timber.tag("$tag getRecommendFeed 통신 실패 error : ").d(it)
                }
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Post>) {
        compositeDisposable.add(
            service.getRecommendFeed().map { it }
                .with()
                .subscribe({
                    if (it.code == 200 && it.data.isNotEmpty()) {
                        callback.onResult(it.data, params.key + 1)
                    }
                }) {
                    Timber.tag("$tag getRecommendFeed 통신 실패 error : ").d(it)
                }
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Post>) { }

    override fun invalidate() {
        super.invalidate()
        compositeDisposable.dispose()
    }
}