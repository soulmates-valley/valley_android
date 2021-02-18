package com.soulmates.valley.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<VB : ViewDataBinding, VM : BaseViewModel> : Fragment() {
    lateinit var viewDataBinding: VB

    abstract val layoutResID: Int
    abstract val viewModel: VM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, layoutResID, container, false)
        viewDataBinding.lifecycleOwner = this@BaseFragment
        return viewDataBinding.root
    }

}