package com.soulmates.valley.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.PagedList
import com.soulmates.valley.base.BaseViewModel
import com.soulmates.valley.data.local.room.dao.BookmarkPostDao
import com.soulmates.valley.data.local.room.dao.SearchKeywordDao
import com.soulmates.valley.data.local.room.entity.BookmarkPost
import com.soulmates.valley.data.local.room.entity.SearchKeyword
import com.soulmates.valley.data.model.post.Post
import com.soulmates.valley.data.model.search.SearchTag
import com.soulmates.valley.data.model.search.SearchUser
import com.soulmates.valley.data.repository.search.SearchRepository
import com.soulmates.valley.util.extension.with
import com.soulmates.valley.util.extension.ioThread
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class SearchViewModel(
    private val searchRepository: SearchRepository,
    private val searchKeywordDao: SearchKeywordDao, private val bookmarkPostDao: BookmarkPostDao
) : BaseViewModel() {

    private val tag = "SearchViewModel -> "

    val localKeywords: LiveData<List<SearchKeyword>> get() = searchKeywordDao.getSearchKeyword()
    fun insertSearchKeyword(data: String) =
        ioThread {
            searchKeywordDao.insert(
                SearchKeyword(keywordName = data)
            )
        }

    fun deleteAll() =
        ioThread { searchKeywordDao.deleteAll() }

    private val _popularKeyword = MutableLiveData<List<String>>()
    val popularKeyword: LiveData<List<String>> get() = _popularKeyword

    fun getPopularKeyword() {
        addDisposable(disposable = searchRepository.getPopularKeyword()
            .with()
            .subscribe({
                if (it.code == 200) {
                    _popularKeyword.postValue(it.data)
                }
            }) {
                Timber.tag("$tag getPopularKeyword 통신 실패 error : ").d(it)
            })
    }

    private val _userList = MutableLiveData<List<SearchUser>>()
    val userList: LiveData<List<SearchUser>> get() = _userList

    fun getSearchUser(keyword: String) {
        addDisposable(
            disposable = searchRepository.getSearchUser(keyword)
                .with()
                .subscribe({
                    Timber.d("$tag getSearchUser 통신 성공")
                    when (it.code) {
                        200 -> _userList.postValue(it.data)
                        2001 -> _userList.postValue(listOf())
                    }
                }) {
                    Timber.tag("$tag getSearchUser 통신 실패 error : ").d(it)
                })
    }

    var keyword: String = ""
    val paginationStatus = searchRepository.searchPostFactory.getPaginationStatus()
    private val searchPostState = MutableLiveData<String>()
    var searchPostLiveData: LiveData<LiveData<PagedList<Post>>> =
        Transformations.map(searchPostState) { searchRepository.getSearchPost(keyword) }
    val searchPost: LiveData<PagedList<Post>> =
        Transformations.switchMap(searchPostLiveData) { it }

    fun showProfilePost(state: String): Boolean {
        if (searchPostState.value == state) return false

        searchPostState.value = state
        return true
    }

    fun currentProfilePost(): String? = searchPostState.value

    private val _tagList = MutableLiveData<List<SearchTag>>()
    val tagList: LiveData<List<SearchTag>> get() = _tagList

    fun getSearchTag(keyword: String) {
        addDisposable(
            disposable = searchRepository.getSearchTag(keyword)
                .with()
                .subscribe({
                    Timber.d("$tag getSearchUser 통신 성공")
                    when (it.code) {
                        200 -> _tagList.postValue(it.data)
                        2001 -> _tagList.postValue(listOf())
                    }
                }) {
                    Timber.tag("$tag getSearchUser 통신 실패 error : ").d(it)
                })
    }

    fun getSearchTagDetail(keyword: String): LiveData<PagedList<Post>>{
        val result =  searchRepository.getSearchTagDetail(keyword)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .toFlowable(BackpressureStrategy.BUFFER)

        return LiveDataReactiveStreams.fromPublisher(result)
    }

    fun insertBookmarkPost(post: Post) =
        ioThread {
            bookmarkPostDao.insert(BookmarkPost.postToBookmark(post))
        }
}