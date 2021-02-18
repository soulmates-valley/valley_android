package com.soulmates.valley.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.tabs.TabLayout
import com.soulmates.valley.BR
import com.soulmates.valley.R
import com.soulmates.valley.ValleyApplication
import com.soulmates.valley.base.BaseActivity
import com.soulmates.valley.base.BasePagerAdapter
import com.soulmates.valley.base.BaseRecyclerViewAdapter
import com.soulmates.valley.data.model.profile.Profile
import com.soulmates.valley.databinding.ActivityProfileBinding
import com.soulmates.valley.databinding.ItemProfileInterestBinding
import com.soulmates.valley.ui.chat.ChatActivity
import com.soulmates.valley.ui.friend.FriendViewModel
import com.soulmates.valley.ui.post.write.url.WebViewActivity
import com.soulmates.valley.util.extension.glide
import com.soulmates.valley.util.extension.setSafeOnClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ProfileActivity : BaseActivity<ActivityProfileBinding, ProfileViewModel>() {
    override val layoutResID: Int = R.layout.activity_profile
    override val viewModel: ProfileViewModel by viewModel()
    private val friendViewModel: FriendViewModel by viewModel()
    private var userId = -1
    private var userProfileImg = ""
    private var userLink = ""
    private var userNickname = ""
    private var isFollowing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewDataBinding.vm = viewModel

        userId = intent.getIntExtra("userId", -1) // 넘겨받은 user ID로 프로필 조회 통신
        if (userId == ValleyApplication.prefManager.userId.toInt()) {
            viewDataBinding.actProfileTvChat.visibility = View.GONE
            viewDataBinding.actProfileTvFollow.visibility = View.GONE
        }
        viewModel.getProfileInfo(userId)

        observe()
        setClick()
        setViewPager()
        setTabLayout()
    }

    private fun setClick() {
        viewDataBinding.run {
            actProfileIvBack.setSafeOnClickListener { finish() }

            actProfileTvFollow.setSafeOnClickListener {
                if (isFollowing) {
                    friendViewModel.deleteFollow(userId)
                    friendViewModel.deleteFollowStatus.observe(this@ProfileActivity, Observer {
                        actProfileTvFollow.run {
                            setBackgroundResource(R.drawable.solid_5_main_yellow)
                            setTextColor(ContextCompat.getColor(context, R.color.black))
                            text = "팔로우"
                        }
                        isFollowing = false
                    })
                } else {
                    friendViewModel.postFollow(userId)
                    friendViewModel.postFollowStatus.observe(this@ProfileActivity, Observer {
                        actProfileTvFollow.run {
                            setBackgroundResource(R.drawable.border_5_454545)
                            setTextColor(ContextCompat.getColor(context, R.color.gray))
                            text = "팔로잉"
                        }
                        isFollowing = true
                    })
                }
            }

            actProfileTvChat.setSafeOnClickListener {
                // 채팅 방이 없으면 새로 만들고 들어가기
                // 채팅방이 있으면 기존 방 띄우기
                startActivity(
                    Intent(this@ProfileActivity, ChatActivity::class.java)
                        .putExtra("otherUserId", userId)
                        .putExtra("profileImg", userProfileImg)
                        .putExtra("nickname", userNickname)
                        .putExtra("roomName", "")
                )
            }

            actProfileTvLink.setSafeOnClickListener {
                startActivity(
                    Intent(this@ProfileActivity, WebViewActivity::class.java)
                        .putExtra("url", userLink)
                        .putExtra("title", "${userNickname}님의 link")
                )
            }

        }
    }

    private fun setViewPager() {
        viewDataBinding.actProfileVp.run {
            adapter = BasePagerAdapter(supportFragmentManager).apply {
                addFragment(PostListPageFragment.newInstance(0, userId))
                addFragment(PostListPageFragment.newInstance(1, userId))
                isSaveEnabled = false
            }
            currentItem = 0
            offscreenPageLimit = 1
        }
    }

    private fun setTabLayout() {
        viewDataBinding.actProfileTl.apply {
            setupWithViewPager(viewDataBinding.actProfileVp)

            getTabAt(0)!!.text = "작성한 글"
            getTabAt(1)!!.text = "저장한 글"
        }
    }

    private fun observe() {
        viewModel.profileInfo.observe(this, Observer { res ->
            setInterestRv(res.interest)
            isFollowing = res.isFollowing
            userNickname = res.nickname
            res.link?.let { userLink = it }
            res.profileImg?.let { userProfileImg = it }

            viewDataBinding.apply {
                this.actProfileCiv.glide(res.profileImg, Glide.with(this@ProfileActivity))
                this.actProfileTvNickname.text = res.nickname
                res.description?.let {
                    this.actProfileTvDescription.visibility = View.VISIBLE
                    this.actProfileTvDescription.text = it
                }
                res.link?.let {
                    this.actProfileTvLink.visibility = View.VISIBLE
                    this.actProfileTvLink.text = it
                }
            }
        })
    }

    private fun setInterestRv(interest: List<Int>?) {
        if (interest.isNullOrEmpty()) return
        viewDataBinding.actProfileRvInterest.apply {
            adapter = object : BaseRecyclerViewAdapter<Int, ItemProfileInterestBinding>() {
                override val bindingVariableId: Int = BR.interestIdx
                override val layoutResID: Int = R.layout.item_profile_interest
                override val listener: OnItemClickListener? = null
            }
            layoutManager = FlexboxLayoutManager(this@ProfileActivity).apply {
                flexWrap = FlexWrap.WRAP
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.CENTER
            }
        }

    }
}