package com.soulmates.valley.ui.post.read

import android.os.Bundle
import com.soulmates.valley.R
import com.soulmates.valley.base.BaseActivity
import com.soulmates.valley.base.BasePagerAdapter
import com.soulmates.valley.databinding.ActivityPostImageBinding
import com.soulmates.valley.ui.post.PostViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PostImageActivity : BaseActivity<ActivityPostImageBinding, PostViewModel>() {
    override val layoutResID: Int = R.layout.activity_post_image
    override val viewModel: PostViewModel by viewModel()
    var imageList = arrayListOf<String>()
    var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.let {
            imageList = it.getStringArrayListExtra("imageList")
            position = it.getIntExtra("position", 0)
        }
        setViewPager()
    }

    private fun setViewPager() {
        viewDataBinding.actPostImageVp.run {
            adapter = BasePagerAdapter(supportFragmentManager).apply {
                isSaveEnabled = false
                for(i in 0 until imageList.size){
                    addFragment(PostImageFragment.newInstance(imageList[i]))
                }
            }
            currentItem = position
            offscreenPageLimit = 4
        }
    }
}