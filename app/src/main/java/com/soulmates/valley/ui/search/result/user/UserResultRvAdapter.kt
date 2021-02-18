package com.soulmates.valley.ui.search.result.user

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.soulmates.valley.data.model.search.SearchUser
import com.soulmates.valley.databinding.ItemSearchResultUserBinding
import com.soulmates.valley.ui.feed.FeedViewHolder
import com.soulmates.valley.util.extension.glide
import com.soulmates.valley.util.extension.setSafeOnClickListener
import timber.log.Timber

class UserResultRvAdapter : RecyclerView.Adapter<UserResultViewHolder>() {

    lateinit var binding: ItemSearchResultUserBinding
    private var requestManager: RequestManager? = null
    var data = mutableListOf<SearchUser>()
    private var listener: UserResultViewHolder.OnUserClickListener? = null

    fun setUserClickListener(listener: UserResultViewHolder.OnUserClickListener) {
        this.listener = listener
    }

    fun setRequestManager(requestManager: RequestManager) {
        this.requestManager = requestManager
    }

    fun replaceAll(array: ArrayList<SearchUser>?) {
        array?.let {
            data.run {
                clear()
                addAll(it)
            }
        }
    }

    override fun getItemViewType(position: Int): Int = position

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserResultViewHolder {
        binding = ItemSearchResultUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserResultViewHolder(binding, requestManager!!, listener!!)
    }

    override fun onBindViewHolder(holder: UserResultViewHolder, position: Int) {
        holder.run {
            bind(data[position])
            binding.user = data[position]
            binding.executePendingBindings()
        }
    }
}

class UserResultViewHolder(
    val binding: ItemSearchResultUserBinding,
    val requestManager: RequestManager, val listener: OnUserClickListener
) : RecyclerView.ViewHolder(binding.root) {

    var userData: SearchUser? = null

    init {
        binding.itemSearchResultUserCl.setSafeOnClickListener {
            listener.onItemClick(userData!!.userId)
        }
    }

    fun bind(data: SearchUser) {
        this.userData = data

        binding.itemSearchUserCivProfile.glide(userData!!.userImage, requestManager)
        val userResultInterestAdapter = UserResultInterestRvAdpater()
        binding.itemSearchUserRvInterest.apply {
            adapter = userResultInterestAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        userResultInterestAdapter.run {
            replaceAll(userData!!.interest)
        }
    }

    interface OnUserClickListener {
        fun onItemClick(userId: Int)
    }
}