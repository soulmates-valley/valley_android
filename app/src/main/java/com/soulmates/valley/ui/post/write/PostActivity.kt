package com.soulmates.valley.ui.post.write

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.soulmates.valley.BR
import com.soulmates.valley.R
import com.soulmates.valley.base.BaseActivity
import com.soulmates.valley.base.BaseRecyclerViewAdapter
import com.soulmates.valley.base.dialogFragment.OneButtonDialogFragment
import com.soulmates.valley.data.model.post.PostAttachType
import com.soulmates.valley.databinding.ActivityPostBinding
import com.soulmates.valley.databinding.ItemAttachTypeBinding
import com.soulmates.valley.databinding.ItemPostTagBinding
import com.soulmates.valley.ui.initial.Loading
import com.soulmates.valley.ui.post.PostViewModel
import com.soulmates.valley.ui.post.write.code.BottomDialogFragment
import com.soulmates.valley.ui.post.write.code.CodeViewActivity
import com.soulmates.valley.ui.post.write.code.LanguagePickerDialogFragment
import com.soulmates.valley.ui.post.write.image.ImageRvAdapter
import com.soulmates.valley.ui.post.write.image.ImageViewHolder
import com.soulmates.valley.ui.post.write.url.UrlDialogFragment
import com.soulmates.valley.ui.post.write.url.WebViewActivity
import com.soulmates.valley.util.extension.*
import com.soulmates.valley.util.view.*
import gun0912.tedimagepicker.builder.TedRxImagePicker
import gun0912.tedimagepicker.builder.type.MediaType
import io.github.ponnamkarthik.richlinkpreview.MetaData
import io.github.ponnamkarthik.richlinkpreview.ResponseListener
import io.github.ponnamkarthik.richlinkpreview.RichPreview
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.File
import java.net.URI
import java.net.URLEncoder


class PostActivity : BaseActivity<ActivityPostBinding, PostViewModel>() {
    override val layoutResID: Int = R.layout.activity_post
    override val viewModel: PostViewModel by viewModel()

    private var imageRvAdapter: ImageRvAdapter? = null
    var hashTagList: ArrayList<String> = arrayListOf()
    private var imageUriList: ArrayList<Uri>? = null
    private var link: String? = ""
    private var linkTitle: String = ""
    private var linkImage: String = ""
    private var linkContent: String = ""
    private var linkSiteName: String = ""
    private var codeLangIdx = ""
    private var codeContent = ""
    var bundle = Bundle()
    val attachTypeList = arrayListOf(
        PostAttachType("사진", 1, false),
        PostAttachType("URL", 2, false),
        PostAttachType("코드", 3, false)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewDataBinding.vm = viewModel
        viewDataBinding.act = this

        setText()
        setClick()
        setRv()
        observe()
    }

    override fun onResume() {
        super.onResume()
        if (imageUriList == null || imageUriList!!.isEmpty()) {
            attachTypeList[0].isSelected = false
            (viewDataBinding.actPostRvAttachType.adapter as BaseRecyclerViewAdapter<PostAttachType, ItemAttachTypeBinding>).run {
                notifyItemChanged(0)
            }
        }
    }

    private fun setText() {
        viewDataBinding.run {
            actPostHeader.layoutHeaderCancelTvTitle.text = "게시글 작성"
            actPostLayoutButton.layoutButtonTv.text = "완료"
            actPostEtTag.hideKeyboardWhenFocusOut()
        }

        viewDataBinding.actPostEtContent.apply {
            hideKeyboardWhenFocusOut()
            addTextChangedListener {
                when {
                    it.isNullOrBlank() -> viewDataBinding.actPostLayoutButton.layoutButtonTv.buttonInactive(
                        5
                    )
                    it.length >= 1000 -> {
                        Toast.makeText(this@PostActivity, "최대 1000자까지 입력 가능합니다", Toast.LENGTH_SHORT)
                            .show()
                        viewDataBinding.actPostLayoutButton.layoutButtonTv.buttonActiveBlue(5)
                    }
                    else -> viewDataBinding.actPostLayoutButton.layoutButtonTv.buttonActiveBlue(5)
                }
            }
        }

        viewDataBinding.actPostEtTag.addTextChangedListener {
            if (it?.length!! >= 2) {
                val lastString = it.substring(it.length - 1)
                if (lastString.isBlank()) {
                    if (hashTagList.size == 5) {
                        Toast.makeText(this@PostActivity, "태그는 5개까지 입력 가능합니다", Toast.LENGTH_SHORT)
                            .show()
                        viewDataBinding.actPostEtTag.clearFocus()
                    } else {
                        val tag = it.substring(0, it.length - 1)

                        if (hashTagList.contains(tag)) Toast.makeText(
                            this@PostActivity,
                            "이미 입력된 태그입니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        else {
                            hashTagList.add(tag)
                            (viewDataBinding.actPostRvTag.adapter as BaseRecyclerViewAdapter<String, *>).run {
                                addItem(tag)
                                notifyItemInserted(hashTagList.size - 1)
                            }
                        }
                    }
                    viewDataBinding.actPostEtTag.apply {
                        setText("")
                        hint = ""
                    }
                }
            }
        }
    }

    private fun setClick() {
        viewDataBinding.run {
            actPostLayoutUrl.root.setSafeOnClickListener {
                startActivity(
                    Intent(this@PostActivity, WebViewActivity::class.java)
                        .putExtra("url", link)
                        .putExtra(
                            "title",
                            viewDataBinding.actPostLayoutUrl.layoutPostAttachUrlTvTitle.text
                        )
                )
            }

            actPostLayoutButton.root.setSafeOnClickListener {
                postPosting()
            }

            actPostHeader.layoutHeaderCancelIvCancel.setSafeOnClickListener { finish() }
        }
    }

    private fun setRv() {
        viewDataBinding.actPostRvAttachType.apply {
            adapter = object : BaseRecyclerViewAdapter<PostAttachType, ItemAttachTypeBinding>() {
                override val bindingVariableId: Int = BR.type
                override val layoutResID: Int = R.layout.item_attach_type
                override val listener: OnItemClickListener? = onAttachTypeItemClickListener
            }
            if (itemDecorationCount == 0) addItemDecoration(HorizonItemDecorator(3, 50))
        }

        imageRvAdapter = ImageRvAdapter().apply {
            setHasStableIds(true)
            setImageClickListener(onImageClickListener)
            replaceAll(imageUriList)
        }
        viewDataBinding.actPostLayoutImg.layoutPostAttachImgRv.adapter = imageRvAdapter


        viewDataBinding.actPostRvTag.apply {
            adapter = object : BaseRecyclerViewAdapter<String, ItemPostTagBinding>() {
                override val bindingVariableId: Int = BR.tag
                override val layoutResID: Int = R.layout.item_post_tag
                override val listener: OnItemClickListener? = onTagItemClickListener
            }
            layoutManager = FlexboxLayoutManager(this@PostActivity).apply {
                flexWrap = FlexWrap.WRAP
                flexDirection = FlexDirection.ROW
            }
        }
    }

    private val onAttachTypeItemClickListener =
        object : BaseRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClicked(item: Any?, position: Int?) {
                if (attachTypeList[position!!].isSelected) { // true -> false
                    when (position) {
                        0 -> { // 사진 선택
                            bottomDialog.setDialogDismissListener(object :
                                BottomDialogFragment.BottomDialogDismissListener {
                                @SuppressLint("CheckResult")
                                override fun onEditButtonClick() {
                                    showAlbum()
                                }

                                override fun onDeleteButtonClick() {
                                    changeAttachType(position)
                                    imageUriList?.clear()
                                    imageRvAdapter?.clearAll()
                                    viewDataBinding.actPostLayoutImg.root.slideUp()
                                }
                            })
                            bottomDialog.show(supportFragmentManager, null)
                        }
                        1 -> { // URL 선택
                            bottomDialog.setDialogDismissListener(object :
                                BottomDialogFragment.BottomDialogDismissListener {
                                override fun onEditButtonClick() {
                                    urlDialog.show(
                                        this@PostActivity.supportFragmentManager,
                                        "urlDialog"
                                    )
                                }

                                override fun onDeleteButtonClick() {
                                    link = ""
                                    linkTitle = ""
                                    linkImage = ""
                                    linkContent = ""
                                    linkSiteName = ""
                                    changeAttachType(position)
                                    viewDataBinding.actPostLayoutUrl.root.slideUp()
                                }
                            })
                            bottomDialog.show(supportFragmentManager, null)
                        }
                        2 -> { // 코드 선택
                            bottomDialog.setDialogDismissListener(object :
                                BottomDialogFragment.BottomDialogDismissListener {
                                override fun onEditButtonClick() {
                                    codeViewResultLauncher.launch(
                                        Intent(this@PostActivity, CodeViewActivity::class.java)
                                            .putExtra("langIdx", codeLangIdx)
                                            .putExtra("content", codeContent)
                                    )
                                }

                                override fun onDeleteButtonClick() {
                                    changeAttachType(position)
                                    codeContent = ""
                                    viewDataBinding.actPostLayoutCode.root.slideUp()
                                }
                            })
                            bottomDialog.show(supportFragmentManager, null)
                        }
                    }
                } else { // false -> true
                    changeAttachType(position)
                    when (position) {
                        0 -> showAlbum()
                        1 -> urlDialog.show(this@PostActivity.supportFragmentManager, "urlDialog")
                        2 -> {
                            LanguagePickerDialogFragment().apply {
                                setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog)
                                setOnDialogDismissedListener(onLangDialogFragmentDismissListener)
                                show(supportFragmentManager, "langPicker")
                            }
                        }
                    }
                }
            }
        }

    private val onLangDialogFragmentDismissListener =
        object : LanguagePickerDialogFragment.OnDialogDismissedListener {
            override fun onCancelButtonClick() {
                changeAttachType(2)
            }

            override fun onDialogDismissed(idx: String) {
                codeLangIdx = idx
                codeViewResultLauncher.launch(
                    Intent(this@PostActivity, CodeViewActivity::class.java)
                        .putExtra("langIdx", codeLangIdx)
                        .putExtra("fromView", 1)
                )
            }
        }

    private val onUrlDialogFragmentDismissListener =
        object : UrlDialogFragment.OnDialogDismissedListener {
            override fun onDialogDismissed(inputUrl: String?, isCancel: Boolean) {
                // url preview 보여주기
                if (!isCancel) {
                    RichPreview(object : ResponseListener {
                        override fun onData(metaData: MetaData) {
                            metaData.run {
                                link = this.url
                                linkTitle = this.title
                                linkImage = this.imageurl
                                this.description?.let {
                                    linkContent = if (it.length > 200) it.substring(0, 200)
                                    else it.substring(0)
                                }
                                linkSiteName = this.sitename
                            }
                            viewDataBinding.actPostLayoutUrl.apply {
                                layoutPostAttachUrlTvTitle.text = metaData.title
                                layoutPostAttachUrlIv.glide(
                                    metaData.imageurl,
                                    Glide.with(this@PostActivity)
                                )
                                layoutPostAttachUrlTvDesc.text = metaData.sitename
                            }
                        }

                        override fun onError(e: Exception) {}
                    }).getPreview(inputUrl)
                    Loading.showLoading(this@PostActivity)
                    Handler().postDelayed({
                        Loading.stopLoading()
                        viewDataBinding.actPostLayoutUrl.root.slideDown()
                    }, 1000)
                } else {
                    if (link.isNullOrBlank()) changeAttachType(1)
                }
            }
        }


    val urlDialog = UrlDialogFragment().apply {
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog)
        setOnDialogDismissedListener(onUrlDialogFragmentDismissListener)
    }

    val bottomDialog = BottomDialogFragment().apply {
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog)
    }

    val onImageClickListener = object : ImageViewHolder.OnImageClickListener {
        override fun onDeleteItem(position: Int) {
            imageUriList!!.removeAt(position)
            if (imageUriList!!.size == 0) {
                imageRvAdapter!!.clearAll()
                changeAttachType(0)
                viewDataBinding.actPostLayoutImg.root.slideUp()
            } else imageRvAdapter!!.replaceAll(imageUriList)
        }
    }

    var codeViewResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intentCode = result.data?.getStringExtra("codeContent")
                if (!intentCode.isNullOrBlank()) {
                    viewDataBinding.actPostLayoutCode.root.slideDown()
                    viewDataBinding.actPostLayoutCode.layoutPostAttachCodeEditor.run {
                        setEditor(codeLangIdx)
                        editorConfig.highlightCurrentLine = false
                        setTextContent(intentCode)
                    }
                    codeContent = intentCode
                } else if (codeContent.isBlank() && intentCode.isNullOrBlank()) {
                    changeAttachType(2)
                    viewDataBinding.actPostLayoutCode.root.slideUp()
                }
            }
        }

    val onTagItemClickListener = object : BaseRecyclerViewAdapter.OnItemClickListener {
        override fun onItemClicked(item: Any?, position: Int?) {
            hashTagList.removeAt(position!!)
            (viewDataBinding.actPostRvTag.adapter as BaseRecyclerViewAdapter<String, *>).run {
                removeItem(position)
                notifyItemRemoved(position)
            }
        }
    }

    private fun observe() {
        viewModel.postStatus.observe(this@PostActivity, Observer {
            if (it == 200) {
                val dialog = OneButtonDialogFragment.newInstance("게시글이 등록되었습니다.", "확인")
                dialog.setDialogDismissListener(object :
                    OneButtonDialogFragment.OneButtonDialogDismissListener {
                    override fun buttonClick() { // 게시글 작성 완료 다이얼로그 띄우고 프로필로 이동
                        dialog.dismiss()
                        finish()
                    }

                    override fun onDialogDismissed() {}
                })
                dialog.show(supportFragmentManager, null)
            }
        })

        viewModel.isProgress.observe(this, Observer {
            if (it) Loading.showLoading(this)
            else Loading.stopLoading()
        })
    }

    private fun changeAttachType(position: Int) {
        attachTypeList[position].isSelected = !attachTypeList[position].isSelected
        (viewDataBinding.actPostRvAttachType.adapter as BaseRecyclerViewAdapter<PostAttachType, ItemAttachTypeBinding>).run {
            notifyItemChanged(position)
        }
    }

    private fun postPosting() {
        val map: HashMap<String, RequestBody?> = HashMap()
        map.run {
            put("content", makeRequestBody(viewDataBinding.actPostEtContent.text.toString()))
            put("link", makeRequestBody(link))
            put("linkImage", makeRequestBody(linkImage))
            put("linkTitle", makeRequestBody(linkTitle))
            put("linkSiteName", makeRequestBody(linkSiteName))
            put("linkContent", makeRequestBody(linkContent))
            put("codeType", makeRequestBody(codeLangIdx))
            put("code", makeRequestBody(codeContent))
        }

        val images: ArrayList<MultipartBody.Part> = ArrayList()
        imageUriList?.let {
            for (item in imageUriList!!) images.add(makeMultipartBody(item.toString()))
        }

        viewModel.postPosting(map, hashTagList, images)
    }

    private fun makeRequestBody(value: String?): RequestBody? {
        value?.let { return RequestBody.create(okhttp3.MediaType.parse("text/plain"), value) }
        return null
    }

    private fun makeMultipartBody(url: String): MultipartBody.Part {
        val uri = URI.create(url)
        val file = File(uri.path)
        val requestFile = RequestBody.create(okhttp3.MediaType.parse("multipart/form-data"), file)
        return MultipartBody.Part.createFormData(
            "images",
            URLEncoder.encode(file.name, "UTF-8"),
            requestFile
        )
    }

    @SuppressLint("CheckResult")
    private fun showAlbum() {
        TedRxImagePicker.with(this@PostActivity)
            .mediaType(MediaType.IMAGE)
            .buttonTextColor(R.color.black)
            .min(0, "")
            .max(8, "사진은 최대 8장까지 선택 가능합니다")
            .startMultiImage()
            .subscribe(
                this@PostActivity::showMultiImage,
                Throwable::printStackTrace
            )
    }

    private fun showMultiImage(uriList: List<Uri>) {
        this.imageUriList = ArrayList(uriList)
        if (uriList.isEmpty()) {
            changeAttachType(0)
            return
        }
        // image list 나타내기
        viewDataBinding.actPostLayoutImg.root.slideDown()
        imageRvAdapter!!.replaceAll(uriList)
    }
}