package com.soulmates.valley.data.repository.chat

import com.soulmates.valley.data.model.chat.response.ChatLogResponse
import com.soulmates.valley.data.model.chat.response.ChatRoomListResponse
import com.soulmates.valley.data.remote.ValleyService
import io.reactivex.Single

class ChatRepository (private val service: ValleyService){
    fun getChatRoomList(userId: Int): Single<ChatRoomListResponse> = service.getChatRoomList(userId).map {
        it }
    fun getChatLog(roomName: String): Single<ChatLogResponse> = service.getChatLog(roomName).map { it }
}