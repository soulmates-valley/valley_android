package com.soulmates.valley.data.repository.search.tag

import androidx.paging.PageKeyedDataSource
import com.soulmates.valley.data.model.post.Post
import com.soulmates.valley.data.remote.ValleyService
import com.soulmates.valley.util.extension.with
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class SearchTagDetailDataSource(
    private val service: ValleyService,
    private val keyword: String
) : PageKeyedDataSource<Int, Post>() {
    private val compositeDisposable = CompositeDisposable()
    val FIRST_PAGE = 0
    val tag = "SearchPostDataSource -> "

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Post>
    ) {
        compositeDisposable.add(
            service.getSearchTagDetail(
                "application/json;charset=utf-8",
                keyword,
                FIRST_PAGE,
                params.requestedLoadSize
            ).map { it }
                .with()
                .subscribe({
                    if (it.code == 200) {
                        callback.onResult(it.data, null, FIRST_PAGE + 1)
                    }
                }) {
                    Timber.tag("$tag getHomeFeed 통신 실패 error : ").d(it)
                }
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Post>) {
        compositeDisposable.add(
            service.getSearchTagDetail(
                "application/json;charset=utf-8",
                keyword,
                params.key,
                params.requestedLoadSize
            ).map { it }
                .with()
                .subscribe({
                    if (it.code == 200 && it.data.isNotEmpty()) {
                        callback.onResult(it.data, params.key + 1)
                    }
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