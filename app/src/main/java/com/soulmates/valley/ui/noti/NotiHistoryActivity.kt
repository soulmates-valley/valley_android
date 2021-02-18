package com.soulmates.valley.ui.noti

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.soulmates.valley.BR
import com.soulmates.valley.R
import com.soulmates.valley.base.BaseActivity
import com.soulmates.valley.base.BaseRecyclerViewAdapter
import com.soulmates.valley.data.model.noti.NotiHistory
import com.soulmates.valley.data.model.post.LanguageList
import com.soulmates.valley.data.model.search.SearchTag
import com.soulmates.valley.databinding.ActivityNotiHistoryBinding
import com.soulmates.valley.databinding.ItemNotiHistoryBinding
import com.soulmates.valley.databinding.ItemSearchResultTagBinding
import com.soulmates.valley.ui.search.result.post.PostListActivity
import com.soulmates.valley.util.extension.setSafeOnClickListener
import com.soulmates.valley.util.view.WrapContentLinearLayoutManager
import kotlinx.android.synthetic.main.activity_noti_history.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotiHistoryActivity : BaseActivity<ActivityNotiHistoryBinding, NotiViewModel>() {
    override val layoutResID: Int = R.layout.activity_noti_history
    override val viewModel: NotiViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewDataBinding.vm = viewModel
        viewModel.getNotiHistory()

        setNotiHistoryRv()
        setText()
        setClick()
    }

    private fun setText(){
        viewDataBinding.actNotiHistoryHeader.layoutHeaderBackTvTitle.text = "알림"
    }

    private fun setClick(){
        viewDataBinding.actNotiHistoryHeader.layoutHeaderBackIvBack.setSafeOnClickListener { finish() }
    }

    private fun setNotiHistoryRv(){
        viewDataBinding.actNotiHistoryRv.apply {
            adapter = object : BaseRecyclerViewAdapter<NotiHistory, ItemNotiHistoryBinding>(){
                override val bindingVariableId: Int get() = BR.noti
                override val layoutResID: Int = R.layout.item_noti_history
                override val listener: OnItemClickListener? = object : OnItemClickListener {
                    override fun onItemClicked(item: Any?, position: Int?) { }
                }
            }
            layoutManager = WrapContentLinearLayoutManager()
        }
        viewModel.notiHistoryList.observe(this, Observer {
            if(it.isEmpty()) viewDataBinding.actNotiHistoryLlEmpty.visibility = View.VISIBLE
            else{
                viewDataBinding.actNotiHistoryLlEmpty.visibility = View.GONE
                (viewDataBinding.actNotiHistoryRv.adapter as BaseRecyclerViewAdapter<NotiHistory, ItemNotiHistoryBinding>).run {
                    replaceAll(it)
                    notifyDataSetChanged()
                }
            }

        })
    }

}