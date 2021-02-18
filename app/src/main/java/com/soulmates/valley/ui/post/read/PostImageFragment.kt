package com.soulmates.valley.ui.post.read

import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import com.bumptech.glide.Glide
import com.soulmates.valley.R
import com.soulmates.valley.base.BaseFragment
import com.soulmates.valley.databinding.FragmentPostImageBinding
import com.soulmates.valley.ui.post.PostViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class PostImageFragment : BaseFragment<FragmentPostImageBinding, PostViewModel>() {
    override val layoutResID: Int = R.layout.fragment_post_image
    override val viewModel: PostViewModel by viewModel()

    var image = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            image = it.getString("image")!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(this.isRemoving) return
        Glide.with(this)
            .load(image)
            .into(viewDataBinding.fragPostImageIv)
    }

    companion object {
        fun newInstance(param1: String) =
            PostImageFragment().apply {
                arguments = Bundle().apply {
                    putString("image", param1)
                }
            }
    }
}