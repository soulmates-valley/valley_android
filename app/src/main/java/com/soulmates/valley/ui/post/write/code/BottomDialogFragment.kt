package com.soulmates.valley.ui.post.write.code

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.soulmates.valley.R
import com.soulmates.valley.databinding.DialogFragmentBottomBinding
import com.soulmates.valley.util.extension.setSafeOnClickListener

class BottomDialogFragment : BottomSheetDialogFragment() {
    private lateinit var viewDataBinding: DialogFragmentBottomBinding
    private lateinit var listener : BottomDialogDismissListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_fragment_bottom,
            container,
            false
        )
        viewDataBinding.lifecycleOwner = this
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setButton()
    }

    override fun onResume() {
        super.onResume()

        dialog!!.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (event.action != KeyEvent.ACTION_DOWN) true
                else {
                    dismiss()
                    true
                }
            } else false
        }
    }

    private fun setButton(){
        viewDataBinding.dialFragBottomTvEdit.setSafeOnClickListener {
            listener.onEditButtonClick()
            dismiss()
        }

        viewDataBinding.dialFragBottomTvDelete.setSafeOnClickListener {
            listener.onDeleteButtonClick()
            dismiss()
        }

        viewDataBinding.dialFragBottomTvCancel.setSafeOnClickListener {
            dismiss()
        }
    }

    interface BottomDialogDismissListener {
        fun onEditButtonClick()
        fun onDeleteButtonClick()
    }

    fun setDialogDismissListener(listener : BottomDialogDismissListener){
        this.listener = listener
    }
}