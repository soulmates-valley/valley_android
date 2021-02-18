package com.soulmates.valley.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.soulmates.valley.BR
import com.soulmates.valley.R
import com.soulmates.valley.ValleyApplication
import com.soulmates.valley.base.BaseFragment
import com.soulmates.valley.base.BasePagerAdapter
import com.soulmates.valley.base.BaseRecyclerViewAdapter
import com.soulmates.valley.databinding.FragmentProfileBinding
import com.soulmates.valley.databinding.ItemProfileInterestBinding
import com.soulmates.valley.ui.post.write.url.WebViewActivity
import com.soulmates.valley.util.extension.setSafeOnClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileViewModel>() {
    override val layoutResID: Int = R.layout.fragment_profile
    override val viewModel: ProfileViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewDataBinding.vm = viewModel
        viewModel.getFollowCount()
        viewModel.getProfileFromRoom()

        setClick()
        setViewPager()
        setTabLayout()
        observe()
    }

    private fun observe() {
        viewModel.profileProfileFromRoom.observe(requireActivity(), Observer {
            setProfile(it.nickname, it.link, it.description)
            setProfileImg(it.profileImage)
            setInterestRv(it.interest)
        })

    }

    private fun setClick() {
        viewDataBinding.run {
            fragProfileIvSetting.setSafeOnClickListener {
                startActivity(Intent(requireContext(), SettingActivity::class.java))
            }
        }
    }

    private fun setViewPager() {
        viewDataBinding.fragProfileVp.run {
            adapter = BasePagerAdapter(childFragmentManager).apply {
                addFragment(
                    PostListPageFragment.newInstance(
                        0,
                        ValleyApplication.prefManager.userId.toInt()
                    )
                )
                addFragment(
                    PostListPageFragment.newInstance(
                        1,
                        ValleyApplication.prefManager.userId.toInt()
                    )
                )
                isSaveEnabled = false
            }
            currentItem = 0
            offscreenPageLimit = 1
        }
    }

    private fun setTabLayout() {
        viewDataBinding.fragProfileTl.apply {
            setupWithViewPager(viewDataBinding.fragProfileVp)
            getTabAt(0)!!.text = "내가 작성한 글"
            getTabAt(1)!!.text = "내가 저장한 글"
        }
    }

    private fun setProfile(nickname: String, link: String?, description: String?) {
        viewDataBinding.run {
            fragProfileTvNickname.text = nickname
            link?.let {
                fragProfileTvLink.apply {
                    visibility = View.VISIBLE
                    text = it
                    setSafeOnClickListener {
                        startActivity(
                            Intent(requireActivity(), WebViewActivity::class.java)
                                .putExtra("url", link)
                                .putExtra("title", "${nickname}님의 link")
                        )
                    }
                }
            }
            description?.let {
                fragProfileTvDescription.apply {
                    visibility = View.VISIBLE
                    text = it
                }
            }
        }
    }

    private fun setProfileImg(profileImg: String?) {
        if (this.isRemoving) return
        Glide.with(this@ProfileFragment)
            .load(profileImg)
            .error(R.drawable.img_url_default)
            .into(viewDataBinding.fragProfileCiv)
    }

    private fun setInterestRv(interest: List<Int>?) {
        if (interest.isNullOrEmpty()) return
        viewDataBinding.fragProfileRvInterest.apply {
            adapter = object : BaseRecyclerViewAdapter<Int, ItemProfileInterestBinding>() {
                override val bindingVariableId: Int = BR.interestIdx
                override val layoutResID: Int = R.layout.item_profile_interest
                override val listener: OnItemClickListener? = null
            }
            layoutManager = FlexboxLayoutManager(requireActivity()).apply {
                flexWrap = FlexWrap.WRAP
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.CENTER
            }
        }

    }
}