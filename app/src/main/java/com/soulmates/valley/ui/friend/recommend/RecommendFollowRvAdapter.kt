package com.soulmates.valley.ui.friend.recommend

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.soulmates.valley.R
import com.soulmates.valley.data.model.friend.RecommendFollow
import com.soulmates.valley.databinding.*
import com.soulmates.valley.ui.search.result.user.UserResultInterestRvAdpater
import com.soulmates.valley.util.extension.glide
import com.soulmates.valley.util.extension.setSafeOnClickListener

class RecommendFollowRvAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var requestManager: RequestManager? = null
    var viewType: Int = 0
    var data = mutableListOf<RecommendFollow>()
    private var listener: OnRecommendFollowClickListener? = null

    fun setClickListener(listener: OnRecommendFollowClickListener) {
        this.listener = listener
    }

    fun setRequestManager(requestManager: RequestManager) {
        this.requestManager = requestManager
    }

    fun replaceAll(array: ArrayList<RecommendFollow>?) {
        array?.let {
            data.run {
                clear()
                addAll(it)
            }
        }
    }

    override fun getItemViewType(position: Int): Int = viewType

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        lateinit var viewHolder: RecyclerView.ViewHolder

        return when (viewType) {
            1 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_frag_friend_know, parent, false)
                KnowFollowViewHolder(
                    ItemFragFriendKnowBinding.bind(view),
                    requestManager!!,
                    listener!!
                )
            }
            2 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_frag_friend_like, parent, false)
                LikeFollowViewHolder(
                    ItemFragFriendLikeBinding.bind(view),
                    requestManager!!,
                    listener!!
                )
            }
            else -> viewHolder
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is KnowFollowViewHolder -> {
                holder.bind(data[position])
                holder.binding.apply {
                    knowFollow = data[position]
                    executePendingBindings()
                }
            }
            is LikeFollowViewHolder -> {
                holder.bind(data[position])
                holder.binding.apply {
                    likeFollow = data[position]
                    executePendingBindings()
                }
            }
        }
    }
}

class KnowFollowViewHolder(
    val binding: ItemFragFriendKnowBinding,
    val requestManager: RequestManager, val listener: OnRecommendFollowClickListener
) : RecyclerView.ViewHolder(binding.root) {

    var followData: RecommendFollow? = null

    init {
        binding.root.setSafeOnClickListener {
            listener.onProfileClick(followData!!.userId)
        }
        binding.itemFragFriendKnowTvButton.setSafeOnClickListener {
            listener.onButtonClick(
                followData!!.userId,
                followData!!.isFollowed,
                binding.itemFragFriendKnowTvButton,
                layoutPosition
            )
        }
    }

    fun bind(data: RecommendFollow) {
        this.followData = data
        binding.itemFragFriendKnowCivProfile.glide(followData!!.profileImg, requestManager)
    }
}

class LikeFollowViewHolder(
    val binding: ItemFragFriendLikeBinding,
    val requestManager: RequestManager, val listener: OnRecommendFollowClickListener
) : RecyclerView.ViewHolder(binding.root) {

    var followData: RecommendFollow? = null

    init {
        binding.root.setSafeOnClickListener {
            listener.onProfileClick(followData!!.userId)
        }
        binding.itemFragFriendLikeTvButton.setSafeOnClickListener {
            listener.onButtonClick(
                followData!!.userId,
                followData!!.isFollowed,
                binding.itemFragFriendLikeTvButton,
                layoutPosition
            )
        }
    }

    fun bind(data: RecommendFollow) {
        this.followData = data

        binding.itemFragFriendLikeCivProfile.glide(followData!!.profileImg, requestManager)
        val interestAdapter = UserResultInterestRvAdpater()
        binding.itemFragFriendLikeRvInterest.apply {
            adapter = interestAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        interestAdapter.run {
            replaceAll(followData!!.interest)
        }
    }
}

interface OnRecommendFollowClickListener {
    fun onProfileClick(userId: Int)
    fun onButtonClick(
        userId: Int,
        isFollowed: Boolean,
        button: TextView,
        position: Int
    )
}