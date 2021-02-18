package com.soulmates.valley.data.repository.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.soulmates.valley.data.model.post.Post
import com.soulmates.valley.data.model.search.response.PopularKeywordReponse
import com.soulmates.valley.data.model.search.response.SearchPostResponse
import com.soulmates.valley.data.model.search.response.SearchTagResponse
import com.soulmates.valley.data.model.search.response.SearchUserResponse
import com.soulmates.valley.data.repository.search.post.SearchPostDataSourceFactory
import com.soulmates.valley.data.repository.search.tag.SearchTagDetailDataSourceFactory
import com.soulmates.valley.data.remote.ValleyService
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors

class SearchRepository(private val service: ValleyService, private val searchPostDataSourceFactory: SearchPostDataSourceFactory, private val searchTagDetailDataSourceFactory: SearchTagDetailDataSourceFactory) {
    fun getSearchUser(keyword: String): Single<SearchUserResponse> = service.getSearchUser("application/json;charset=utf-8", keyword).map { it }
    fun getSearchTag(keyword: String): Single<SearchTagResponse> = service.getSearchTag("application/json;charset=utf-8", keyword).map { it }

    fun getPopularKeyword(): Single<PopularKeywordReponse> = service.getPopularKeyword().map { it }

    private val executors = Executors.newFixedThreadPool(5)
    private val config = PagedList.Config.Builder()
        .setInitialLoadSizeHint(20)
        .setPageSize(20)
        .setPrefetchDistance(10)
        .setEnablePlaceholders(false)
        .build()

    val searchPostFactory = searchPostDataSourceFactory
    val searchTagDetailFactory = searchTagDetailDataSourceFactory

    fun getSearchPost(keyword: String): LiveData<PagedList<Post>> {
        searchPostFactory.run {
            this.keyword = keyword
            create()
        }

        return LivePagedListBuilder(searchPostFactory, config)
            .setInitialLoadKey(0)
            .setFetchExecutor(executors)
            .build()
    }

    val paginationStatus = MutableLiveData<Boolean>(true)
    fun getSearchTagDetail(keyword: String): Observable<PagedList<Post>> {
        searchTagDetailFactory.run {
            this.keyword = keyword
            create()
        }

        return RxPagedListBuilder(searchTagDetailFactory, config)
            .setInitialLoadKey(0)
            .setFetchScheduler(Schedulers.io())
            .setNotifyScheduler(AndroidSchedulers.mainThread())
            .buildObservable()
    }
}