package com.soulmates.valley.ui.friend.recommend

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.soulmates.valley.R
import com.soulmates.valley.ValleyApplication
import com.soulmates.valley.base.BaseFragment
import com.soulmates.valley.data.model.friend.RecommendFollow
import com.soulmates.valley.databinding.FragmentFriendListPageBinding
import com.soulmates.valley.ui.friend.FriendViewModel
import com.soulmates.valley.ui.profile.ProfileActivity
import com.soulmates.valley.util.view.WrapContentLinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class RecommendFollowListPageFragment : BaseFragment<FragmentFriendListPageBinding, FriendViewModel>() {
    override val layoutResID: Int = R.layout.fragment_friend_list_page
    override val viewModel: FriendViewModel by viewModel()
    private lateinit var requestManager: RequestManager
    var recommendFollowRvAdapter: RecommendFollowRvAdapter? = null
    var recommendType = 0 // 1은 알수도 있는 친구, 2는 좋아할만한 친구
    var followButtonView : TextView? = null
    var followButtonPosition = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestManager = Glide.with(this)

        arguments?.let {
            recommendType = it.getInt("recommendType")
        }

        when (recommendType) {
            1 -> {
                viewModel.getKnowFollowList()
                viewModel.knowFollowList.observe(requireActivity(), Observer {
                    recommendFollowRvAdapter?.run {
                        replaceAll(it as ArrayList<RecommendFollow>)
                        notifyDataSetChanged()
                    }
                })
            }
            2 -> {
                viewModel.getLikeFollowList()
                viewModel.likeFollowList.observe(requireActivity(), Observer {
                    recommendFollowRvAdapter?.run {
                        replaceAll(it as ArrayList<RecommendFollow>)
                        notifyDataSetChanged()
                    }
                })
            }
        }

        setFollowRv()
        observe()
        setRefresh()
    }

    private fun setRefresh() {
        viewDataBinding.fragFriendListPageSrl.apply {
            setOnRefreshListener {
                when (recommendType) {
                    1 -> viewModel.getKnowFollowList()
                    2 -> viewModel.getLikeFollowList()
                }
                this@apply.isRefreshing = false
            }
            setColorSchemeColors(ContextCompat.getColor(context, R.color.main_green))
        }
    }

    private val onRecommendFollowClickListener = object : OnRecommendFollowClickListener {

        // 사용자 프로필 조회
        override fun onProfileClick(userId: Int) {
            startActivity(
                Intent(requireActivity(), ProfileActivity::class.java)
                    .putExtra("userId", userId)
            )
        }

        // 팔로우/팔로잉 상태 변경
        override fun onButtonClick(
            userId: Int,
            isFollowed: Boolean,
            button: TextView,
            position: Int
        ) {
            followButtonPosition = position
            followButtonView = button

            if (isFollowed) {
                viewModel.deleteFollow(userId)
            } else{
                viewModel.postFollow(userId)
            }
        }

    }

    private fun observe(){
        viewModel.deleteFollowStatus.observe(requireActivity(), Observer {
            followButtonView?.run {
                setBackgroundResource(R.drawable.solid_5_main_yellow)
                setTextColor(ContextCompat.getColor(context, R.color.black))
                text = "팔로우"
            }
            recommendFollowRvAdapter?.data!![followButtonPosition].isFollowed = false
        })

        viewModel.postFollowStatus.observe(requireActivity(), Observer {
            followButtonView?.run {
                setBackgroundResource(R.drawable.border_5_454545)
                setTextColor(ContextCompat.getColor(context, R.color.gray))
                text = "팔로잉"
            }
            recommendFollowRvAdapter?.data!![followButtonPosition].isFollowed = true
        })
    }

    private fun setFollowRv() {
        recommendFollowRvAdapter = RecommendFollowRvAdapter()
            .apply {
                this.viewType = recommendType
                setClickListener(onRecommendFollowClickListener)
                setRequestManager(requestManager)
                setHasStableIds(true)
            }
        viewDataBinding.fragFriendListPageRv.run {
            adapter = recommendFollowRvAdapter
            (itemAnimator as SimpleItemAnimator).run {
                changeDuration = 0
                supportsChangeAnimations = false
            }
            layoutManager = WrapContentLinearLayoutManager()
        }
    }

    companion object {
        fun newInstance(recommendType: Int): RecommendFollowListPageFragment {
            val frag = RecommendFollowListPageFragment()
            val bundle = Bundle()
            bundle.putInt("recommendType", recommendType)
            frag.arguments = bundle
            return frag
        }
    }

}