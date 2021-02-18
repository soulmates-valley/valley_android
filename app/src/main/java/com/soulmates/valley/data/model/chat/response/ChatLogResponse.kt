package com.soulmates.valley.data.model.chat.response

import com.soulmates.valley.data.model.chat.Message

data class ChatLogResponse (
    val code: Int,
    val data: ArrayList<Message>
)