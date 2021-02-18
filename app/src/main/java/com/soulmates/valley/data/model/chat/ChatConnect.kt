package com.soulmates.valley.data.model.chat

data class ChatConnect (
    val roomName: String,
    val user1Id: Int,
    val user1Nickname: String,
    val user1Img: String?,
    val user2Id: Int,
    val user2Nickname: String,
    val user2Img: String?
)