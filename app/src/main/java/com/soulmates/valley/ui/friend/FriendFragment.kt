package com.soulmates.valley.ui.friend

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.google.android.material.tabs.TabLayout
import com.soulmates.valley.BR
import com.soulmates.valley.R
import com.soulmates.valley.base.BaseFragment
import com.soulmates.valley.base.BasePagerAdapter
import com.soulmates.valley.base.BaseRecyclerViewAdapter
import com.soulmates.valley.data.model.friend.FriendButton
import com.soulmates.valley.databinding.FragmentFriendBinding
import com.soulmates.valley.databinding.ItemFriendButtonBinding
import com.soulmates.valley.ui.friend.all.AllFollowListPageFragment
import com.soulmates.valley.ui.friend.recommend.RecommendFollowListPageFragment
import com.soulmates.valley.util.extension.checkNetworkConnection
import com.soulmates.valley.util.view.HorizonItemDecorator
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class FriendFragment : BaseFragment<FragmentFriendBinding, FriendViewModel>() {
    override val layoutResID: Int = R.layout.fragment_friend
    override val viewModel: FriendViewModel by viewModel()

    val buttonList = arrayListOf(
        FriendButton("모든 친구", true),
        FriendButton("추천 친구", false)
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (checkNetworkConnection(requireContext())) {
            viewModel.getFollowCount()
        } else {
            viewDataBinding.fragFriendTlAll.run {
                getTabAt(0)?.text = " 팔로잉"
                getTabAt(1)?.text = " 팔로워"
            }
        }

        setText()
        setRv()
        observe()
        setViewPager()
        setTabLayout()
    }

    override fun onResume() {
        super.onResume()
        if (checkNetworkConnection(requireContext())) viewModel.getFollowCount()
    }

    private fun setText() {
        viewDataBinding.fragFriendHeader.layoutHeaderTitleOnlyTv.text = "친구"
    }

    private fun observe() {
        viewModel.followCount.observe(requireActivity(), Observer {
            viewDataBinding.fragFriendTlAll.run {
                getTabAt(0)?.text = it.followingCount.toString() + " 팔로잉"
                getTabAt(1)?.text = it.followerCount.toString() + " 팔로워"
            }
        })
    }

    private fun setRv() {
        viewDataBinding.fragFriendRvButton.apply {
            adapter = object : BaseRecyclerViewAdapter<FriendButton, ItemFriendButtonBinding>() {
                override val bindingVariableId: Int = BR.button
                override val layoutResID: Int = R.layout.item_friend_button
                override val listener: OnItemClickListener? = friendButtonClickListener
            }
            if (itemDecorationCount == 0) addItemDecoration(HorizonItemDecorator(2, 20))
        }
        (viewDataBinding.fragFriendRvButton.adapter as BaseRecyclerViewAdapter<FriendButton, ItemFriendButtonBinding>).run {
            replaceAll(buttonList)
            notifyDataSetChanged()
        }
    }

    private val friendButtonClickListener = object : BaseRecyclerViewAdapter.OnItemClickListener {
        override fun onItemClicked(item: Any?, position: Int?) {
            when (position) {
                0 -> { // 모든 친구
                    buttonList[0].isSelected = true
                    buttonList[1].isSelected = false

                    viewModel.getFollowCount()
                    viewDataBinding.run {
                        fragFriendClAll.visibility = View.VISIBLE
                        fragFriendClRec.visibility = View.GONE
                    }
                }
                1 -> { // 추천 친구
                    buttonList[0].isSelected = false
                    buttonList[1].isSelected = true

                    viewDataBinding.run {
                        fragFriendClAll.visibility = View.GONE
                        fragFriendClRec.visibility = View.VISIBLE
                    }
                }
            }
            (viewDataBinding.fragFriendRvButton.adapter as BaseRecyclerViewAdapter<FriendButton, ItemFriendButtonBinding>).run {
                replaceAll(buttonList)
                notifyDataSetChanged()
            }
        }
    }

    private fun setViewPager() {
        viewDataBinding.fragFriendVpAll.run {
            adapter = BasePagerAdapter(childFragmentManager).apply {
                addFragment(AllFollowListPageFragment.newInstance(1))
                addFragment(AllFollowListPageFragment.newInstance(2))
                isSaveEnabled = false
            }
            currentItem = 0
            offscreenPageLimit = 1
        }

        viewDataBinding.fragFriendVpRec.run {
            adapter = BasePagerAdapter(childFragmentManager).apply {
                addFragment(RecommendFollowListPageFragment.newInstance(1))
                addFragment(RecommendFollowListPageFragment.newInstance(2))
                isSaveEnabled = false
            }
            currentItem = 0
            offscreenPageLimit = 1
        }
    }

    private fun setTabLayout() {
        viewDataBinding.fragFriendTlAll.apply {
            setupWithViewPager(viewDataBinding.fragFriendVpAll)
            getTabAt(0)!!.text = "팔로잉"
            getTabAt(1)!!.text = "팔로워"
        }
        viewDataBinding.fragFriendTlRec.apply {
            setupWithViewPager(viewDataBinding.fragFriendVpRec)
            getTabAt(0)!!.text = "알 수도 있는 친구"
            getTabAt(1)!!.text = "좋아할 만한 친구"
        }
    }
}