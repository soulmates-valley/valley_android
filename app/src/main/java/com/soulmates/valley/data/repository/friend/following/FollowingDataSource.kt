package com.soulmates.valley.data.repository.friend.following

import androidx.paging.PageKeyedDataSource
import com.soulmates.valley.ValleyApplication
import com.soulmates.valley.data.model.friend.Follow
import com.soulmates.valley.data.remote.ValleyService
import com.soulmates.valley.util.etc.NetworkStatus
import com.soulmates.valley.util.extension.with
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber


class FollowingDataSource(private val service: ValleyService) : PageKeyedDataSource<Int, Follow>() {
    val tag = "FollowingDataSource -> "
    val FIRST_PAGE = 0

    val userId = ValleyApplication.prefManager.userId.toInt()
    private val compositeDisposable = CompositeDisposable()
    var loadState: PublishSubject<NetworkStatus> = PublishSubject.create()

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Follow>
    ) {
        compositeDisposable.add(
            service.getFollowingList(userId, FIRST_PAGE, params.requestedLoadSize).map { it }
                .with()
                .subscribe({
                    if (it.code == 200) {
                        callback.onResult(it.data, null, FIRST_PAGE+1)
                    }
                }) {
                    Timber.tag("$tag getFollowingList 통신 실패 error : ").d(it)
                }
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Follow>) {
        compositeDisposable.add(
            service.getFollowingList(userId, params.key, params.requestedLoadSize).map { it }
                .with()
                .subscribe({
                    if (it.code == 200 && !it.data.isEmpty()) {
                        callback.onResult(it.data, params.key + 1)
                    }
                }) {
                    Timber.tag("$tag getFollowingList 통신 실패 error : ").d(it)
                }
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Follow>) {}

    override fun invalidate() {
        super.invalidate()
        compositeDisposable.dispose()
    }
}