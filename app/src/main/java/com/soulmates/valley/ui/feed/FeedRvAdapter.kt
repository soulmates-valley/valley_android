package com.soulmates.valley.ui.feed

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.brackeys.ui.editorkit.widget.TextProcessor
import com.bumptech.glide.RequestManager
import com.soulmates.valley.BR
import com.soulmates.valley.R
import com.soulmates.valley.base.BaseRecyclerViewAdapter
import com.soulmates.valley.data.model.post.Post
import com.soulmates.valley.databinding.ItemFeedPostBinding
import com.soulmates.valley.databinding.ItemFeedPostImgBinding
import com.soulmates.valley.databinding.LayoutFeedPostUrlBinding
import com.soulmates.valley.ui.post.read.PostImageActivity
import com.soulmates.valley.util.extension.*
import com.soulmates.valley.util.view.HorizonItemDecorator


class FeedRvAdapter : PagedListAdapter<Post, FeedViewHolder>(diffCallback) {
    lateinit var binding: ItemFeedPostBinding
    private var listener: FeedViewHolder.OnFeedItemClickListener? = null
    private var requestManager: RequestManager? = null
    var data = mutableListOf<Post>()

    fun setFeedItemClickListener(listener: FeedViewHolder.OnFeedItemClickListener) {
        this.listener = listener
    }

    fun setRequestManager(requestManager: RequestManager) {
        this.requestManager = requestManager
    }

    fun updateLike(position: Int){
        data[position].isLiked = !data[position].isLiked
    }

    override fun getItemViewType(position: Int): Int = position
    override fun getItemId(position: Int) = position.toLong()

    override fun submitList(pagedList: PagedList<Post>?) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        binding = ItemFeedPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedViewHolder(binding, listener!!, requestManager!!)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.bind(getItem(position))
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
                oldItem.postId == newItem.postId

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
                oldItem == newItem
        }
    }
}

class FeedViewHolder(
    val viewDataBinding: ItemFeedPostBinding,
    val listener: OnFeedItemClickListener, var requestManager: RequestManager
) : RecyclerView.ViewHolder(viewDataBinding.root) {

    private var postData: Post? = null

    init {
        viewDataBinding.run {
            itemFeedPostClProfile.setSafeOnClickListener {
                listener.onProfileClick(postData!!.userId)
            }
            itemFeedPostRlContent.setSafeOnClickListener {
                listener.onContentClick(postData!!.postId)
            }
            itemFeedPostIvLike.setSafeOnClickListener {
                listener.onLikeClick(
                    postData!!.postId,
                    postData!!.isLiked,
                    layoutPosition,
                    viewDataBinding.itemFeedPostIvLike,
                    viewDataBinding.itemFeedPostTvLike
                )
            }
            itemFeedPostIvBookmark.setSafeOnClickListener {
                listener.onBookmarkClick(postData!!, itemFeedPostIvBookmark)
            }
        }
    }

    fun bind(data: Post?) {
        postData = data
        viewDataBinding.post = postData

        setReadMore()
        when {
            !postData!!.images.isNullOrEmpty() -> this.setImage(
                viewDataBinding.itemFeedPostRvImg,
                viewDataBinding.itemFeedPostIvImg,
                postData!!.images!!
            )
            !postData!!.link.isNullOrBlank() -> setLink(viewDataBinding.itemFeedPostLayoutUrl)
            !postData!!.languageIdx.isNullOrBlank() -> setCodeBlock(viewDataBinding.itemFeedPostCodeEditor)
        }
        viewDataBinding.executePendingBindings()
    }

    private fun setReadMore() {
        viewDataBinding.itemFeedPostTvContent.run {
            this.post {
                if (layout.getEllipsisCount(lineCount - 1) <= 0) {
                    viewDataBinding.itemFeedPostTvMore.visibility = View.GONE
                }
            }
        }
    }

    private fun setImage(rv: RecyclerView, iv: ImageView, images: List<String>) {
        if (images.size == 1) {
            iv.apply {
                visibility = View.VISIBLE
                iv.glide(images[0], requestManager)
                setSafeOnClickListener {
                    context.startActivity(Intent(context, PostImageActivity::class.java).apply {
                        putExtra("imageList", postData!!.images!!)
                        putExtra("position", 0)
                    })
                }
            }
            (viewDataBinding.itemFeedPostLayoutUrl.root.layoutParams as RelativeLayout.LayoutParams).addRule(
                RelativeLayout.BELOW,
                iv.id
            )
        } else {
            rv.apply {
                visibility = View.VISIBLE
                adapter = object : BaseRecyclerViewAdapter<String, ItemFeedPostImgBinding>() {
                    override val bindingVariableId: Int = BR.url
                    override val layoutResID: Int = R.layout.item_feed_post_img
                    override val listener: OnItemClickListener? = object : OnItemClickListener {
                        override fun onItemClicked(item: Any?, position: Int?) {
                            context.startActivity(
                                Intent(
                                    context,
                                    PostImageActivity::class.java
                                ).apply {
                                    putExtra("imageList", postData!!.images)
                                    putExtra("position", position)
                                })
                        }
                    }
                }
                onFlingListener = null
                LinearSnapHelper().attachToRecyclerView(this)
                if (itemDecorationCount == 0) addItemDecoration(
                    HorizonItemDecorator(
                        postData!!.images!!.size,
                        10
                    )
                )
            }
        }
    }

    private fun setLink(view: LayoutFeedPostUrlBinding) {
        view.apply {
            root.visibility = View.VISIBLE
            requestManager
                .load(postData!!.linkImage)
                .thumbnail(0.1f)
                .into(layoutFeedPostUrlIv)
            layoutFeedPostUrlTvTitle.text = postData!!.linkTitle
            layoutFeedPostUrlTvDesc.apply {
                if (postData!!.linkContent.isNullOrBlank()) visibility = View.GONE
                else text = postData!!.linkContent
            }
            layoutFeedPostUrlTvSite.apply {
                if (postData!!.linkSiteName.isNullOrBlank()) visibility = View.GONE
                else text = postData!!.linkSiteName
            }
            root.setSafeOnClickListener {
                listener.onLinkClick(postData!!.link, postData!!.linkTitle)
            }
        }
    }

    private fun setCodeBlock(editor: TextProcessor) {
        editor.apply {
            visibility = View.VISIBLE
            setEditor(postData!!.languageIdx!!)
            editorConfig.highlightCurrentLine = false
            setTextContent(postData!!.code!!)
            setSafeOnClickListener {
                listener.onCodeClick(postData!!.languageIdx!!, postData!!.code!!)
            }
        }
    }

    interface OnFeedItemClickListener {
        fun onProfileClick(userId: Int)
        fun onLinkClick(url: String?, title: String?)
        fun onCodeClick(langIdx: String, code: String)
        fun onContentClick(postId: Int)
        fun onLikeClick(
            postId: Int,
            isLiked: Boolean,
            position: Int,
            imageView: ImageView,
            textView: TextView
        )
        fun onBookmarkClick(post: Post, imageView: ImageView)
    }
}