package com.soulmates.valley.ui.post.read

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.soulmates.valley.data.model.comment.Comment
import com.soulmates.valley.data.model.friend.Follow
import com.soulmates.valley.databinding.ItemCommentBinding
import com.soulmates.valley.util.extension.setSafeOnClickListener

class CommentRvAdapter : PagedListAdapter<Comment, CommentViewHolder>(diffCallback) {

    lateinit var binding: ItemCommentBinding
    private var listener: CommentViewHolder.OnCommentItemClickListener? = null
    var data = mutableListOf<Comment>()

    fun seCommentItemClickListener(listener: CommentViewHolder.OnCommentItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding, listener!!)
    }

    override fun getItemId(position: Int) = position.toLong()
    override fun getItemViewType(position: Int): Int = position

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val data = getItem(position)
        holder.run {
            bind(data!!)
            binding.comment = data
            binding.executePendingBindings()
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Comment>() {
            override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean =
                oldItem.commentId == newItem.commentId

            override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean =
                oldItem.commentId == newItem.commentId
        }
    }
}

class CommentViewHolder(
    val binding: ItemCommentBinding,
    val listener: OnCommentItemClickListener) : RecyclerView.ViewHolder(binding.root) {

    private var commentData: Comment? = null
    init {
        binding.itemCommentCivProfile.setSafeOnClickListener {
            listener.onProfileClick(commentData!!.userId)
        }
    }

    fun bind(data: Comment){
        commentData = data
    }

    interface OnCommentItemClickListener {
        fun onProfileClick(userId: Int)
    }
}