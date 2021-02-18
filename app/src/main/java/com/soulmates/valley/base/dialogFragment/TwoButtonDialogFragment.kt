package com.soulmates.valley.base.dialogFragment

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import com.soulmates.valley.R
import com.soulmates.valley.databinding.DialogFragmentTwoButtonBinding

class TwoButtonDialogFragment : BaseDialogFragment<DialogFragmentTwoButtonBinding>(){

    override val layoutResID: Int
        get() = R.layout.dialog_fragment_two_button

    private lateinit var listener : TwoButtonDialogDismissListener

    override fun dismiss() {
        listener.onDialogDismissed()
        super.dismiss()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.run{
            setText(
                getString("content")!!,
                getString("leftText")!!,
                getString("rightText")!!
            )
        }
        setButton()
    }

    override fun onResume() {
        super.onResume()
        dialog!!.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (event.action != KeyEvent.ACTION_DOWN) true
                else {
                    listener.onDialogDismissed()
                    dismiss()
                    true
                }
            } else false
        }
    }


    private fun setText(content : String, leftText : String, rightText : String){
        viewDataBinding.dialFragTwoButtonTvContent.text = content
        viewDataBinding.dialFragTwoButtonTvLeft.text = leftText
        viewDataBinding.dialFragTwoButtonTvRight.text = rightText
    }

    private fun setButton(){
        viewDataBinding.dialFragTwoButtonTvLeft.setOnClickListener {
            listener.onLeftButtonClick()
        }

        viewDataBinding.dialFragTwoButtonTvRight.setOnClickListener {
            listener.onRightButtonClick()
        }
    }

    interface TwoButtonDialogDismissListener {
        fun onLeftButtonClick()
        fun onRightButtonClick()
        fun onDialogDismissed()
    }

    fun setDialogDismissListener(listener : TwoButtonDialogDismissListener){
        this.listener = listener
    }

    companion object {
        fun newInstance(content : String, leftText : String, rightText : String): TwoButtonDialogFragment {
            val fragment = TwoButtonDialogFragment()
            val bundle = Bundle()
            bundle.putString("content", content)
            bundle.putString("leftText", leftText)
            bundle.putString("rightText", rightText)
            fragment.arguments = bundle
            return fragment
        }
    }

}