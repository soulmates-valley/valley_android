package com.soulmates.valley.data.model.noti

data class NotiHistoryResponse(
    val code: Int,
    val data: ArrayList<NotiHistory>?
)

data class NotiHistory(
    val fromUserId: Int,
    val notiTime: String,
    val event: Int,
    val nickname: String,
    val profileImg: String
)