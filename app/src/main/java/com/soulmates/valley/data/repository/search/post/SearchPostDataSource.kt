package com.soulmates.valley.data.repository.search.post

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.soulmates.valley.data.model.post.Post
import com.soulmates.valley.data.model.PaginationStatus
import com.soulmates.valley.data.remote.ValleyService
import com.soulmates.valley.util.extension.with
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class SearchPostDataSource(
    private val service: ValleyService,
    private val keyword: String,
    private val paginationStatus: MutableLiveData<PaginationStatus>
) : PageKeyedDataSource<Int, Post>() {
    private val compositeDisposable = CompositeDisposable()
    private val FIRST_PAGE = 0
    val tag = "SearchPostDataSource -> "

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Post>
    ) {
        compositeDisposable.add(
            service.getSearchPost(
                "application/json;charset=utf-8",
                keyword,
                FIRST_PAGE,
                params.requestedLoadSize
            ).map { it }
                .with()
                .subscribe({
                    if (it.code == 200) {
                        paginationStatus.postValue(
                            if (it.data.isEmpty()) PaginationStatus.Empty else PaginationStatus.NotEmpty
                        )
                        callback.onResult(it.data, null, FIRST_PAGE + 1)
                    } else if (it.code == 2001) paginationStatus.postValue(PaginationStatus.Empty)
                }) {
                    handleError(it)
                    Timber.tag("$tag getHomeFeed 통신 실패 error : ").d(it)
                }
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Post>) {
        compositeDisposable.add(
            service.getSearchPost(
                "application/json;charset=utf-8",
                keyword,
                params.key,
                params.requestedLoadSize
            ).map { it }
                .with()
                .subscribe({
                    if (it.code == 200) callback.onResult(it.data, params.key + 1)
                }) {
                    handleError(it)
                    Timber.tag("$tag getHomeFeed 통신 실패 error : ").d(it)
                }
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Post>) {}

    override fun invalidate() {
        super.invalidate()
        compositeDisposable.dispose()
    }

    private fun handleError(t: Throwable) {
        paginationStatus.postValue(PaginationStatus.Error)
    }
}