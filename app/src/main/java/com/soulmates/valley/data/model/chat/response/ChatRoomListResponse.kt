package com.soulmates.valley.data.model.chat.response

import com.soulmates.valley.data.model.chat.ChatRoom

data class ChatRoomListResponse(
    val code: Int,
    val data: ArrayList<ChatRoom>
)