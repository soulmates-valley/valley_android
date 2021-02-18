package com.soulmates.valley.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.soulmates.valley.R
import com.soulmates.valley.ValleyApplication
import com.soulmates.valley.base.BaseFragment
import com.soulmates.valley.data.local.room.entity.BookmarkPost
import com.soulmates.valley.data.model.post.Post
import com.soulmates.valley.data.model.PaginationStatus
import com.soulmates.valley.databinding.FragmentPostListPageBinding
import com.soulmates.valley.ui.feed.FeedRvAdapter
import com.soulmates.valley.ui.feed.FeedViewHolder
import com.soulmates.valley.ui.post.PostViewModel
import com.soulmates.valley.ui.post.read.PostDetailActivity
import com.soulmates.valley.ui.post.write.code.CodeViewActivity
import com.soulmates.valley.ui.post.write.url.WebViewActivity
import com.soulmates.valley.util.extension.likeImageActive
import com.soulmates.valley.util.extension.likeImageInactive
import com.soulmates.valley.util.extension.likeTextActive
import com.soulmates.valley.util.extension.likeTextInactive
import com.soulmates.valley.util.view.WrapContentLinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class PostListPageFragment : BaseFragment<FragmentPostListPageBinding, ProfileViewModel>() {
    override val layoutResID: Int = R.layout.fragment_post_list_page
    override val viewModel: ProfileViewModel by viewModel()
    private val postViewModel: PostViewModel by viewModel()
    private lateinit var requestManager: RequestManager
    var profileFeedRvAdapter: FeedRvAdapter = FeedRvAdapter()
    var bookmarkPostRvAdatper: BookmarkPostAdapter = BookmarkPostAdapter()
    private val PROFILE_POST_STATE = "profilePostState"
    private var pageType = -1 // 작성한 글(0) / 저장한 글(1)
    private var userId = -1
    private var likePosition = 0
    private var likeImageView: ImageView? = null
    private var likeTextView: TextView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.showProfilePost(savedInstanceState?.getString(PROFILE_POST_STATE) ?: "")
        arguments?.let {
            pageType = it.getInt("pageType")
            userId = it.getInt("userId")
        }
        viewDataBinding.vm = viewModel
        viewModel.userId = userId
        requestManager = Glide.with(this)

        setRefresh()
        setRv()
        observe()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(PROFILE_POST_STATE, viewModel.currentProfilePost())
    }

    private fun setRefresh() {
        viewDataBinding.fragPostListPageSrl.apply {
            setOnRefreshListener {
                if (pageType == 0) viewModel.refreshProfilePost()
                this@apply.isRefreshing = false
            }
            setColorSchemeColors(ContextCompat.getColor(context, R.color.main_green))
        }
    }

    private fun observe() {
        when (pageType) {
            0 -> { // 작성한 글
                viewModel.run {
                    profilePost.observe(requireActivity(), Observer {
                        profileFeedRvAdapter.submitList(it)
                    })

                    paginationStatus.observe(requireActivity(), Observer {
                        when(it){
                            PaginationStatus.Empty -> {
                                viewDataBinding.fragProfilePostListLlEmpty.visibility = View.VISIBLE
                                viewDataBinding.fragPostListPageRv.visibility = View.GONE
                            }
                            PaginationStatus.Error -> Toast.makeText(requireContext(), "서버 점검 중입니다.", Toast.LENGTH_SHORT).show()
                            else -> viewDataBinding.fragPostListPageRv.visibility = View.VISIBLE
                        }
                    })
                }
            }
            1 -> { // 저장한 글
                viewModel.bookmarkList.observe(requireActivity(), Observer {
                    bookmarkPostRvAdatper.submitList(it)
                })
            }
        }

        postViewModel.run {
            postLikeStatus.observe(requireActivity(), Observer {
                profileFeedRvAdapter.updateLike(likePosition)
                likeTextView?.likeTextActive(++profileFeedRvAdapter.data[likePosition].likeCount)
                likeImageView?.likeImageActive()
            })

            deleteLikeStatus.observe(requireActivity(), Observer {
                profileFeedRvAdapter.updateLike(likePosition)
                likeTextView?.likeTextInactive(--profileFeedRvAdapter.data[likePosition].likeCount)
                likeImageView?.likeImageInactive()
            })
        }
    }

    private val onFeedItemClickListener = object : FeedViewHolder.OnFeedItemClickListener {
        override fun onProfileClick(userId: Int) {
            if (userId != ValleyApplication.prefManager.userId.toInt())
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
            if (isLiked) {
                postViewModel.deleteLike(postId)
            } else {
                postViewModel.postLike(postId)
            }
        }

        override fun onBookmarkClick(post: Post, imageView: ImageView) {
            viewModel.insertBookmarkPost(post)
            imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_save_on))
        }
    }

    private val onBookmarkItemClickListener = object : BookmarkViewHolder.OnBookmarkItemClickListener {
        override fun onProfileClick(userId: Int) {
            if (userId != ValleyApplication.prefManager.userId.toInt())
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

        override fun onBookmarkClick(post: BookmarkPost, imageView: ImageView) {
            viewModel.deleteBookmarkPost(post.postId)
            imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_save))
        }
    }

    private fun setRv() {
        when(pageType){
            0 -> {
                viewDataBinding.fragPostListPageRv.run {
                    adapter = profileFeedRvAdapter.apply {
                        setFeedItemClickListener(onFeedItemClickListener)
                        setRequestManager(requestManager)
                        setHasStableIds(true)
                    }
                    (itemAnimator as SimpleItemAnimator).run {
                        changeDuration = 0
                        supportsChangeAnimations = false
                    }
                    onFlingListener = null
                    layoutManager = WrapContentLinearLayoutManager()
                }
            }
            1-> {
                viewDataBinding.fragPostListPageRv.run {
                    adapter = bookmarkPostRvAdatper.apply {
                        setFeedItemClickListener(onBookmarkItemClickListener)
                        setRequestManager(requestManager)
                        setHasStableIds(true)
                    }
                    (itemAnimator as SimpleItemAnimator).run {
                        changeDuration = 0
                        supportsChangeAnimations = false
                    }
                    onFlingListener = null
                    layoutManager = WrapContentLinearLayoutManager()
                }
            }
        }

    }

    companion object {
        fun newInstance(pageType: Int, userId: Int): PostListPageFragment {
            val frag = PostListPageFragment()
            val bundle = Bundle()
            bundle.putInt("pageType", pageType)
            bundle.putInt("userId", userId)
            frag.arguments = bundle
            return frag
        }
    }
}