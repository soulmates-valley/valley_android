package com.soulmates.valley.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.soulmates.valley.ValleyApplication
import com.soulmates.valley.base.BaseViewModel
import com.soulmates.valley.data.local.room.dao.UserInfoDao
import com.soulmates.valley.data.local.room.entity.UserProfile
import com.soulmates.valley.data.model.chat.ChatRoom
import com.soulmates.valley.data.model.chat.Message
import com.soulmates.valley.data.repository.chat.ChatRepository
import com.soulmates.valley.util.extension.with
import timber.log.Timber

class ChatViewModel(private val repository: ChatRepository, private val dao: UserInfoDao) : BaseViewModel(){
    private val _chatRoomList = MutableLiveData<ArrayList<ChatRoom>>()
    val chatRoomList: LiveData<ArrayList<ChatRoom>> get() = _chatRoomList

    fun getChatRoomList(){
        addDisposable(disposable = repository.getChatRoomList(ValleyApplication.prefManager.userId.toInt())
            .with()
            .subscribe({
                if (it.code==200) _chatRoomList.postValue(it.data)
            }){
                Timber.tag("getChatRoomList 통신 실패 error : ").d(it)
            })
    }

    private val _chatLogList = MutableLiveData<ArrayList<Message>>()
    val chatLogList: LiveData<ArrayList<Message>> get() = _chatLogList

    fun getChatLog(roomName: String){
        addDisposable(repository.getChatLog(roomName)
            .with()
            .subscribe({
                _chatLogList.postValue(it.data)
            }){
                Timber.tag("getChatLog 통신 실패 error : ").d(it)
            })
    }

    private val _profileData = MutableLiveData<UserProfile>()
    val profileData: LiveData<UserProfile> get() = _profileData

    fun getProfileFromRoom(){
        addDisposable(dao.getUserInfo()
            .with()
            .subscribe({
                _profileData.postValue(it)
            }){
                Timber.d("Road Profile From Room Fail")
            })
    }
}