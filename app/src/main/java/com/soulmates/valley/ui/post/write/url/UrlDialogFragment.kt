package com.soulmates.valley.ui.post.write.url

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.URLUtil
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.soulmates.valley.R
import com.soulmates.valley.databinding.DialogFragmentUrlBinding
import com.soulmates.valley.util.extension.setSafeOnClickListener


class UrlDialogFragment : DialogFragment() {
    lateinit var viewDataBinding: DialogFragmentUrlBinding
    lateinit var listener: OnDialogDismissedListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_fragment_url, container, false)
        viewDataBinding.lifecycleOwner = this
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // keyboard 올라오면 dialog resize
        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        setClick()
    }

    private fun setClick() {
        viewDataBinding.dialFragIvCancel.setSafeOnClickListener {
            viewDataBinding.dialFragUrlEt.setText("")
            listener.onDialogDismissed("", true)
            dismiss()
        }

        viewDataBinding.dialFragUrlTvConfirm.setSafeOnClickListener {
            val inputUrl = viewDataBinding.dialFragUrlEt.text.toString()

            if (!isURL(inputUrl)) {
                Toast.makeText(activity, "유효하지 않은 URL 입니다", Toast.LENGTH_SHORT).show()
            }
            else {
                viewDataBinding.dialFragUrlEt.setText("")
                listener.onDialogDismissed(inputUrl, false)
                dismiss()
            }
        }
    }

    private fun isURL(url: String): Boolean {
        return URLUtil.isValidUrl(url) && Patterns.WEB_URL.matcher(url).matches()
    }

    fun setOnDialogDismissedListener(listener: OnDialogDismissedListener) {
        this.listener = listener
    }

    interface OnDialogDismissedListener {
        fun onDialogDismissed(inputUrl: String?, isCancel: Boolean)
    }
}