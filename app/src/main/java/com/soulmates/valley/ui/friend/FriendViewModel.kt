package com.soulmates.valley.ui.friend

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.soulmates.valley.ValleyApplication
import com.soulmates.valley.base.BaseViewModel
import com.soulmates.valley.data.model.friend.FollowCount
import com.soulmates.valley.data.model.friend.Follow
import com.soulmates.valley.data.model.friend.RecommendFollow
import com.soulmates.valley.data.repository.friend.FriendRepository
import com.soulmates.valley.util.extension.with
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber


class FriendViewModel(private val friendRepository: FriendRepository) : BaseViewModel() {
    private val tag = "FriendViewModel ->"

    private val _followCount = MutableLiveData<FollowCount>()
    val followCount: LiveData<FollowCount> get() = _followCount

    fun getFollowCount() {
        addDisposable(
            disposable = friendRepository.getFollowCount(ValleyApplication.prefManager.userId.toInt())
                .with()
                .subscribe({
                    Timber.tag("$tag getFollowCount 통신 성공 res : ").d(it.data.toString())
                    _followCount.postValue(it.data)
                }) {
                    Timber.tag("$tag getFollowCount 통신 실패 error : ").d(it)
                })
    }

    private val _postFollowStatus = MutableLiveData<Int>()
    val postFollowStatus: LiveData<Int> get() = _postFollowStatus

    fun postFollow(userId: Int) {
        addDisposable(
            disposable = friendRepository.postFollow(userId)
                .with()
                .subscribe({
                    when (it.code) {
                        200 -> _postFollowStatus.postValue(it.code)
                        2100 -> Toast.makeText(context, "찾을 수 없는 사용자입니다.", Toast.LENGTH_SHORT)
                            .show()
                        2101 -> Toast.makeText(context, "이미 팔로우 상태입니다.", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Timber.tag("$tag postFollow 통신 실패 error : ").d(it)
                })
    }

    private val _deleteFollowStatus = MutableLiveData<Int>()
    val deleteFollowStatus: LiveData<Int> get() = _deleteFollowStatus

    fun deleteFollow(userId: Int) {
        addDisposable(
            disposable = friendRepository.deleteFollow(userId)
                .with()
                .subscribe({
                    when (it.code) {
                        200 -> _deleteFollowStatus.postValue(it.code)
                        2100 -> Toast.makeText(context, "찾을 수 없는 사용자입니다.", Toast.LENGTH_SHORT)
                            .show()
                        2101 -> Toast.makeText(context, "팔로우 상태가 아닙니다.", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Timber.tag("$tag deleteFollow 통신 실패 error : ").d(it)
                })
    }

    fun getFollowingList(): LiveData<PagedList<Follow>> {
        friendRepository.followingFactory.create()
        val result = friendRepository.getFollowingList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .toFlowable(BackpressureStrategy.BUFFER)

        return LiveDataReactiveStreams.fromPublisher(result)
    }

    fun refreshFollowingList() {
        friendRepository.followingFactory.getLiveDataSource().value?.invalidate()
    }

    fun getFollowerList(): LiveData<PagedList<Follow>> {
        friendRepository.followerFactory.create()
        val result = friendRepository.getFollowerList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .toFlowable(BackpressureStrategy.BUFFER)

        return LiveDataReactiveStreams.fromPublisher(result)
    }

    fun refreshFollowerList() {
        friendRepository.followerFactory.getLiveDataSource().value?.invalidate()
    }

    private val _knowFollowList = MutableLiveData<List<RecommendFollow>>()
    val knowFollowList: LiveData<List<RecommendFollow>> get() = _knowFollowList

    fun getKnowFollowList() {
        addDisposable(
            disposable = friendRepository.getKnowFollowList(ValleyApplication.prefManager.userId.toInt())
                .with()
                .subscribe({
                    Timber.d("$tag getFollowCount 통신 성공")
                    when (it.code) {
                        200 -> _knowFollowList.postValue(it.data)
                        1200 -> Toast.makeText(context, "알 수도 없는 친구가 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }) {
                    Timber.tag("$tag getFollowCount 통신 실패 error : ").d(it)
                })
    }

    private val _likeFollowList = MutableLiveData<List<RecommendFollow>>()
    val likeFollowList: LiveData<List<RecommendFollow>> get() = _likeFollowList

    fun getLikeFollowList() {
        addDisposable(
            disposable = friendRepository.getLikeFollowList(ValleyApplication.prefManager.userId.toInt())
                .with()
                .subscribe({
                    Timber.d("$tag getLikeFollowList 통신 성공")
                    when (it.code) {
                        200 -> _likeFollowList.postValue(it.data)
                        1200 -> Toast.makeText(context, "좋아할만한 친구가 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }) {
                    Timber.tag("$tag getLikeFollowList 통신 실패 error : ").d(it)
                })
    }
}