package com.soulmates.valley.ui.signUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.soulmates.valley.R
import com.soulmates.valley.base.dialogFragment.BaseDialogFragment
import com.soulmates.valley.databinding.DialogFragmentBirthPickerBinding
import com.soulmates.valley.util.extension.setSafeOnClickListener
import java.util.*

class BirthPickerDialogFragment : BaseDialogFragment<DialogFragmentBirthPickerBinding>() {
    override val layoutResID: Int = R.layout.dialog_fragment_birth_picker

    var calendar: Calendar = Calendar.getInstance()
    private val MIN_YEAR = 1900
    private val MAX_YEAR = calendar.get(Calendar.YEAR)
    var selectYear = calendar.get(Calendar.YEAR)
    lateinit var listener : BirthPickerDialogDismissListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setCanceledOnTouchOutside(true)
        setPicker()
    }

    private fun setPicker() {
        viewDataBinding.dialFragBirthNp.apply {
            wrapSelectorWheel = false
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            minValue = MIN_YEAR
            maxValue = MAX_YEAR
            value = selectYear
        }

        viewDataBinding.dialFragBirthTv.setSafeOnClickListener {
            selectYear = viewDataBinding.dialFragBirthNp.value
            dismiss()
        }
    }

    fun setDialogDismissListener(listener: BirthPickerDialogDismissListener) {
        this.listener = listener
    }

    interface BirthPickerDialogDismissListener {
        fun onDialogDismissed(year: String)
    }

    override fun dismiss() {
        listener.onDialogDismissed(viewDataBinding.dialFragBirthNp.value.toString())
        super.dismiss()
    }
}