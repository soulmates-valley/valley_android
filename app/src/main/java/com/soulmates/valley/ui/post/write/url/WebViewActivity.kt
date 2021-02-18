package com.soulmates.valley.ui.post.write.url

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.webkit.*
import android.widget.Toast
import com.soulmates.valley.R
import com.soulmates.valley.base.BaseActivity
import com.soulmates.valley.databinding.ActivityWebViewBinding
import com.soulmates.valley.ui.post.PostViewModel
import com.soulmates.valley.util.extension.setSafeOnClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class WebViewActivity : BaseActivity<ActivityWebViewBinding, PostViewModel>() {
    override val layoutResID: Int = R.layout.activity_web_view
    override val viewModel: PostViewModel by viewModel()
    private var url = ""
    private var title = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        url = intent.getStringExtra("url")!!
        title = intent.getStringExtra("title")!!

        setText()
        setClick()
    }

    private fun setText(){
        viewDataBinding.actWebViewWv.loadUrl(url)
        viewDataBinding.actWebViewHeader.layoutHeaderBackTvTitle.text = title

        viewDataBinding.actWebViewWv.apply{
            settings.run {
                javaScriptEnabled = true
                domStorageEnabled = true
                useWideViewPort = true
            }

            webChromeClient = WebChromeClient()

            webViewClient = object : WebViewClient(){
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    viewDataBinding.actWebViewPb.visibility = View.VISIBLE
                    return super.shouldOverrideUrlLoading(view, request)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    viewDataBinding.actWebViewPb.visibility = View.GONE
                    super.onPageFinished(view, url)
                }
            }
        }
    }

    private fun setClick(){
        viewDataBinding.actWebViewHeader.layoutHeaderBackIvBack.setSafeOnClickListener { finish() }
    }
}