package com.soulmates.valley.ui.friend.all

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
import com.soulmates.valley.databinding.FragmentFriendListPageBinding
import com.soulmates.valley.ui.friend.FriendFragment
import com.soulmates.valley.ui.friend.FriendViewModel
import com.soulmates.valley.ui.profile.ProfileActivity
import com.soulmates.valley.util.view.WrapContentLinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel


class AllFollowListPageFragment : BaseFragment<FragmentFriendListPageBinding, FriendViewModel>() {
    override val layoutResID: Int = R.layout.fragment_friend_list_page
    override val viewModel: FriendViewModel by viewModel()
    private lateinit var requestManager: RequestManager
    var friendListRvAdapter: FriendListRvAdapter? = null
    var followType = 0 // 1은 팔로잉, 2는 팔로워
    var followButtonView : TextView? = null
    var followButtonPosition = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestManager = Glide.with(this)

        arguments?.let {
            followType = it.getInt("followType")
        }

        when (followType) {
            1 -> {
                viewModel.getFollowingList().observe(requireActivity(), Observer {
                    friendListRvAdapter!!.submitList(it)
                })
            }
            2 -> {
                viewModel.getFollowerList().observe(requireActivity(), Observer {
                    friendListRvAdapter!!.submitList(it)
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
                when (followType) {
                    1 -> {
                        viewModel.refreshFollowingList()
                        (parentFragment as FriendFragment).viewModel.getFollowCount()
                    }
                    2 -> {
                        viewModel.refreshFollowerList()
                        (parentFragment as FriendFragment).viewModel.getFollowCount()
                    }
                }
                this@apply.isRefreshing = false
            }
            setColorSchemeColors(ContextCompat.getColor(context, R.color.main_green))
        }
    }

    private val onFriendClickListener = object :
        FriendListViewHolder.OnFriendClickListener {

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
            friendListRvAdapter?.data!![followButtonPosition].isFollowed = false
        })

        viewModel.postFollowStatus.observe(requireActivity(), Observer {
            followButtonView?.run {
                setBackgroundResource(R.drawable.border_5_454545)
                setTextColor(ContextCompat.getColor(context, R.color.gray))
                text = "팔로잉"
            }
            friendListRvAdapter?.data!![followButtonPosition].isFollowed = true
        })
    }

    private fun setFollowRv() {
        friendListRvAdapter = FriendListRvAdapter()
            .apply {
            setFriendClickListener(onFriendClickListener)
            setRequestManager(requestManager)
            setHasStableIds(true)
        }
        viewDataBinding.fragFriendListPageRv.run {
            adapter = friendListRvAdapter
            (itemAnimator as SimpleItemAnimator).run {
                changeDuration = 0
                supportsChangeAnimations = false
            }
            layoutManager = WrapContentLinearLayoutManager()
        }
    }

    companion object {
        fun newInstance(followType: Int): AllFollowListPageFragment {
            val frag =
                AllFollowListPageFragment()
            val bundle = Bundle()
            bundle.putInt("followType", followType)
            frag.arguments = bundle
            return frag
        }
    }
}