package com.soulmates.valley.ui.search.result

import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayout
import com.soulmates.valley.R
import com.soulmates.valley.base.BaseFragment
import com.soulmates.valley.base.BasePagerAdapter
import com.soulmates.valley.databinding.FragmentSearchResultBinding
import com.soulmates.valley.ui.search.SearchViewModel
import com.soulmates.valley.ui.search.result.post.PostResultFragment
import com.soulmates.valley.ui.search.result.tag.TagResultFragment
import com.soulmates.valley.ui.search.result.user.UserResultFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchResultFragment : BaseFragment<FragmentSearchResultBinding, SearchViewModel>() {
    override val layoutResID: Int = R.layout.fragment_search_result
    override val viewModel: SearchViewModel by viewModel()
    var keyword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            keyword = it.getString("keyword")!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewPager()
        setTabLayout()
    }

    private fun setViewPager() {
        viewDataBinding.fragSearchResultVp.run {
            adapter = BasePagerAdapter(childFragmentManager).apply {
                addFragment(UserResultFragment.newInstance(keyword))
                addFragment(PostResultFragment.newInstance(keyword))
                addFragment(TagResultFragment.newInstance(keyword))
                isSaveEnabled = false
            }
            currentItem = 0
            offscreenPageLimit = 2
        }
    }

    private fun setTabLayout() {
        viewDataBinding.fragSearchResultTl.apply{
            setupWithViewPager(viewDataBinding.fragSearchResultVp)

            getTabAt(0)!!.text = "계정"
            getTabAt(1)!!.text = "게시글"
            getTabAt(2)!!.text = "태그"
        }
    }

    companion object {
        fun newInstance(param1: String) =
            SearchResultFragment().apply {
                arguments = Bundle().apply {
                    putString("keyword", param1)
                }
            }
    }
}