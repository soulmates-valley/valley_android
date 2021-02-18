package com.soulmates.valley.data.repository.profile.profilePost

import androidx.lifecycle.MutableLiveData
import androidx.paging.ItemKeyedDataSource
import com.soulmates.valley.data.model.PaginationStatus
import com.soulmates.valley.data.model.post.Post
import com.soulmates.valley.data.remote.ValleyService
import com.soulmates.valley.util.extension.with
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class ProfilePostDataSource(
    private val service: ValleyService, private val userId: Int,
    private val paginationStatus: MutableLiveData<PaginationStatus>
) :
    ItemKeyedDataSource<Int, Post>() {
    val tag = "ProfileDataSource -> "

    private val compositeDisposable = CompositeDisposable()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Post>) {
        compositeDisposable.add(
            service.getProfilePost(userId, params.requestedLoadSize, -1).map { it }
                .with()
                .subscribe({
                    if (it.code == 200) {
                        paginationStatus.postValue(
                            if (it.data.isEmpty()) PaginationStatus.Empty else PaginationStatus.NotEmpty
                        )
                        callback.onResult(it.data)
                    }
                }) {
                    handleError(it)
                    Timber.tag("$tag getProfilePost loadInitial 통신 실패 error : ").d(it)
                }
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Post>) {
        compositeDisposable.add(
            service.getProfilePost(userId, params.requestedLoadSize, params.key).map { it }
                .with()
                .subscribe({
                    if (it.code == 200) {
                        callback.onResult(it.data)
                    }
                }) {
                    handleError(it)
                    Timber.tag("$tag getProfilePost loadAfter 통신 실패 error : ").d(it)
                }
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Post>) {}

    override fun getKey(item: Post): Int = item.postId

    override fun invalidate() {
        super.invalidate()
        compositeDisposable.dispose()
    }

    private fun handleError(t: Throwable) {
        paginationStatus.postValue(PaginationStatus.Error)
    }
}
