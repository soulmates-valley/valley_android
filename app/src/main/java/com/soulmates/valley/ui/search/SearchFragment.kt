package com.soulmates.valley.ui.search

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.soulmates.valley.BR
import com.soulmates.valley.R
import com.soulmates.valley.base.BaseFragment
import com.soulmates.valley.base.BaseRecyclerViewAdapter
import com.soulmates.valley.data.local.room.entity.SearchKeyword
import com.soulmates.valley.databinding.FragmentSearchBinding
import com.soulmates.valley.databinding.ItemSearchKeywordPopularBinding
import com.soulmates.valley.databinding.ItemSearchKeywordRecentBinding
import com.soulmates.valley.ui.search.result.SearchResultFragment
import com.soulmates.valley.util.extension.hideKeyboardWhenFocusOut
import com.soulmates.valley.util.extension.setSafeOnClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class SearchFragment : BaseFragment<FragmentSearchBinding, SearchViewModel>() {
    override val layoutResID: Int = R.layout.fragment_search
    override val viewModel: SearchViewModel by viewModel()
    var fragManager: FragmentManager? = null
    private var searchResultFrag: SearchResultFragment? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.vm = viewModel

        viewModel.getPopularKeyword()

        fragManager = childFragmentManager
        setText()
        setClick()
        setPopularKeywordRv()
        setRecentKeywordRv()
    }

    private fun setText() {
        viewDataBinding.fragSearchHeader.layoutHeaderTitleOnlyTv.text = "검색"
        viewDataBinding.fragSearchEt.run {
            hideKeyboardWhenFocusOut()
            addTextChangedListener {
                viewDataBinding.fragSearchIvSearch.isEnabled = !this.text.isNullOrBlank()
            }
        }
    }

    private fun setClick() {
        viewDataBinding.run {
            fragSearchIvClear.setSafeOnClickListener {
                this.fragSearchEt.setText("")
                childFragmentManager.fragments

                val nowFrag =
                    childFragmentManager.findFragmentById(viewDataBinding.fragSearchFlContainer.id)
                if (nowFrag != null && nowFrag == searchResultFrag) {
                    childFragmentManager.beginTransaction().remove(searchResultFrag!!).commit()
                }
            }

            fragSearchIvSearch.setSafeOnClickListener {
                showResultFrag(
                    fragSearchEt.text.toString(),
                    0
                )
            }

            fragSearchEt.setOnKeyListener(View.OnKeyListener { v, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    v.clearFocus()
                    val inputMethodManager =
                        v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)

                    showResultFrag(viewDataBinding.fragSearchEt.text.toString(), 0)
                    return@OnKeyListener true
                }
                false
            })

            fragSearchTvDeleteAll.setSafeOnClickListener {
                viewModel.deleteAll()
            }
        }
    }

    private fun showResultFrag(keyword: String, type: Int) { // type 0은 사용자 입력, 1은 인기/최신 검색어 click
        if (keyword.isBlank()) return

        if (isAdded) {
            viewModel.insertSearchKeyword(keyword) // 검색 성공시에만 추가되도록 viewmodel에서 호출하는 방식으로 추후 수정
            searchResultFrag = SearchResultFragment.newInstance(keyword)
            fragManager!!.beginTransaction()
                .add(viewDataBinding.fragSearchFlContainer.id, searchResultFrag!!).commit()
            viewDataBinding.fragSearchEt.clearFocus()
        }
        if (type == 1) viewDataBinding.fragSearchEt.setText(keyword)
    }

    private fun setPopularKeywordRv() {
        viewDataBinding.fragSearchRvPopular.apply {
            adapter = object : BaseRecyclerViewAdapter<String, ItemSearchKeywordPopularBinding>() {
                override val bindingVariableId: Int = BR.keyword
                override val layoutResID: Int = R.layout.item_search_keyword_popular
                override val listener: OnItemClickListener? = object : OnItemClickListener {
                    override fun onItemClicked(item: Any?, position: Int?) {
                        showResultFrag((item as String), 1)
                    }
                }
            }
            layoutManager = FlexboxLayoutManager(requireActivity()).apply {
                flexWrap = FlexWrap.WRAP
                flexDirection = FlexDirection.ROW
            }
        }
    }

    private fun setRecentKeywordRv() {
        viewDataBinding.fragSearchRvRecent.apply {
            adapter =
                object : BaseRecyclerViewAdapter<SearchKeyword, ItemSearchKeywordRecentBinding>() {
                    override val bindingVariableId: Int = BR.keyword
                    override val layoutResID: Int = R.layout.item_search_keyword_recent
                    override val listener: OnItemClickListener? = object : OnItemClickListener {
                        override fun onItemClicked(item: Any?, position: Int?) {
                            showResultFrag((item as SearchKeyword).keywordName, 1)
                        }
                    }
                }
            layoutManager = FlexboxLayoutManager(requireActivity()).apply {
                flexWrap = FlexWrap.WRAP
                flexDirection = FlexDirection.ROW
            }
        }

        viewModel.localKeywords.observe(requireActivity(), Observer {
            (viewDataBinding.fragSearchRvRecent.adapter as BaseRecyclerViewAdapter<SearchKeyword, ItemSearchKeywordRecentBinding>).run {
                replaceAll(it)
                notifyDataSetChanged()
            }
        })
    }
}