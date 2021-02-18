package com.soulmates.valley.ui.search.result.post

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.soulmates.valley.R
import com.soulmates.valley.base.BaseActivity
import com.soulmates.valley.data.model.post.Post
import com.soulmates.valley.databinding.ActivityPostListBinding
import com.soulmates.valley.ui.feed.FeedRvAdapter
import com.soulmates.valley.ui.feed.FeedViewHolder
import com.soulmates.valley.ui.post.PostViewModel
import com.soulmates.valley.ui.post.read.PostDetailActivity
import com.soulmates.valley.ui.post.write.code.CodeViewActivity
import com.soulmates.valley.ui.post.write.url.WebViewActivity
import com.soulmates.valley.ui.profile.ProfileActivity
import com.soulmates.valley.ui.search.SearchViewModel
import com.soulmates.valley.util.extension.*
import com.soulmates.valley.util.view.WrapContentLinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class PostListActivity : BaseActivity<ActivityPostListBinding, SearchViewModel>() {
    override val layoutResID: Int = R.layout.activity_post_list
    override val viewModel: SearchViewModel by viewModel()
    private val postViewModel: PostViewModel by viewModel()
    lateinit var requestManager: RequestManager
    private var postRvAdapter: FeedRvAdapter = FeedRvAdapter()
    private val SEARCH_POST_STATE = "searchPostState"
    private var likePosition = -1
    private var likeImageView: ImageView? = null
    private var likeTextView: TextView? = null
    private var fromView = -1 // 0은 게시글 검색 전체보기, 1은 태그 개별 게시글 보기
    var keyword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.let {
            keyword = it.getStringExtra("keyword")
            fromView = it.getIntExtra("fromView", -1)
        }
        viewModel.keyword = keyword
        requestManager = Glide.with(this)

        setText()
        setClick()
        setPostRv()

        viewModel.showProfilePost(savedInstanceState?.getString(SEARCH_POST_STATE) ?: "")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_POST_STATE, viewModel.currentProfilePost())
    }

    private fun setText() {
        viewDataBinding.actPostListLayoutHeader.layoutHeaderBackTvTitle.text = "$keyword 검색 결과"
    }

    private fun setClick() {
        viewDataBinding.run {
            actPostListLayoutHeader.layoutHeaderBackIvBack.setSafeOnClickListener {
                finish()
            }
        }
    }

    private val onFeedItemClickListener = object : FeedViewHolder.OnFeedItemClickListener {
        override fun onProfileClick(userId: Int) {
            startActivity(
                Intent(this@PostListActivity, ProfileActivity::class.java)
                    .putExtra("userId", userId)
            )
        }

        override fun onLinkClick(url: String?, title: String?) {
            startActivity(
                Intent(this@PostListActivity, WebViewActivity::class.java)
                    .putExtra("url", url)
                    .putExtra("title", title)
            )
        }

        override fun onCodeClick(langIdx: String, code: String) {
            startActivity(
                Intent(this@PostListActivity, CodeViewActivity::class.java)
                    .putExtra("langIdx", langIdx)
                    .putExtra("content", code)
                    .putExtra("fromView", 2)
            )
        }

        override fun onContentClick(postId: Int) {
            startActivity(
                Intent(
                    this@PostListActivity,
                    PostDetailActivity::class.java
                ).putExtra("postId", postId)
            )
        }

        override fun onLikeClick(
            postId: Int,
            isLiked: Boolean,
            position: Int,
            imageView: ImageView,
            textView: TextView
        ) {
            likePosition = position
            likeImageView = imageView
            likeTextView = textView
            if (isLiked) {
                postViewModel.deleteLike(postId)
            } else {
                postViewModel.postLike(postId)
            }
        }

        override fun onBookmarkClick(post: Post, imageView: ImageView) {
            viewModel.insertBookmarkPost(post)
            imageView.setImageDrawable(ContextCompat.getDrawable(this@PostListActivity, R.drawable.ic_save_on))
        }
    }

    private fun setPostRv() {
        postRvAdapter = FeedRvAdapter().apply {
            setFeedItemClickListener(onFeedItemClickListener)
            setRequestManager(requestManager)
            setHasStableIds(true)
        }
        viewDataBinding.actPostListRv.run {
            adapter = postRvAdapter
            (itemAnimator as SimpleItemAnimator).run {
                changeDuration = 0
                supportsChangeAnimations = false
            }
            layoutManager = WrapContentLinearLayoutManager()
        }
        observe()
    }

    private fun observe() {
        when (fromView) {
            0 -> {
                viewModel.searchPost.observe(this, Observer {
                    postRvAdapter.submitList(it)
                })
            }
            1 -> {
                viewModel.getSearchTagDetail(keyword).observe(this, Observer {
                    postRvAdapter.submitList(it)
                })
            }
        }

        postViewModel.postLikeStatus.observe(this, Observer {
            postRvAdapter.updateLike(likePosition)
            likeTextView?.likeTextActive(++postRvAdapter.data[likePosition].likeCount)
            likeImageView?.likeImageActive()
        })

        postViewModel.deleteLikeStatus.observe(this, Observer {
            postRvAdapter.updateLike(likePosition)
            likeTextView?.likeTextInactive(--postRvAdapter.data[likePosition].likeCount)
            likeImageView?.likeImageInactive()

        })
    }
}