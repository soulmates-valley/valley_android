package com.soulmates.valley.ui.post.write.code

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.soulmates.valley.R
import com.soulmates.valley.base.BaseActivity
import com.soulmates.valley.data.model.post.LanguageList.languageMap
import com.soulmates.valley.databinding.ActivityCodeViewBinding
import com.soulmates.valley.ui.post.PostViewModel
import com.soulmates.valley.util.extension.setSafeOnClickListener
import com.soulmates.valley.util.extension.buttonActiveBlue
import com.soulmates.valley.util.extension.buttonInactive
import com.soulmates.valley.util.extension.setEditor
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class CodeViewActivity : BaseActivity<ActivityCodeViewBinding, PostViewModel>() {
    override val layoutResID: Int = R.layout.activity_code_view
    override val viewModel: PostViewModel by viewModel()
    private var fromView: Int = 0 //code view activity를 실행시킨 뷰 (1: PostActivity(쓰기), 2:FeedFragment 등 (읽기))
    private var langIdx = ""
    private var content: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fromView = intent.getIntExtra("fromView", 0)
        langIdx = intent.getStringExtra("langIdx")
        content = intent.getStringExtra("content")

        setText()
        setClick()
        setEditor()
    }

    @SuppressLint("SetTextI18n")
    private fun setText() {
        viewDataBinding.actCodeViewHeader.layoutHeaderBackTvTitle.text = "${languageMap[langIdx.toInt()]?.langName} 코드"
        viewDataBinding.actCodeViewHeaderButton.layoutButtonTv.text = "확인"

        viewDataBinding.actCodeViewEditor.addTextChangedListener {
            if (!it.isNullOrBlank()) viewDataBinding.actCodeViewHeaderButton.layoutButtonTv.buttonActiveBlue(5)
            else viewDataBinding.actCodeViewHeaderButton.layoutButtonTv.buttonInactive(5)
        }
    }

    private fun setClick() {
        val intent = Intent()
        viewDataBinding.actCodeViewHeader.layoutHeaderBackIvBack.setSafeOnClickListener {
            viewDataBinding.actCodeViewEditor.text?.let {
                intent.putExtra("codeContent", "")
                setResult(Activity.RESULT_OK, intent)
            }
            finish()
        }
        viewDataBinding.actCodeViewHeaderButton.layoutButtonTv.setSafeOnClickListener {
            intent.putExtra("codeContent", viewDataBinding.actCodeViewEditor.text.toString())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun setEditor() {
        viewDataBinding.actCodeViewEditor.apply {
            setEditor(langIdx)
            if (content.isNullOrBlank()) setTextContent("")
            else setTextContent(content!!)
            if(fromView==2){
                editorConfig.highlightCurrentLine = false
                viewDataBinding.actCodeViewHeaderButton.root.visibility = View.GONE
                isCursorVisible = false
                isFocusableInTouchMode = false
                isFocusable = false
            }
        }
    }
}