package com.soulmates.valley.ui.profile

import com.soulmates.valley.data.local.room.entity.BookmarkPost
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
import com.soulmates.valley.databinding.ItemBookmarkPostBinding
import com.soulmates.valley.databinding.ItemFeedPostImgBinding
import com.soulmates.valley.databinding.LayoutFeedPostUrlBinding
import com.soulmates.valley.ui.post.read.PostImageActivity
import com.soulmates.valley.util.extension.*
import com.soulmates.valley.util.view.HorizonItemDecorator


class BookmarkPostAdapter : PagedListAdapter<BookmarkPost, BookmarkViewHolder>(diffCallback) {
    lateinit var binding: ItemBookmarkPostBinding
    private var listener: BookmarkViewHolder.OnBookmarkItemClickListener? = null
    private var requestManager: RequestManager? = null
    var data = mutableListOf<BookmarkPost>()

    fun setFeedItemClickListener(listener: BookmarkViewHolder.OnBookmarkItemClickListener) {
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

    override fun submitList(pagedList: PagedList<BookmarkPost>?) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        binding = ItemBookmarkPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookmarkViewHolder(binding, listener!!, requestManager!!)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.bind(getItem(position))
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<BookmarkPost>() {
            override fun areItemsTheSame(oldItem: BookmarkPost, newItem: BookmarkPost): Boolean =
                oldItem.postId == newItem.postId

            override fun areContentsTheSame(oldItem: BookmarkPost, newItem: BookmarkPost): Boolean =
                oldItem == newItem
        }
    }
}

class BookmarkViewHolder(
    val viewDataBinding: ItemBookmarkPostBinding,
    val listener: OnBookmarkItemClickListener, var requestManager: RequestManager
) : RecyclerView.ViewHolder(viewDataBinding.root) {

    private var postData: BookmarkPost? = null

    init {
        viewDataBinding.run {
            itemBookmarkPostClProfile.setSafeOnClickListener {
                listener.onProfileClick(postData!!.userId)
            }
            itemBookmarkPostRlContent.setSafeOnClickListener {
                listener.onContentClick(postData!!.postId)
            }
            itemBookmarkPostIvBookmark.setSafeOnClickListener {
                listener.onBookmarkClick(postData!!, itemBookmarkPostIvBookmark)
            }
        }
    }

    fun bind(data: BookmarkPost?) {
        postData = data
        viewDataBinding.bookmarkPost = postData

        setReadMore()
        when {
            !postData!!.images.isNullOrEmpty() -> this.setImage(
                viewDataBinding.itemBookmarkPostRvImg,
                viewDataBinding.itemBookmarkPostIvImg,
                postData!!.images!!
            )
            !postData!!.link.isNullOrBlank() -> setLink(viewDataBinding.itemBookmarkPostLayoutUrl)
            !postData!!.languageIdx.isNullOrBlank() -> setCodeBlock(viewDataBinding.itemBookmarkPostCodeEditor)
        }
        viewDataBinding.executePendingBindings()
    }

    private fun setReadMore() {
        viewDataBinding.itemBookmarkPostTvContent.run {
            this.post {
                if (layout.getEllipsisCount(lineCount - 1) <= 0) {
                    viewDataBinding.itemBookmarkPostTvMore.visibility = View.GONE
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
                        putExtra("imageList", postData!!.images as ArrayList<String>)
                        putExtra("position", 0)
                    })
                }
            }
            (viewDataBinding.itemBookmarkPostLayoutUrl.root.layoutParams as RelativeLayout.LayoutParams).addRule(
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
                                    putExtra("imageList", postData!!.images as ArrayList<String>)
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

    interface OnBookmarkItemClickListener {
        fun onProfileClick(userId: Int)
        fun onLinkClick(url: String?, title: String?)
        fun onCodeClick(langIdx: String, code: String)
        fun onContentClick(postId: Int)
        fun onBookmarkClick(post: BookmarkPost, imageView: ImageView)
    }
}