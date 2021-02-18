package com.soulmates.valley.ui.post.write.code

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.soulmates.valley.R
import com.soulmates.valley.base.dialogFragment.BaseDialogFragment
import com.soulmates.valley.data.model.post.LanguageList.languageMap
import com.soulmates.valley.databinding.DialogFragmentLanguagePickerBinding
import com.soulmates.valley.util.extension.setSafeOnClickListener
import timber.log.Timber

class LanguagePickerDialogFragment : BaseDialogFragment<DialogFragmentLanguagePickerBinding>() {
    override val layoutResID: Int = R.layout.dialog_fragment_language_picker
    lateinit var listener : OnDialogDismissedListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setCanceledOnTouchOutside(true)
        setClick()
        setPicker()
    }

    private fun setClick(){
        viewDataBinding.run {
            dialFragLangIvCancel.setSafeOnClickListener {
                listener.onCancelButtonClick()
                dismiss()
            }
            dialFragLangTv.setSafeOnClickListener {
                listener.onDialogDismissed(languageMap[viewDataBinding.dialFragLangNp.value+1]?.langIdx!!)
                dismiss()
            }
        }
    }

    private fun setPicker() {
        val nameArray = arrayOfNulls<String>(languageMap.size)
        for (j in 0 until languageMap.size) {
            nameArray[j] = languageMap[j+1]?.langName
        }

        viewDataBinding.dialFragLangNp.apply {
            wrapSelectorWheel = false
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            minValue = 0
            maxValue = languageMap.size-1
            displayedValues = nameArray
        }
    }

    fun setOnDialogDismissedListener(listener: OnDialogDismissedListener) {
        this.listener = listener
    }

    interface OnDialogDismissedListener {
        fun onCancelButtonClick()
        fun onDialogDismissed(idx: String)
    }
}