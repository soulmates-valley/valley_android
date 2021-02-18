package com.soulmates.valley.util.extension

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.brackeys.ui.editorkit.model.EditorConfig
import com.brackeys.ui.editorkit.theme.EditorTheme
import com.brackeys.ui.editorkit.widget.TextProcessor
import com.brackeys.ui.language.c.CLanguage
import com.brackeys.ui.language.cpp.CppLanguage
import com.brackeys.ui.language.csharp.CSharpLanguage
import com.brackeys.ui.language.html.HtmlLanguage
import com.brackeys.ui.language.java.JavaLanguage
import com.brackeys.ui.language.javascript.JavaScriptLanguage
import com.brackeys.ui.language.kotlin.KotlinLanguage
import com.brackeys.ui.language.markdown.MarkdownLanguage
import com.brackeys.ui.language.plaintext.PlainTextLanguage
import com.brackeys.ui.language.python.PythonLanguage
import com.brackeys.ui.language.shell.ShellLanguage
import com.brackeys.ui.language.sql.SqlLanguage
import com.brackeys.ui.language.visualbasic.VisualBasicLanguage
import com.brackeys.ui.language.xml.XmlLanguage
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.soulmates.valley.R
import com.soulmates.valley.ValleyApplication.Companion.globalApplication
import com.soulmates.valley.util.etc.NetworkStatus

fun ImageView.glide(imageUrl: String?, requestManager: RequestManager){
    val requestOptions = RequestOptions
        .skipMemoryCacheOf(false)//memory cache 사용
        .diskCacheStrategy(DiskCacheStrategy.NONE)//disk cache 사용하지 않음

    requestManager
        .load(imageUrl)
        .thumbnail(0.1f)
        .error(R.drawable.img_url_default)
        .apply(requestOptions)
        .into(this)
}

fun ImageView.likeImageActive(){
    setImageDrawable(
        ContextCompat.getDrawable(
            context,
            R.drawable.ic_like_on
        )
    )
}

fun ImageView.likeImageInactive(){
    setImageDrawable(
        ContextCompat.getDrawable(
            context,
            R.drawable.ic_like_off
        )
    )
}

fun TextView.likeTextActive(likeCount: Int){
    text = "좋아요 ${likeCount}개"
    setTextColor(
        ContextCompat.getColor(
            context,
            R.color.sub_red
        )
    )
}

fun TextView.likeTextInactive(likeCount: Int){
    text = "좋아요 ${likeCount}개"
    setTextColor(ContextCompat.getColor(context, R.color.white))
}

fun TextView.buttonActiveGreen(radius: Int){
    when(radius){
        5 -> setBackgroundResource(R.drawable.solid_5_main_green)
        10 -> setBackgroundResource(R.drawable.solid_10_main_green)
    }
    isEnabled = true
}

fun TextView.buttonActiveBlue(radius: Int){
    when(radius){
        5 -> setBackgroundResource(R.drawable.solid_5_sub_blue)
    }
    isEnabled = true
}

fun TextView.buttonInactive(radius: Int){
    when(radius){
        5 -> setBackgroundResource(R.drawable.solid_5_dark_gray)
        10 -> setBackgroundResource(R.drawable.solid_10_dark_gray)
    }
    isEnabled = false
}

fun EditText.hideKeyboardWhenFocusOut(){
    onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
        if (!hasFocus) {
            val inputMethodManager = view.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}

fun View.slideDown(){
    val slideDown: Animation = AnimationUtils.loadAnimation(globalApplication, R.anim.slide_to_down)
    if (visibility == View.GONE) {
        visibility = View.VISIBLE
        startAnimation(slideDown)
    }
}

fun View.slideUp(){
    val slideUp: Animation = AnimationUtils.loadAnimation(globalApplication, R.anim.slide_to_up)
    if (visibility == View.VISIBLE) {
        startAnimation(slideUp)
        Handler().postDelayed({
            visibility = View.GONE
        }, 250)
    }
}

fun TextProcessor.setEditor(langIdx: String){
    apply {
        colorScheme = EditorTheme.DARCULA
        language =
            when (langIdx) {
                "1" -> CLanguage()
                "2" -> CppLanguage()
                "3" -> CSharpLanguage()
                "4" -> HtmlLanguage()
                "5" -> JavaLanguage()
                "6" -> JavaScriptLanguage()
                "7" -> KotlinLanguage()
                "8" -> PythonLanguage()
                "9" -> XmlLanguage()
                "10" -> MarkdownLanguage()
                "11" -> ShellLanguage()
                "12" -> SqlLanguage()
                "13" -> VisualBasicLanguage()
                else -> PlainTextLanguage()
            }
        editorConfig = EditorConfig(
            fontSize = 12f
        )
    }
}

fun checkNetworkConnection(context: Context): Boolean {
    return when (NetworkStatus.getConnectivityStatus(context)) {
        NetworkStatus.TYPE_MOBILE -> true
        NetworkStatus.TYPE_WIFI -> true
        else -> false
    }
}
