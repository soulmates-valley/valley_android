package com.soulmates.valley.data.repository.post.comment

import androidx.paging.PageKeyedDataSource
import com.soulmates.valley.data.model.comment.Comment
import com.soulmates.valley.data.remote.ValleyService
import com.soulmates.valley.util.extension.with
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class CommentDataSource (private val service: ValleyService, private val postId: Int) : PageKeyedDataSource<Int, Comment>() {
    private val compositeDisposable = CompositeDisposable()
    private val FIRST_PAGE = 0
    val tag = "CommentDataSource -> "

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Comment>
    ) {
        compositeDisposable.add(
            service.getCommentList(postId, FIRST_PAGE, params.requestedLoadSize).map { it }
                .with()
                .subscribe({
                    if (it.code == 200) {
                        callback.onResult(it.data, null, FIRST_PAGE+1)
                    }
                }) {
                    Timber.tag("$tag getCommentList loadInitial 통신 실패 error : ").d(it)
                }
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Comment>) {
        compositeDisposable.add(
            service.getCommentList(postId, params.key, params.requestedLoadSize).map { it }
                .with()
                .subscribe({
                    if (it.code == 200 && it.data.isNotEmpty()) {
                        callback.onResult(it.data, params.key + 1)
                    }
                }) {
                    Timber.tag("$tag getCommentList 통신 실패 error : ").d(it)
                }
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Comment>) {}

    override fun invalidate() {
        super.invalidate()
        compositeDisposable.dispose()
    }
}