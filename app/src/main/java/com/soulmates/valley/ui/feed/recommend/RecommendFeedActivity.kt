package com.soulmates.valley.ui.feed.recommend

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.soulmates.valley.R
import com.soulmates.valley.base.BaseActivity
import com.soulmates.valley.data.model.post.Post
import com.soulmates.valley.data.model.PaginationStatus
import com.soulmates.valley.databinding.ActivityRecommendFeedBinding
import com.soulmates.valley.ui.feed.FeedRvAdapter
import com.soulmates.valley.ui.feed.FeedViewHolder
import com.soulmates.valley.ui.feed.FeedViewModel
import com.soulmates.valley.ui.post.PostViewModel
import com.soulmates.valley.ui.post.read.PostDetailActivity
import com.soulmates.valley.ui.post.write.code.CodeViewActivity
import com.soulmates.valley.ui.post.write.url.WebViewActivity
import com.soulmates.valley.ui.profile.ProfileActivity
import com.soulmates.valley.util.extension.*
import com.soulmates.valley.util.view.WrapContentLinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel

class RecommendFeedActivity : BaseActivity<ActivityRecommendFeedBinding, FeedViewModel>() {
    override val layoutResID: Int = R.layout.activity_recommend_feed
    override val viewModel: FeedViewModel by viewModel()
    private val postViewModel: PostViewModel by viewModel()
    lateinit var requestManager: RequestManager
    private var recommendFeedRvAdapter: FeedRvAdapter = FeedRvAdapter()
    private var likePosition = -1
    private var likeImageView: ImageView? = null
    private var likeTextView: TextView? = null
    var keyword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestManager = Glide.with(this)
        setRecommendFeedRv()
        observe()
        setRefresh()
        setHeader()
    }

    private fun setHeader() {
        viewDataBinding.actRecommendFeedHeader.layoutHeaderBackTvTitle.text = "추천 피드"
        viewDataBinding.actRecommendFeedHeader.layoutHeaderBackIvBack.setSafeOnClickListener { finish() }
    }

    private val onFeedItemClickListener = object : FeedViewHolder.OnFeedItemClickListener {
        override fun onProfileClick(userId: Int) {
            startActivity(
                Intent(this@RecommendFeedActivity, ProfileActivity::class.java)
                    .putExtra("userId", userId)
            )
        }

        override fun onLinkClick(url: String?, title: String?) {
            startActivity(
                Intent(this@RecommendFeedActivity, WebViewActivity::class.java)
                    .putExtra("url", url)
                    .putExtra("title", title)
            )
        }

        override fun onCodeClick(langIdx: String, code: String) {
            startActivity(
                Intent(this@RecommendFeedActivity, CodeViewActivity::class.java)
                    .putExtra("langIdx", langIdx)
                    .putExtra("content", code)
                    .putExtra("fromView", 2)
            )
        }

        override fun onContentClick(postId: Int) {
            startActivity(
                Intent(
                    this@RecommendFeedActivity,
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
            if (isLiked) postViewModel.deleteLike(postId)
            else postViewModel.postLike(postId)
        }

        override fun onBookmarkClick(post: Post, imageView: ImageView) {
            viewModel.insertBookmarkPost(post)
            imageView.setImageDrawable(
                ContextCompat.getDrawable(
                    this@RecommendFeedActivity,
                    R.drawable.ic_save_on
                )
            )
        }
    }

    private fun setRecommendFeedRv() {
        viewDataBinding.actRecommendFeedRv.run {
            adapter = recommendFeedRvAdapter.apply {
                setFeedItemClickListener(onFeedItemClickListener)
                setRequestManager(requestManager)
                setHasStableIds(true)
            }
            layoutManager = WrapContentLinearLayoutManager()
        }
    }

    private fun setRefresh() {
        viewDataBinding.actRecommendFeedSrl.apply {
            setOnRefreshListener {
                viewModel.refreshRecommendFeed()
                this@apply.isRefreshing = false
            }
            setColorSchemeColors(ContextCompat.getColor(context, R.color.main_green))
        }
    }

    private fun observe() {
        viewModel.run {
            getRecommendFeed().observe(this@RecommendFeedActivity, Observer {
                recommendFeedRvAdapter.submitList(it)
            })

            recoPaginationStatus.observe(this@RecommendFeedActivity, Observer {
                when(it){
                    PaginationStatus.Empty -> {
                        viewDataBinding.actRecommentFeedLlEmpty.visibility = View.VISIBLE
                        viewDataBinding.actRecommendFeedRv.visibility = View.GONE
                    }
                    else -> viewDataBinding.actRecommendFeedRv.visibility = View.VISIBLE
                }
            })
        }

        postViewModel.run {
            postLikeStatus.observe(this@RecommendFeedActivity, Observer {
                recommendFeedRvAdapter.updateLike(likePosition)
                likeTextView?.likeTextActive(++recommendFeedRvAdapter.data[likePosition].likeCount)
                likeImageView?.likeImageActive()
            })

            deleteLikeStatus.observe(this@RecommendFeedActivity, Observer {
                recommendFeedRvAdapter.updateLike(likePosition)
                likeTextView?.likeTextInactive(--recommendFeedRvAdapter.data[likePosition].likeCount)
                likeImageView?.likeImageInactive()
            })
        }

    }
}