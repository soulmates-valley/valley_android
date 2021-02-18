package com.soulmates.valley.ui.post

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.google.gson.JsonObject
import com.soulmates.valley.base.BaseViewModel
import com.soulmates.valley.data.model.comment.Comment
import com.soulmates.valley.data.model.post.Post
import com.soulmates.valley.data.repository.post.PostRepository
import com.soulmates.valley.util.extension.with
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber

class PostViewModel(private val postRepository: PostRepository) : BaseViewModel() {
    private val tag = "PostViewModel ->"

    private val _isProgress = MutableLiveData<Boolean>()
    val isProgress: LiveData<Boolean> get() = _isProgress

    private var _postStatus = MutableLiveData<Int>()
    val postStatus: LiveData<Int> get() = _postStatus

    fun postPosting(
        map: HashMap<String, RequestBody?>,
        hashTag: ArrayList<String>,
        images: ArrayList<MultipartBody.Part>
    ) {
        addDisposable(
            disposable = postRepository.postPosting(map, hashTag, images)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { _isProgress.value = true }
                .doOnSuccess { _isProgress.value = false }
                .doOnError { _isProgress.value = false }
                .subscribe({
                    _postStatus.postValue(it.code)
                }) {
                    Timber.tag("$tag postPosting 통신 실패 error : ").d(it.message!!)
                })
    }

    private var _postDetail = MutableLiveData<Post>()
    val postDetail: LiveData<Post> get() = _postDetail

    fun getPostDetail(postId: Int) {
        addDisposable(
            disposable = postRepository.getPostDetail(postId)
                .with()
                .subscribe({
                    Timber.d("$tag getPostDetail 통신 성공")
                    when (it.code) {
                        200 -> _postDetail.postValue(it.data)
                        1200 -> Toast.makeText(context, "해당 게시글이 존재하지 않습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }) {
                    Timber.tag("$tag getPostDetail 통신 실패 error : ").d(it.message!!)
                    Toast.makeText(context, "서버 점검 중입니다.", Toast.LENGTH_SHORT).show()
                })
    }

    fun getCommentList(postId: Int): LiveData<PagedList<Comment>> {
        val result = postRepository.getCommentList(postId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .toFlowable(BackpressureStrategy.BUFFER)

        return LiveDataReactiveStreams.fromPublisher(result)
    }

    fun refreshCommentList() {
        postRepository.commentFactory.getLiveDataSource().value?.invalidate()
    }

    private val _commentData: MutableLiveData<Comment> = MutableLiveData()
    val commentData: LiveData<Comment> get() = _commentData

    fun postComment(jsonObject: JsonObject) {
        addDisposable(
            disposable = postRepository.postComment(jsonObject)
                .with()
                .subscribe({
                    Timber.d("$tag postComment 통신 성공")
                    if (it.code == 200)
                        when (it.code) {
                            200 -> _commentData.postValue(it.data)
                            1200 -> Toast.makeText(
                                context,
                                "해당 게시글이 존재하지 않습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }) {
                    Timber.tag("$tag postComment 통신 실패 error : ").d(it)
                    Toast.makeText(context, "서버 점검 중입니다.", Toast.LENGTH_SHORT).show()
                })
    }

    private val _postLikeStatus: MutableLiveData<Int> = MutableLiveData()
    val postLikeStatus: LiveData<Int> get() = _postLikeStatus

    fun postLike(postId: Int) {
        addDisposable(
            disposable = postRepository.postLike(postId)
                .with()
                .subscribe({
                    Timber.d("$tag postLike 통신 성공")
                    when (it.code) {
                        200 -> _postLikeStatus.postValue(it.code)
                        1200 -> Toast.makeText(context, it.data, Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Timber.tag("$tag postLike 통신 실패 error : ").d(it)
                    Toast.makeText(context, "서버 점검 중입니다.", Toast.LENGTH_SHORT).show()
                })
    }

    private val _deleteLikeStatus: MutableLiveData<Int> = MutableLiveData()
    val deleteLikeStatus: LiveData<Int> get() = _deleteLikeStatus

    fun deleteLike(postId: Int) {
        addDisposable(
            disposable = postRepository.deleteLike(postId)
                .with()
                .subscribe({
                    Timber.d("$tag deleteLike 통신 성공")
                    when (it.code) {
                        200 -> _deleteLikeStatus.postValue(it.code)
                        1200 -> Toast.makeText(context, it.data, Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Timber.tag("$tag deleteLike 통신 실패 error : ").d(it)
                    Toast.makeText(context, "서버 점검 중입니다.", Toast.LENGTH_SHORT).show()
                })
    }
}