package com.soulmates.valley.ui.signUp

import android.os.Bundle
import android.view.View
import com.soulmates.valley.R
import com.soulmates.valley.base.dialogFragment.BaseDialogFragment
import com.soulmates.valley.databinding.DialogFragmentJoinSuccessBinding

class JoinSuccessDialogFragment : BaseDialogFragment<DialogFragmentJoinSuccessBinding>() {
    override val layoutResID: Int = R.layout.dialog_fragment_join_success

    private lateinit var listener : JoinSuccessDialogDismissListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButton()
    }

    override fun dismiss() {
        listener.onDialogDismissed()
        super.dismiss()
    }

    private fun setButton(){
        viewDataBinding.dialFragJoinSuccessTvConfirm.setOnClickListener {
            listener.buttonClick()
            dismiss()
        }
    }

    interface JoinSuccessDialogDismissListener {

        fun buttonClick()
        fun onDialogDismissed()
    }

    fun setDialogDismissListener(listener : JoinSuccessDialogDismissListener){
        this.listener = listener
    }

}