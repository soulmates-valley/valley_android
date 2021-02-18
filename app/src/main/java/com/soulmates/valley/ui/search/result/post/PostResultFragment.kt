package com.soulmates.valley.ui.search.result.post

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
import com.soulmates.valley.base.BaseFragment
import com.soulmates.valley.data.model.post.Post
import com.soulmates.valley.data.model.PaginationStatus
import com.soulmates.valley.databinding.FragmentSearchResultPostBinding
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


class PostResultFragment : BaseFragment<FragmentSearchResultPostBinding, SearchViewModel>() {
    override val layoutResID: Int = R.layout.fragment_search_result_post
    override val viewModel: SearchViewModel by viewModel()
    private val postViewModel: PostViewModel by viewModel()
    lateinit var requestManager: RequestManager
    private var postRvAdapter: FeedRvAdapter = FeedRvAdapter()
    private val SEARCH_POST_STATE = "searchPostState"
    private var likePosition = -1
    private var likeImageView: ImageView? = null
    private var likeTextView: TextView? = null
    var keyword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            keyword = it.getString("keyword")!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.keyword = keyword
        requestManager = Glide.with(this)
        setRv()
        setClick()

        viewModel.showProfilePost(savedInstanceState?.getString(SEARCH_POST_STATE) ?: "")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_POST_STATE, viewModel.currentProfilePost())
    }

    private fun setClick(){
        viewDataBinding.fragSearchResultPostTv.setSafeOnClickListener {
            startActivity(Intent(requireContext(), PostListActivity::class.java)
                .putExtra("keyword", keyword)
                .putExtra("fromView", 0)
            )
        }
    }

    private fun setRv() {
        postRvAdapter = FeedRvAdapter().apply {
            setFeedItemClickListener(onFeedItemClickListener)
            setRequestManager(requestManager)
            setHasStableIds(true)
        }
        viewDataBinding.fragSearchResultPostRv.run {
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
        viewModel.run {
            searchPost.observe(requireActivity(), Observer { postRvAdapter.submitList(it) })
            paginationStatus.observe(requireActivity(), Observer {
                when(it){
                    PaginationStatus.Empty -> {
                        viewDataBinding.fragSearchResultPostLlEmpty.visibility = View.VISIBLE
                        viewDataBinding.fragSearchResultPostRv.visibility = View.GONE
                    }
                    PaginationStatus.Error -> Toast.makeText(requireContext(), "서버 점검 중입니다.", Toast.LENGTH_SHORT).show()
                    else -> viewDataBinding.fragSearchResultPostRv.visibility = View.VISIBLE
                }
            })
        }

        postViewModel.run {
            postLikeStatus.observe(requireActivity(), Observer {
                postRvAdapter.updateLike(likePosition)
                likeTextView?.likeTextActive(++postRvAdapter.data[likePosition].likeCount)
                likeImageView?.likeImageActive()
            })

            deleteLikeStatus.observe(requireActivity(), Observer {
                postRvAdapter.updateLike(likePosition)
                likeTextView?.likeTextInactive(--postRvAdapter.data[likePosition].likeCount)
                likeImageView?.likeImageInactive()
            })
        }
    }

    private val onFeedItemClickListener = object : FeedViewHolder.OnFeedItemClickListener {
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

    companion object {
        fun newInstance(param1: String) =
            PostResultFragment().apply {
                arguments = Bundle().apply {
                    putString("keyword", param1)
                }
            }
    }
}