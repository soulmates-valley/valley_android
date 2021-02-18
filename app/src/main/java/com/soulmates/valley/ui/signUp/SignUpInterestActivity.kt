package com.soulmates.valley.ui.signUp

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.soulmates.valley.BR
import com.soulmates.valley.R
import com.soulmates.valley.base.BaseActivity
import com.soulmates.valley.base.BaseRecyclerViewAdapter
import com.soulmates.valley.base.dialogFragment.OneButtonDialogFragment
import com.soulmates.valley.data.model.user.Interest
import com.soulmates.valley.data.model.user.InterestList
import com.soulmates.valley.databinding.ActivitySignUpInterestBinding
import com.soulmates.valley.databinding.ItemInterestBinding
import com.soulmates.valley.ui.initial.InitialActivity
import com.soulmates.valley.util.extension.setSafeOnClickListener
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel


class SignUpInterestActivity :
    BaseActivity<ActivitySignUpInterestBinding, SignUpViewModel>() {
    override val layoutResID: Int = R.layout.activity_sign_up_interest
    override val viewModel: SignUpViewModel by viewModel()
    var interest = arrayListOf<Int>()
    var positionList: ArrayList<Interest> = InterestList.positionList
    var techList: ArrayList<Interest> = InterestList.techList
    var etcList: ArrayList<Interest> = InterestList.etcList
    private lateinit var jsonObject: JSONObject
    var profileImg = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        jsonObject = JSONObject(intent.getStringExtra("json")!!)
        intent.getStringExtra("profileImg")?.let { profileImg = it }
        viewDataBinding.activity = this

        observe()
        setClick()
        setRv()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val focusView = currentFocus
        if (focusView != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev.x.toInt()
            val y = ev.y.toInt()
            if (!rect.contains(x, y)) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(focusView.windowToken, 0)
                focusView.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun observe() {
        viewModel.signUpStatus.observe(this, Observer {
            when (it) {
                200 -> {
                    JoinSuccessDialogFragment().apply {
                        setDialogDismissListener(joinSuccessDismissListener)
                        show(this@SignUpInterestActivity.supportFragmentManager, "joinSuccess")
                    }
                }
                else -> {
                    Toast.makeText(this, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private val joinSuccessDismissListener =
        object : JoinSuccessDialogFragment.JoinSuccessDialogDismissListener {
            override fun buttonClick() {}
            override fun onDialogDismissed() {
                val intent = Intent(this@SignUpInterestActivity, InitialActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
        }

    private fun setClick() {
        viewDataBinding.actSignUpInterestHeader.layoutHeaderBackIvBack.setSafeOnClickListener { finish() }
        viewDataBinding.actSignUpInterestHeader.layoutHeaderBackTvTitle.text = "회원가입"

        viewDataBinding.actSignUpInterestTvComplete.setSafeOnClickListener {
            if (!combineInterest()) {
                Toast.makeText(this, "관심사를 3개 이상 10개 이하로 선택해주세요", Toast.LENGTH_SHORT).show()
            } else {
                combineInterest()
                val body = JsonParser.parseString(jsonObject.toString()) as JsonObject
                viewModel.postSignUp(body, interest, profileImg)
            }
        }
    }

    private fun setRv() {
        viewDataBinding.actSignUpInterestRvPosition.apply {
            adapter = object : BaseRecyclerViewAdapter<Interest, ItemInterestBinding>() {
                override val layoutResID: Int = R.layout.item_interest
                override val bindingVariableId: Int = BR.interest
                override val listener: OnItemClickListener? = itemClick
            }
            layoutManager = FlexboxLayoutManager(this@SignUpInterestActivity).apply {
                flexWrap = FlexWrap.WRAP
                flexDirection = FlexDirection.ROW
            }
        }

        viewDataBinding.actSignUpInterestRvTech.apply {
            adapter = object : BaseRecyclerViewAdapter<Interest, ItemInterestBinding>() {
                override val layoutResID: Int = R.layout.item_interest
                override val bindingVariableId: Int = BR.interest
                override val listener: OnItemClickListener? = itemClick
            }
            layoutManager = FlexboxLayoutManager(this@SignUpInterestActivity).apply {
                flexWrap = FlexWrap.WRAP
                flexDirection = FlexDirection.ROW
            }
        }

        viewDataBinding.actSignUpInterestRvEtc.apply {
            adapter = object : BaseRecyclerViewAdapter<Interest, ItemInterestBinding>() {
                override val layoutResID: Int = R.layout.item_interest
                override val bindingVariableId: Int = BR.interest
                override val listener: OnItemClickListener? = itemClick
            }
            layoutManager = FlexboxLayoutManager(this@SignUpInterestActivity).apply {
                flexWrap = FlexWrap.WRAP
                flexDirection = FlexDirection.ROW
            }
        }
    }

    private val itemClick = object : BaseRecyclerViewAdapter.OnItemClickListener {
        override fun onItemClicked(item: Any?, position: Int?) {
            clickInterest(position!!, (item as Interest).interestIdx)
        }
    }

    private fun refreshRv(rv: RecyclerView, position: Int, list: ArrayList<Interest>) {
        (rv.adapter as BaseRecyclerViewAdapter<Interest, ItemInterestBinding>).run {
            refreshItem(list[position], position)
            notifyItemChanged(position)
        }
    }

    fun clickInterest(position: Int, idx: Int) {
        when (idx) {
            in 100..199 -> {
                positionList[position].isSelected = !positionList[position].isSelected
                refreshRv(viewDataBinding.actSignUpInterestRvPosition, position, positionList)
            }
            in 200..299 -> {
                techList[position].isSelected = !techList[position].isSelected
                refreshRv(viewDataBinding.actSignUpInterestRvTech, position, techList)
            }
            in 300..400 -> {
                etcList[position].isSelected = !etcList[position].isSelected
                refreshRv(viewDataBinding.actSignUpInterestRvEtc, position, etcList)
            }
        }
    }

    private fun combineInterest(): Boolean {
        interest.clear()
        for (i in 0 until positionList.size) {
            if (positionList[i].isSelected) {
                interest.add(positionList[i].interestIdx)
            }
        }
        for (i in 0 until techList.size) {
            if (techList[i].isSelected) {
                interest.add(techList[i].interestIdx)
            }
        }
        for (i in 0 until etcList.size) {
            if (etcList[i].isSelected) {
                interest.add(etcList[i].interestIdx)
            }
        }
        return interest.size in 3..10
    }
}