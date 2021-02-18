package com.soulmates.valley.ui.feed.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.soulmates.valley.R
import com.soulmates.valley.base.BaseFragment
import com.soulmates.valley.data.model.post.Post
import com.soulmates.valley.data.model.PaginationStatus
import com.soulmates.valley.databinding.FragmentHomeBinding
import com.soulmates.valley.ui.tab.MainActivity
import com.soulmates.valley.ui.feed.FeedRvAdapter
import com.soulmates.valley.ui.feed.FeedViewHolder
import com.soulmates.valley.ui.feed.FeedViewModel
import com.soulmates.valley.ui.feed.recommend.RecommendFeedActivity
import com.soulmates.valley.ui.noti.NotiHistoryActivity
import com.soulmates.valley.ui.post.PostViewModel
import com.soulmates.valley.ui.post.read.PostDetailActivity
import com.soulmates.valley.ui.post.write.PostActivity
import com.soulmates.valley.ui.post.write.code.CodeViewActivity
import com.soulmates.valley.ui.post.write.url.WebViewActivity
import com.soulmates.valley.ui.profile.ProfileActivity
import com.soulmates.valley.util.extension.*
import com.soulmates.valley.util.view.WrapContentLinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class HomeFragment : BaseFragment<FragmentHomeBinding, FeedViewModel>() {
    override val layoutResID: Int = R.layout.fragment_home
    override val viewModel: FeedViewModel by viewModel()
    private val postViewModel: PostViewModel by viewModel()
    private lateinit var requestManager: RequestManager
    private val homeFeedRvAdapter: FeedRvAdapter = FeedRvAdapter()
    private val HOME_FEED_STATE = "homeFeedState"
    private var likePosition = 0
    private var likeImageView: ImageView? = null
    private var likeTextView: TextView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.vm = viewModel

        if (this.isRemoving) return
        requestManager = Glide.with(this)
        viewDataBinding.fragHomeCivProfile.glide(MainActivity.MainObject.profileImg, requestManager)

        setClick()
        setRv()
        observe()
        setRefresh()
        Timber.tag("ONCREATE@@@ before").d(viewModel.showHomeFeed(savedInstanceState?.getString(HOME_FEED_STATE) ?: "").toString())
        viewModel.showHomeFeed(savedInstanceState?.getString(HOME_FEED_STATE) ?: "")

        Timber.tag("ONCREATE@@@ after").d(viewModel.showHomeFeed(savedInstanceState?.getString(HOME_FEED_STATE) ?: "").toString())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(HOME_FEED_STATE, viewModel.currentHomeFeed())
    }


    private fun setRefresh() {
        viewDataBinding.fragHomeSrl.apply {
            setOnRefreshListener {
                viewModel.refreshHomeFeed()
                this@apply.isRefreshing = false
            }
            setColorSchemeColors(ContextCompat.getColor(context, R.color.main_green))
        }
    }

    private fun setClick() {
        viewDataBinding.fragHomeTvPost.setSafeOnClickListener {
            val intent = Intent(activity, PostActivity::class.java)
            startActivity(intent)
        }
        viewDataBinding.fragHomeIvRecommend.setSafeOnClickListener {
            startActivity(Intent(requireContext(), RecommendFeedActivity::class.java))
        }
        viewDataBinding.fragHomeIvAlarm.setSafeOnClickListener {
            startActivity(Intent(requireContext(), NotiHistoryActivity::class.java))
        }
    }

    private val onFeedItemClickListener = object :
        FeedViewHolder.OnFeedItemClickListener {
        override fun onProfileClick(userId: Int) {
            startActivity(
                Intent(requireActivity(), ProfileActivity::class.java)
                    .putExtra("userId", userId)
            )
        }

        override fun onLinkClick(url: String?, title: String?) {
            startActivity(
                Intent(requireActivity(), WebViewActivity::class.java)
                    .putExtra("url", url)
                    .putExtra("title", title)
            )
        }

        override fun onCodeClick(langIdx: String, code: String) {
            startActivity(
                Intent(requireActivity(), CodeViewActivity::class.java)
                    .putExtra("langIdx", langIdx)
                    .putExtra("content", code)
                    .putExtra("fromView", 2)
            )
        }

        override fun onContentClick(postId: Int) {
            startActivity(
                Intent(
                    requireContext(),
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
                    requireContext(),
                    R.drawable.ic_save_on
                )
            )
        }
    }

    private fun setRv() {
        viewDataBinding.fragHomeRv.run {
            adapter = homeFeedRvAdapter.apply {
                setFeedItemClickListener(onFeedItemClickListener)
                setRequestManager(requestManager)
                setHasStableIds(true)
            }
            (itemAnimator as SimpleItemAnimator).run {
                changeDuration = 0
                supportsChangeAnimations = false
            }
            layoutManager = WrapContentLinearLayoutManager()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observe() {
        viewModel.homeFeed.observe(requireActivity(), Observer { homeFeedRvAdapter.submitList(it) })
        viewModel.paginationStatus.observe(requireActivity(), Observer {
            when (it) {
                PaginationStatus.Empty -> {
                    viewDataBinding.fragHomeListLlEmpty.visibility = View.VISIBLE
                    viewDataBinding.fragHomeRv.visibility = View.GONE
                }
                else -> viewDataBinding.fragHomeRv.visibility = View.VISIBLE
            }
        })

        postViewModel.run {
            postLikeStatus.observe(requireActivity(), Observer {
                homeFeedRvAdapter.updateLike(likePosition)
                likeTextView?.likeTextActive(++homeFeedRvAdapter.data[likePosition].likeCount)
                likeImageView?.likeImageActive()
            })

            deleteLikeStatus.observe(requireActivity(), Observer {
                homeFeedRvAdapter.updateLike(likePosition)
                likeTextView?.likeTextInactive(--homeFeedRvAdapter.data[likePosition].likeCount)
                likeImageView?.likeImageInactive()
            })
        }
    }
}