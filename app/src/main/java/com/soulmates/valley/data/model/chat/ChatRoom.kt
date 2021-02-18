package com.soulmates.valley.data.model.chat

import com.google.gson.annotations.SerializedName

data class ChatRoom(
    val roomName: String,
    @SerializedName("chatRoomImg")
    val profileImg: String?,
    val userId: Int,
    val nickname: String,
    val content: String,
    val lastMessageTime: String
)