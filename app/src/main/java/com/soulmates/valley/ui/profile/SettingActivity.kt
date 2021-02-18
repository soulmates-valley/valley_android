package com.soulmates.valley.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.soulmates.valley.R
import com.soulmates.valley.base.BaseActivity
import com.soulmates.valley.base.dialogFragment.TwoButtonDialogFragment
import com.soulmates.valley.databinding.ActivitySettingBinding
import com.soulmates.valley.util.extension.setSafeOnClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingActivity : BaseActivity<ActivitySettingBinding, ProfileViewModel>(){
    override val layoutResID: Int = R.layout.activity_setting
    override val viewModel: ProfileViewModel by viewModel()

    lateinit var logoutDialog : TwoButtonDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setClick()
        setText()

        viewModel.logoutStatus.observe(this, Observer {
            startActivity(Intent(this, it.java))
            finishAffinity()
        })
    }

    private fun setText(){
        viewDataBinding.actSettingLayoutaHeader.layoutHeaderBackTvTitle.text = "설정"
    }

    private fun setClick(){
        viewDataBinding.actSettingLlLogout.setSafeOnClickListener {
            logoutDialog = TwoButtonDialogFragment.newInstance("로그아웃 하시겠습니까?", "취소", "확인")
            logoutDialog.apply {
                setDialogDismissListener(dialogDismissListener)
                show(supportFragmentManager, "logout")
            }
        }
    }

    private val dialogDismissListener = object : TwoButtonDialogFragment.TwoButtonDialogDismissListener{
        override fun onLeftButtonClick() { logoutDialog.dismiss() }

        override fun onRightButtonClick() { viewModel.logout() }

        override fun onDialogDismissed() {}
    }
}