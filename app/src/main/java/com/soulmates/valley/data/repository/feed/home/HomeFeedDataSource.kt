package com.soulmates.valley.data.repository.feed.home

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.soulmates.valley.data.model.post.Post
import com.soulmates.valley.data.model.PaginationStatus
import com.soulmates.valley.data.remote.ValleyService
import com.soulmates.valley.util.extension.with
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class HomeFeedDataSource(
    private val service: ValleyService,
    private val paginationStatus: MutableLiveData<PaginationStatus>
) : PageKeyedDataSource<Int, Post>() {
    private val compositeDisposable = CompositeDisposable()

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Post>
    ) {
        compositeDisposable.add(
            service.getHomeFeed(FIRST_PAGE, params.requestedLoadSize).map { it }
                .with()
                .subscribe({
                    if (it.code == 200) {
                        paginationStatus.postValue(
                            if (it.data.isEmpty()) PaginationStatus.Empty else PaginationStatus.NotEmpty
                        )
                        callback.onResult(it.data, null, FIRST_PAGE + 1)
                    }
                }) {
                    paginationStatus.postValue(PaginationStatus.Error)
                }
        )
    }

    val FIRST_PAGE = 0
    val tag = "HomeFeedDataSource -> "

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Post>) {
        compositeDisposable.add(
            service.getHomeFeed(params.key, params.requestedLoadSize).map { it }
                .with()
                .subscribe({
                    if (it.code == 200) callback.onResult(it.data, params.key + 1)
                }) {
                    Timber.tag("$tag getHomeFeed 통신 실패 error : ").d(it)
                }
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Post>) {}

    override fun invalidate() {
        super.invalidate()
        compositeDisposable.dispose()
    }
}