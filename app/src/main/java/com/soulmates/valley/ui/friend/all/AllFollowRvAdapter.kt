package com.soulmates.valley.ui.friend.all

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.soulmates.valley.data.model.friend.Follow
import com.soulmates.valley.databinding.ItemFragFriendListBinding
import com.soulmates.valley.util.extension.glide
import com.soulmates.valley.util.extension.setSafeOnClickListener

class FriendListRvAdapter : PagedListAdapter<Follow, FriendListViewHolder>(
    diffCallback
) {

    lateinit var binding: ItemFragFriendListBinding
    private var listener: FriendListViewHolder.OnFriendClickListener? = null
    private var requestManager: RequestManager? = null
    var data = mutableListOf<Follow>()

    fun setFriendClickListener(listener: FriendListViewHolder.OnFriendClickListener) {
        this.listener = listener
    }

    fun setRequestManager(requestManager: RequestManager) {
        this.requestManager = requestManager
    }

    override fun getItemId(position: Int) = position.toLong()
    override fun getItemViewType(position: Int): Int = position

    override fun submitList(pagedList: PagedList<Follow>?) {
        super.submitList(pagedList)
        pagedList?.addWeakCallback(listOf(), object : PagedList.Callback() {
            override fun onChanged(position: Int, count: Int) {
                data.clear()
                data.addAll(pagedList)
            }

            override fun onInserted(position: Int, count: Int) {
                data.clear()
                data.addAll(pagedList)
            }

            override fun onRemoved(position: Int, count: Int) {
                data.clear()
                data.addAll(pagedList)
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendListViewHolder {
        binding =
            ItemFragFriendListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendListViewHolder(
            binding,
            listener!!,
            requestManager!!
        )
    }

    override fun onBindViewHolder(holder: FriendListViewHolder, position: Int) {
        val data = getItem(position)!!
        holder.run {
            bind(data)
            binding.friend = data
            binding.executePendingBindings()
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Follow>() {
            override fun areItemsTheSame(oldItem: Follow, newItem: Follow): Boolean =
                oldItem.userId == newItem.userId

            override fun areContentsTheSame(oldItem: Follow, newItem: Follow): Boolean =
                oldItem.userId == newItem.userId
        }
    }
}

class FriendListViewHolder(
    val binding: ItemFragFriendListBinding,
    val listener: OnFriendClickListener,
    var requestManager: RequestManager
) : RecyclerView.ViewHolder(binding.root) {

    var followData: Follow? = null

    init {
        binding.run {
            root.setSafeOnClickListener {
                listener.onProfileClick(followData!!.userId)
            }
            itemFragFriendListTvButton.setSafeOnClickListener {
                listener.onButtonClick(
                    followData!!.userId,
                    followData!!.isFollowed,
                    binding.itemFragFriendListTvButton,
                    layoutPosition
                )
            }
        }
    }

    fun bind(data: Follow) {
        followData = data
        followData?.let {
            setProfileImg()
        }
    }

    private fun setProfileImg() {
        if (!followData!!.userImage.isNullOrBlank())
            binding.itemFragFriendListCivProfile.glide(followData!!.userImage!!, requestManager)
    }

    interface OnFriendClickListener {
        fun onProfileClick(userId: Int)
        fun onButtonClick(
            userId: Int,
            isFollowed: Boolean,
            button: TextView,
            position: Int
        )
    }
}