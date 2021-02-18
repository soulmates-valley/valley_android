package com.soulmates.valley.data.model.chat

data class Message (
    var type: Int?,
    val message: String,
    val userId: Int,
    var nickname : String?,
    var profileImg : String?,
    val time : String,
    val room: String
)