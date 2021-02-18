package com.soulmates.valley.ui.post.read

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.brackeys.ui.editorkit.widget.TextProcessor
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.soulmates.valley.BR
import com.soulmates.valley.R
import com.soulmates.valley.base.BaseActivity
import com.soulmates.valley.base.BaseRecyclerViewAdapter
import com.soulmates.valley.data.model.post.Post
import com.soulmates.valley.databinding.ActivityPostDetailBinding
import com.soulmates.valley.databinding.ItemFeedPostImgBinding
import com.soulmates.valley.databinding.ItemFeedPostTagBinding
import com.soulmates.valley.databinding.LayoutFeedPostUrlBinding
import com.soulmates.valley.ui.initial.Loading
import com.soulmates.valley.ui.post.PostViewModel
import com.soulmates.valley.ui.post.write.code.CodeViewActivity
import com.soulmates.valley.ui.post.write.url.WebViewActivity
import com.soulmates.valley.ui.profile.ProfileActivity
import com.soulmates.valley.util.extension.*
import com.soulmates.valley.util.view.DividerItemDecorator
import com.soulmates.valley.util.view.HorizonItemDecorator
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel


class PostDetailActivity : BaseActivity<ActivityPostDetailBinding, PostViewModel>() {
    override val layoutResID: Int = R.layout.activity_post_detail
    override val viewModel: PostViewModel by viewModel()
    var commentRvAdapter: CommentRvAdapter? = null

    private var postId = -1
    private var userId = -1
    private var isLiked = false
    private var likeCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let {
            postId = it.getIntExtra("postId", -1)
        }
        viewDataBinding.vm = viewModel
        viewModel.getPostDetail(postId)
        observe()
        setCommentRv()
        setText()
        setClick()
    }

    private fun setText() {
        viewDataBinding.actPostDetailEtComment.apply {
            addTextChangedListener {
                if (!it.isNullOrBlank()) {
                    viewDataBinding.actPostDetailTvCommentButton.apply {
                        isEnabled = true
                        setBackgroundResource(R.drawable.solid_5_292a2c)
                        setTextColor(
                            ContextCompat.getColor(
                                this@PostDetailActivity,
                                R.color.main_green
                            )
                        )
                    }
                } else {
                    viewDataBinding.actPostDetailTvCommentButton.apply {
                        isEnabled = false
                        setBackgroundResource(R.drawable.solid_5_gray)
                        setTextColor(ContextCompat.getColor(this@PostDetailActivity, R.color.white))
                    }
                }
            }
        }

    }

    private fun setClick() {
        viewDataBinding.run {
            actPostDetailLayoutHeader.layoutHeaderBackIvBack.setSafeOnClickListener {
                finish()
            }

            actPostDetailClProfile.setSafeOnClickListener {
                startActivity(
                    Intent(this@PostDetailActivity, ProfileActivity::class.java)
                        .putExtra("userId", userId)
                )
            }

            actPostDetailTvCommentButton.setSafeOnClickListener {
                val jsonObject = JSONObject()
                jsonObject.put("postId", postId)
                jsonObject.put("content", actPostDetailEtComment.text.toString())
                val body = JsonParser.parseString(jsonObject.toString()) as JsonObject
                viewModel.postComment(body)
            }

            actPostDetailIvLike.setSafeOnClickListener {
                if (isLiked) viewModel.deleteLike(postId)
                else viewModel.postLike(postId)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observe() {
        viewModel.postDetail.observe(this, Observer {
            likeCount = it.likeCount
            userId = it.userId
            isLiked = it.isLiked

            viewDataBinding.actPostDetailLayoutHeader.layoutHeaderBackTvTitle.text =
                it.nickname + "님의 게시글"
            if (!it.images.isNullOrEmpty()) setImage(
                viewDataBinding.actPostDetailRvImg,
                viewDataBinding.actPostDetailIvImg,
                it
            )
            if (!it.link.isNullOrBlank()) setLink(viewDataBinding.actPostDetailLayoutUrl, it)
            if (!it.languageIdx.isNullOrBlank()) setCodeBlock(
                viewDataBinding.actPostDetailCodeEditor,
                it
            )
            if (!it.hashTag.isNullOrEmpty()) setTagRv(viewDataBinding.actPostDetailRvTag)

        })

        viewModel.getCommentList(postId).observe(this, Observer {
            commentRvAdapter?.submitList(it)
        })

        viewModel.commentData.observe(this, Observer {
            viewDataBinding.actPostDetailEtComment.text.clear()
            viewModel.refreshCommentList()
            Loading.showLoading(this)
            Handler().postDelayed({
                Loading.stopLoading()
                viewDataBinding.actPostDetailNsv.smoothScrollTo(
                    0,
                    viewDataBinding.actPostDetailNsv.getChildAt(0).height
                )
            }, 500)

        })

        viewModel.postLikeStatus.observe(this, Observer {
            isLiked = true
            viewDataBinding.actPostDetailTvLike.likeTextActive(++likeCount)
            viewDataBinding.actPostDetailIvLike.likeImageActive()

        })

        viewModel.deleteLikeStatus.observe(this, Observer {
            isLiked = false
            viewDataBinding.actPostDetailTvLike.likeTextInactive(--likeCount)
            viewDataBinding.actPostDetailIvLike.likeImageInactive()

        })
    }

    private val commentItemClickListener = object : CommentViewHolder.OnCommentItemClickListener {
        override fun onProfileClick(userId: Int) {
            startActivity(
                Intent(this@PostDetailActivity, ProfileActivity::class.java)
                    .putExtra("userId", userId)
            )
        }
    }

    private fun setCommentRv() {
        commentRvAdapter = CommentRvAdapter().apply {
            seCommentItemClickListener(commentItemClickListener)
            setHasStableIds(true)
        }
        viewDataBinding.actPostDetailRvComment.run {
            adapter = commentRvAdapter
            itemAnimator = null
            layoutManager = LinearLayoutManager(this@PostDetailActivity)
            if (itemDecorationCount == 0)
                addItemDecoration(
                    DividerItemDecorator(
                        ContextCompat.getDrawable(
                            this@PostDetailActivity,
                            R.drawable.stroke_1_464646
                        )
                    )
                )
        }
    }

    private fun setImage(rv: RecyclerView, iv: ImageView, postData: Post) {
        if (postData.images!!.size == 1) {
            iv.apply {
                visibility = View.VISIBLE
                iv.glide(postData.images[0], Glide.with(this@PostDetailActivity))
                setSafeOnClickListener {
                    startActivity(Intent(context, PostImageActivity::class.java).apply {
                        putExtra("imageList", postData.images)
                        putExtra("position", 0)
                    })
                }
            }
            (viewDataBinding.actPostDetailLayoutUrl.root.layoutParams as RelativeLayout.LayoutParams).addRule(
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
                            startActivity(Intent(context, PostImageActivity::class.java).apply {
                                putExtra("imageList", postData.images)
                                putExtra("position", position)
                            })
                        }
                    }
                }
                onFlingListener = null
                LinearSnapHelper().attachToRecyclerView(this)
                if (itemDecorationCount == 0) addItemDecoration(
                    HorizonItemDecorator(
                        postData.images.size,
                        10
                    )
                )
                (rv.adapter as BaseRecyclerViewAdapter<String, ItemFeedPostImgBinding>).run {
                    replaceAll(postData.images)
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun setLink(view: LayoutFeedPostUrlBinding, postData: Post) {
        view.apply {
            root.visibility = View.VISIBLE
            Glide.with(this@PostDetailActivity)
                .load(postData.linkImage)
                .thumbnail(0.1f)
                .into(layoutFeedPostUrlIv)
            layoutFeedPostUrlTvTitle.text = postData.linkTitle
            layoutFeedPostUrlTvDesc.apply {
                if (postData.linkContent.isNullOrBlank()) visibility = View.GONE
                else text = postData.linkContent
            }
            layoutFeedPostUrlTvSite.apply {
                if (postData.linkSiteName.isNullOrBlank()) visibility = View.GONE
                else text = postData.linkSiteName
            }
            root.setSafeOnClickListener {
                startActivity(
                    Intent(this@PostDetailActivity, WebViewActivity::class.java)
                        .putExtra("url", postData.link)
                        .putExtra("title", postData.linkTitle)
                )
            }
        }
    }

    private fun setCodeBlock(editor: TextProcessor, postData: Post) {
        editor.apply {
            visibility = View.VISIBLE
            setEditor(postData.languageIdx!!)
            editorConfig.highlightCurrentLine = false
            setTextContent(postData.code!!)
            setSafeOnClickListener {
                startActivity(
                    Intent(this@PostDetailActivity, CodeViewActivity::class.java)
                        .putExtra("langIdx", postData.languageIdx)
                        .putExtra("content", postData.code)
                        .putExtra("fromView", 2)
                )
            }
        }
    }

    private fun setTagRv(rv: RecyclerView) {
        rv.apply {
            visibility = View.VISIBLE
            adapter = object : BaseRecyclerViewAdapter<String, ItemFeedPostTagBinding>() {
                override val bindingVariableId: Int = BR.tag
                override val layoutResID: Int = R.layout.item_feed_post_tag
                override val listener: OnItemClickListener? = null
            }
            layoutManager = FlexboxLayoutManager(context).apply {
                flexWrap = FlexWrap.WRAP
                flexDirection = FlexDirection.ROW
            }
        }
    }
}