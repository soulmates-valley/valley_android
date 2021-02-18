package com.soulmates.valley.ui.tab

import android.graphics.Point
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayout
import com.soulmates.valley.R
import com.soulmates.valley.base.BaseActivity
import com.soulmates.valley.base.BasePagerAdapter
import com.soulmates.valley.base.BaseViewModel
import com.soulmates.valley.data.local.room.dao.UserInfoDao
import com.soulmates.valley.databinding.ActivityMainBinding
import com.soulmates.valley.databinding.TapMainBinding
import com.soulmates.valley.ui.tab.MainActivity.MainObject.profileImg
import com.soulmates.valley.ui.chat.ChatRoomFragment
import com.soulmates.valley.ui.friend.FriendFragment
import com.soulmates.valley.ui.feed.home.HomeFragment
import com.soulmates.valley.ui.profile.ProfileFragment
import com.soulmates.valley.ui.search.SearchFragment
import com.soulmates.valley.util.listener.BackPressHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class MainActivity : BaseActivity<ActivityMainBinding, BaseViewModel>() {

    override val layoutResID: Int = R.layout.activity_main
    override val viewModel: BaseViewModel
        get() = TODO("Not yet implemented")

    private val dao: UserInfoDao by inject()
    lateinit var backPressHandler: BackPressHandler
    private var mOnKeyBackPressedListener: OnKeyBackPressedListener? = null
    lateinit var viewPagerAdapter: BasePagerAdapter

    interface OnKeyBackPressedListener {
        fun onBack()
    }


    object MainObject {
        var windowWidth = 0
        var profileImg: String? = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backPressHandler = BackPressHandler(this)

        CoroutineScope(Dispatchers.IO).launch {
            profileImg = dao.getProfileImg()
            launch(Dispatchers.Main) {
                setTabLayout(profileImg)
            }
        }
        getWindowWidth()
        setViewPager()
    }

    private fun getWindowWidth() {

        val display: Display = this.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        MainObject.windowWidth = size.x
    }

    private fun setViewPager() {
        viewPagerAdapter = BasePagerAdapter(supportFragmentManager).apply {
            addFragment(HomeFragment())
            addFragment(SearchFragment())
            addFragment(FriendFragment())
            addFragment(ChatRoomFragment())
            addFragment(ProfileFragment())
        }

        viewDataBinding.actMainVp.run {
            adapter = viewPagerAdapter.apply {
                isSaveEnabled = false
            }
            currentItem = 0
            offscreenPageLimit = 4
        }
    }

    private fun setTabLayout(profileUrl: String?) {
        val viewGroup: ViewGroup = viewDataBinding.root as ViewGroup
        val tapBinding = DataBindingUtil.inflate<TapMainBinding>(
            LayoutInflater.from(this),
            R.layout.tap_main,
            viewGroup,
            false
        )
        if (this.isFinishing) return
        Glide.with(this)
            .load(profileUrl)
            .error(R.drawable.img_url_default)
            .apply(RequestOptions().circleCrop())
            .into(tapBinding.tapMainIvProfile)

        viewDataBinding.actMainTl.run {
            setupWithViewPager(viewDataBinding.actMainVp)

            getTabAt(0)!!.customView = tapBinding.tapMainRlHome
            getTabAt(1)!!.customView = tapBinding.tapMainRlRecco
            getTabAt(2)!!.customView = tapBinding.tapMainRlFriend
            getTabAt(3)!!.customView = tapBinding.tapMainRlChat
            getTabAt(4)!!.customView = tapBinding.tapMainRlProfile

            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {
                    when (tab!!.position) {
                        0 -> {
                            val v = (viewPagerAdapter.getItem(tab.position) as HomeFragment)
                            v.viewDataBinding.fragHomeNsv.smoothScrollTo(0, 0)
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabSelected(tab: TabLayout.Tab?) {}
            })
        }
    }

    override fun onBackPressed() {
        if (mOnKeyBackPressedListener != null) {
            mOnKeyBackPressedListener!!.onBack()
            mOnKeyBackPressedListener = null
        } else {
            backPressHandler.onBackPressed()
        }
    }
}