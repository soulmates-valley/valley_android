package com.soulmates.valley.ui.profile

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.soulmates.valley.ValleyApplication
import com.soulmates.valley.base.BaseViewModel
import com.soulmates.valley.data.local.room.dao.BookmarkPostDao
import com.soulmates.valley.data.local.room.dao.UserInfoDao
import com.soulmates.valley.data.local.room.entity.BookmarkPost
import com.soulmates.valley.data.local.room.entity.UserProfile
import com.soulmates.valley.data.model.friend.FollowCount
import com.soulmates.valley.data.model.post.Post
import com.soulmates.valley.data.model.profile.Profile
import com.soulmates.valley.data.repository.friend.FriendRepository
import com.soulmates.valley.data.repository.profile.ProfileRepository
import com.soulmates.valley.ui.initial.InitialActivity
import com.soulmates.valley.util.extension.with
import com.soulmates.valley.util.extension.ioThread
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import kotlin.reflect.KClass

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val friendRepository: FriendRepository,
    private val userInfoDao: UserInfoDao, private val bookmarkPostDao: BookmarkPostDao
) : BaseViewModel() {

    private val tag = "ProfileViewModel -> "
    var userId: Int = 0
    val paginationStatus = profileRepository.profilePostDataSourceFactory.getPaginationStatus()

    private val profilePostState = MutableLiveData<String>()
    var profilePostLiveData: LiveData<LiveData<PagedList<Post>>> = Transformations.map(profilePostState) { profileRepository.getProfilePost(userId) }
    val profilePost: LiveData<PagedList<Post>> =
        Transformations.switchMap(profilePostLiveData) { it }

    fun showProfilePost(state: String): Boolean {
        if (profilePostState.value == state) return false

        profilePostState.value = state
        return true
    }

    fun currentProfilePost(): String? = profilePostState.value

    fun refreshProfilePost() {
        profileRepository.profilePostDataSourceFactory.getLiveDataSource().value?.invalidate()
    }

    private val _profileInfo = MutableLiveData<Profile>()
    val profileInfo: LiveData<Profile> get() = _profileInfo

    fun getProfileInfo(userId: Int) {
        addDisposable(
            disposable = profileRepository.getProfileInfo(userId)
                .with()
                .subscribe({
                    when (it.code) {
                        200 -> _profileInfo.postValue(it.data)
                    }
                }) {
                    Timber.tag("$tag getProfileInfo 통신 실패 error : ").d(it.message!!)
                    Toast.makeText(context, "서버 점검 중입니다.", Toast.LENGTH_SHORT).show()
                })
    }

    private val _profileInfoFromRoom = MutableLiveData<UserProfile>()
    val profileProfileFromRoom: LiveData<UserProfile> get() = _profileInfoFromRoom

    fun getProfileFromRoom() {
        addDisposable(userInfoDao.getUserInfo()
            .with()
            .subscribe({
                _profileInfoFromRoom.postValue(it)
            }) {
                Timber.d("Road Profile From Room Fail")
            })
    }

    private val _followCount = MutableLiveData<FollowCount>()
    val followCount: LiveData<FollowCount> get() = _followCount

    fun getFollowCount() {
        addDisposable(
            disposable = friendRepository.getFollowCount(ValleyApplication.prefManager.userId.toInt())
                .with()
                .subscribe({
                    _followCount.postValue(it.data)
                }) {
                    Timber.tag("$tag getFollowCount 통신 실패 error : ").d(it.message!!)
                    Toast.makeText(context, "서버 점검 중입니다.", Toast.LENGTH_SHORT).show()
                })
    }

    private val _logoutStatus = MutableLiveData<KClass<*>>()
    val logoutStatus: LiveData<KClass<*>> get() = _logoutStatus

    fun logout() {
        addDisposable(disposable = profileRepository.logout()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                ValleyApplication.prefManager.clear()
                deleteUserInfo()
                _logoutStatus.postValue(InitialActivity::class)
            }) {
                Timber.tag("$tag logout 통신 실패 error : ").d(it.message!!)
                Toast.makeText(context, "서버 점검 중입니다.", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun deleteUserInfo() =
        ioThread { userInfoDao.deleteAll() }

    fun insertBookmarkPost(post: Post) =
        ioThread {
            bookmarkPostDao.insert(BookmarkPost.postToBookmark(post))
        }

    fun deleteBookmarkPost(postId: Int) =
        ioThread {
            bookmarkPostDao.deletePost(postId)
        }

    val bookmarkList: LiveData<PagedList<BookmarkPost>> =
        LivePagedListBuilder(bookmarkPostDao.getPostList(), 10).build()
}