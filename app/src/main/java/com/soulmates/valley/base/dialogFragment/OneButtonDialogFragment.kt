package com.soulmates.valley.base.dialogFragment

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import com.soulmates.valley.R
import com.soulmates.valley.databinding.DialogFragmentOneButtonBinding

class OneButtonDialogFragment : BaseDialogFragment<DialogFragmentOneButtonBinding>(){

    override val layoutResID: Int
        get() = R.layout.dialog_fragment_one_button

    private lateinit var listener : OneButtonDialogDismissListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.run{
            setText(
                getString("content")!!,
                getString("text")!!
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

    override fun dismiss() {
        listener.onDialogDismissed()
        super.dismiss()
    }

    private fun setText(content : String, text : String){
        viewDataBinding.dialFragOneButtonTvContent.text = content
        viewDataBinding.dialFragOneButtonTv.text = text
    }

    private fun setButton(){
        viewDataBinding.dialFragOneButtonTv.setOnClickListener {
            listener.buttonClick()
            dismiss()
        }
    }

    interface OneButtonDialogDismissListener {
        fun buttonClick()
        fun onDialogDismissed()
    }

    fun setDialogDismissListener(listener : OneButtonDialogDismissListener){
        this.listener = listener
    }

    companion object {
        fun newInstance(content : String, text : String): OneButtonDialogFragment {
            val fragment = OneButtonDialogFragment()
            val bundle = Bundle()
            bundle.putString("content", content)
            bundle.putString("text", text)
            fragment.arguments = bundle
            return fragment
        }
    }

}