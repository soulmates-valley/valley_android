package com.soulmates.valley.ui.search.result.tag

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.soulmates.valley.BR
import com.soulmates.valley.R
import com.soulmates.valley.base.BaseFragment
import com.soulmates.valley.base.BaseRecyclerViewAdapter
import com.soulmates.valley.data.model.search.SearchTag
import com.soulmates.valley.databinding.FragmentSearchResultTagBinding
import com.soulmates.valley.databinding.ItemSearchResultTagBinding
import com.soulmates.valley.ui.search.SearchViewModel
import com.soulmates.valley.ui.search.result.post.PostListActivity
import com.soulmates.valley.util.view.WrapContentLinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel


class TagResultFragment : BaseFragment<FragmentSearchResultTagBinding, SearchViewModel>() {
    override val layoutResID: Int = R.layout.fragment_search_result_tag
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
        viewModel.getSearchTag(keyword)
        setTagResultRv()
    }

    private fun setTagResultRv(){
        viewDataBinding.fragSearchResultTagRv.apply {
            adapter = object : BaseRecyclerViewAdapter<SearchTag, ItemSearchResultTagBinding>(){
                override val bindingVariableId: Int get() = BR.tag
                override val layoutResID: Int = R.layout.item_search_result_tag
                override val listener: OnItemClickListener? = object : OnItemClickListener{
                    override fun onItemClicked(item: Any?, position: Int?) {
                        startActivity(
                            Intent(requireContext(), PostListActivity::class.java)
                            .putExtra("keyword", keyword)
                            .putExtra("fromView", 1)
                        )
                    }
                }
            }
            layoutManager = WrapContentLinearLayoutManager()
        }
        viewModel.tagList.observe(requireActivity(), Observer {
            if(it.isEmpty()) viewDataBinding.fragSearchResultTagLlEmpty.visibility = View.VISIBLE
            else {
                viewDataBinding.fragSearchResultTagLlEmpty.visibility = View.GONE
                (viewDataBinding.fragSearchResultTagRv.adapter as BaseRecyclerViewAdapter<SearchTag, ItemSearchResultTagBinding>).run {
                    replaceAll(it)
                    notifyDataSetChanged()
                }
            }
        })
    }

    companion object {
        fun newInstance(param1: String) =
            TagResultFragment().apply {
                arguments = Bundle().apply {
                    putString("keyword", param1)
                }
            }
    }
}