package com.soulmates.valley.ui.search.result.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.soulmates.valley.R
import com.soulmates.valley.base.BaseFragment
import com.soulmates.valley.data.model.search.SearchUser
import com.soulmates.valley.databinding.FragmentSearchResultUserBinding
import com.soulmates.valley.ui.profile.ProfileActivity
import com.soulmates.valley.ui.search.SearchViewModel
import com.soulmates.valley.util.view.WrapContentLinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class UserResultFragment : BaseFragment<FragmentSearchResultUserBinding, SearchViewModel>() {
    override val layoutResID: Int = R.layout.fragment_search_result_user
    override val viewModel: SearchViewModel by viewModel()
    lateinit var requestManager: RequestManager
    var userRvAdapter: UserResultRvAdapter? = null
    var keyword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            keyword = it.getString("keyword")!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestManager = Glide.with(this)

        viewModel.getSearchUser(keyword)
        setUserResultRv()
    }

    val userClickListener = object : UserResultViewHolder.OnUserClickListener{
        override fun onItemClick(userId: Int) {
            startActivity(
                Intent(requireContext(), ProfileActivity::class.java)
                    .putExtra("userId", userId)
            )
        }
    }

    private fun setUserResultRv(){
        userRvAdapter = UserResultRvAdapter().apply {
            setUserClickListener(userClickListener)
            setRequestManager(requestManager)
            setHasStableIds(true)
        }
        viewDataBinding.fragSearchResultFriendRv.run {
            adapter = userRvAdapter
            (itemAnimator as SimpleItemAnimator).run {
                changeDuration = 0
                supportsChangeAnimations = false
            }
            layoutManager = WrapContentLinearLayoutManager()
        }
        viewModel.userList.observe(requireActivity(), Observer {
            if(it.isEmpty()){
                viewDataBinding.fragSearchResultUserLlEmpty.visibility = View.VISIBLE
            }else{
                viewDataBinding.fragSearchResultUserLlEmpty.visibility = View.GONE
                userRvAdapter?.run{
                    replaceAll(it as ArrayList<SearchUser>)
                    notifyDataSetChanged()
                }
            }

        })
    }

    companion object {
        fun newInstance(param1: String) =
            UserResultFragment().apply {
                arguments = Bundle().apply {
                    putString("keyword", param1)
                }
            }
    }
}