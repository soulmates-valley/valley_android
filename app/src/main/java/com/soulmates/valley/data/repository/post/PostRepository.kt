package com.soulmates.valley.data.repository.post

import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.google.gson.JsonObject
import com.soulmates.valley.data.model.DefaultResponse
import com.soulmates.valley.data.model.comment.Comment
import com.soulmates.valley.data.repository.post.comment.CommentDataSourceFactory
import com.soulmates.valley.data.model.comment.response.CommentResponse
import com.soulmates.valley.data.model.post.response.PostDetailResponse
import com.soulmates.valley.data.remote.ValleyService
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PostRepository(private val service: ValleyService, private val commentDataSourceFactory: CommentDataSourceFactory) {
    fun postPosting(
        map: HashMap<String, RequestBody?>,
        hashTag: ArrayList<String>?,
        images: List<MultipartBody.Part>?
    ): Single<DefaultResponse> =
        service.postPosting(map, hashTag, images).map { it }

    fun getPostDetail(postId: Int): Single<PostDetailResponse> =
        service.getPostDetail("application/json;charset=utf-8", postId).map { it }

    fun postComment(jsonObject: JsonObject): Single<CommentResponse> =
        service.postComment("application/json;charset=utf-8", jsonObject).map { it }

    private val config = PagedList.Config.Builder()
        .setInitialLoadSizeHint(30)
        .setPageSize(30)
        .setPrefetchDistance(30)
        .setEnablePlaceholders(false)
        .build()

    val commentFactory = commentDataSourceFactory
    fun getCommentList(postId: Int): Observable<PagedList<Comment>> {
        commentFactory.run {
            this.postId = postId
            create()
        }

        return RxPagedListBuilder(commentFactory, config)
            .setInitialLoadKey(0)
            .setFetchScheduler(Schedulers.io())
            .setNotifyScheduler(AndroidSchedulers.mainThread())
            .buildObservable()
    }

    fun postLike(postId: Int): Single<DefaultResponse> =
        service.postLike(postId).map { it }

    fun deleteLike(postId: Int): Single<DefaultResponse> =
        service.deleteLike(postId).map { it }
}