package com.soulmates.valley.util.databinding

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.soulmates.valley.R
import com.soulmates.valley.ValleyApplication
import com.soulmates.valley.base.BaseRecyclerViewAdapter
import com.soulmates.valley.data.model.user.InterestList.etcList
import com.soulmates.valley.data.model.user.InterestList.positionList
import com.soulmates.valley.data.model.user.InterestList.techList
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

//recycler view 아이템 replace
@Suppress("UNCHECKED_CAST")
@BindingAdapter("replaceAll")
fun RecyclerView.replaceAll(list: List<Any>?) {
    (this.adapter as? BaseRecyclerViewAdapter<Any, *>)?.run {
        replaceAll(list)
        notifyItemRangeChanged(0, this.itemCount)
    }
}

@BindingAdapter("selectInterestItem")
fun TextView.selectInterestItem(isSelected: Boolean) {
    if (isSelected) {
        setBackgroundResource(R.drawable.border_10_main_yellow)
        setTextColor(ContextCompat.getColor(ValleyApplication.globalApplication, R.color.main_yellow))
    } else {
        setBackgroundResource(R.drawable.border_10_454545)
        setTextColor(ContextCompat.getColor(ValleyApplication.globalApplication, R.color.white))
    }
}

@BindingAdapter("setFriendButton")
fun TextView.setFriendButton(isSelected: Boolean) {
    if (isSelected) {
        setBackgroundResource(R.drawable.solid_5_main_green)
        setTextColor(ContextCompat.getColor(this.context, R.color.black))
    } else {
        setBackgroundResource(R.drawable.border_5_454545)
        setTextColor(ContextCompat.getColor(this.context, R.color.gray))
    }
}

@BindingAdapter("postAttachType", "postAttachFlag")
fun ImageView.selectPostAttachTypeItem(idx: Int, flag: Boolean) {
    when (idx) {
        1 -> {
            if (flag) setImageResource(R.drawable.btn_gallery_on)
            else setImageResource(R.drawable.btn_gallery_off)
        }
        2 -> {
            if (flag) setImageResource(R.drawable.btn_url_on)
            else setImageResource(R.drawable.btn_url_off)
        }
        3 -> {
            if (flag) setImageResource(R.drawable.btn_codeblock_on)
            else setImageResource(R.drawable.btn_codeblock_off)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@BindingAdapter("setCreateTime")
fun TextView.setCreateTime(createDt: String?) {
    if(createDt.isNullOrBlank()) {
        text = ""
        return
    }

    var convertTime: String? = null
    val suffix = "전"
    try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA)
        val createTime = dateFormat.parse(createDt)
        val nowTime = Date()
        val dateDiff = nowTime.time - createTime.time
        val second: Long = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
        val minute: Long = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
        val hour: Long = TimeUnit.MILLISECONDS.toHours(dateDiff)
        val day: Long = TimeUnit.MILLISECONDS.toDays(dateDiff)
        when {
            second < 60 -> convertTime = "${second}초 $suffix"
            minute < 60 -> convertTime = "${minute}분 $suffix"
            hour < 24 -> convertTime = "${hour}시간 $suffix"
            day < 7 -> convertTime = "${day}일 $suffix"
            day >= 7 -> {
                if(createTime.year != nowTime.year)
                    convertTime = SimpleDateFormat("yyyy년 M월 d일", Locale.KOREA).format(createTime)
                else
                    convertTime = SimpleDateFormat("M월 d일", Locale.KOREA).format(createTime)
            }
        }
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    text = convertTime
}

@BindingAdapter("setLikeAsset")
fun ImageView.setLikeAsset(flag: Boolean) {
    if(flag) setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_like_on))
    else setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_like_off))
}

@SuppressLint("SetTextI18n")
@BindingAdapter("setLikeText", "setLikeTextColor")
fun TextView.setLikeText(count: Int, flag: Boolean) {
    text = "좋아요 ${count}개"
    if(flag) setTextColor(ContextCompat.getColor(context, R.color.sub_red))
    else setTextColor(ContextCompat.getColor(context, R.color.white))
}

@SuppressLint("SetTextI18n")
@BindingAdapter("setCommentCount")
fun TextView.setCommentCount(count: Int) {
    text = "댓글 ${count}개"
}

@SuppressLint("SetTextI18n")
@BindingAdapter("setFollowingCount")
fun TextView.setFollowingCount(count: Int) {
    text = "$count 팔로잉"
}

@SuppressLint("SetTextI18n")
@BindingAdapter("setFollowerCount")
fun TextView.setFollowerCount(count: Int) {
    text = "$count 팔로워"
}

@BindingAdapter("setInterestByIdx")
fun TextView.setInterestByIdx(interestIdx: Int){
    for (i in 0 until positionList.size) {
        if(positionList[i].interestIdx==interestIdx) text = positionList[i].interestName
    }
    for (i in 0 until techList.size) {
        if(techList[i].interestIdx==interestIdx) text = techList[i].interestName
    }
    for (i in 0 until etcList.size) {
        if(etcList[i].interestIdx==interestIdx) text = etcList[i].interestName
    }
}

// 성공 시 VISIBLE
@BindingAdapter("setVisibleWhenSuccess")
fun View.setVisibleWhenSuccess(num: Int) {
    visibility =
        if (num == 200) VISIBLE
        else INVISIBLE
}

// 실패 시 VISIBLE
@BindingAdapter("setVisibleWhenFail")
fun View.setVisibleWhenFail(num: Int) {
    visibility =
        if (num == 200 || num == 0) INVISIBLE
        else VISIBLE
}

// 기본 멘트 INVISIBLE
@BindingAdapter("setInvisibleTitle")
fun View.setInvisibleTitle(num: Int?) {
    visibility =
        if (num in 200..500) INVISIBLE
        else VISIBLE
}

@BindingAdapter("setFollowButton")
fun TextView.setFollowButton(isFollowed: Boolean){
    if(isFollowed){
        setBackgroundResource(R.drawable.border_5_454545)
        setTextColor(ContextCompat.getColor(context, R.color.gray))
        text = "팔로잉"
    }else{
        setBackgroundResource(R.drawable.solid_5_main_yellow)
        setTextColor(ContextCompat.getColor(context, R.color.black))
        text = "팔로우"
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("setTagText")
fun TextView.setTagText(tag: String){
    text = "#$tag"
}

@SuppressLint("SetTextI18n")
@BindingAdapter("setNotiText", "setNotiNickname")
fun TextView.setNotiText(type: Int, fromUserNickname: String){
    when(type){
        1 -> text = "${fromUserNickname}님이 회원님의 게시글에 댓글을 남겼습니다."
        2 -> text = "${fromUserNickname}님이 회원님의 게시글에 좋아요를 눌렀습니다."
        3 -> text = "${fromUserNickname}님이 회원님을 팔로우 합니다."
    }
}

@BindingAdapter("glideCenterCrop")
fun ImageView.glideCenterCrop(uri: String?) {
    /*
    DiskCacheStrategy.NONE : 디스크 캐싱을 하지 않는다.
    DiskCacheStrategy.SOURCE : 원본 이미지만 캐싱
    DiskCacheStrategy.RESULT : 변형된 이미지만 캐싱
    DiskCacheStrategy.ALL : 모든 이미지를 캐싱(기본)
     */
    val requestOptions = RequestOptions
        .skipMemoryCacheOf(false) // 메모리 캐시 사용
        .diskCacheStrategy(DiskCacheStrategy.DATA)

    Glide.with(this.context)
        .load(uri)
        .apply(requestOptions)
        .error(R.drawable.img_url_default)
        .into(this)
}