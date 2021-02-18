package com.soulmates.valley.ui.chat

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.gson.Gson
import com.soulmates.valley.R
import com.soulmates.valley.ValleyApplication
import com.soulmates.valley.base.BaseActivity
import com.soulmates.valley.data.model.chat.ChatConnect
import com.soulmates.valley.data.model.chat.Message
import com.soulmates.valley.databinding.ActivityChatBinding
import com.soulmates.valley.util.extension.setSafeOnClickListener
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatActivity : BaseActivity<ActivityChatBinding, ChatViewModel>() {
    override val layoutResID: Int = R.layout.activity_chat
    override val viewModel: ChatViewModel by viewModel()
    var chatAdapter: ChatRvAdapter = ChatRvAdapter()
    private lateinit var requestManager: RequestManager
    private val socket: Socket by inject()
    private val gson: Gson = Gson()
    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("a h:mm")
    lateinit var dateTime: LocalDateTime

    private val tag = "socket@@@"

    private var otherUserId: Int = 0
    private var otherNickname = ""
    private var otherProfileImg = ""

    private var myUserId: Int = ValleyApplication.prefManager.userId.toInt()
    private var myNickname: String = ""
    private var myProfileImg: String? = ""

    private var roomName: String = ""

    private val CHAT_TYPE_MINE = 0
    private val CHAT_TYPE_OTHER = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getProfileFromRoom()
        viewModel.profileData.observe(this, Observer {
            myNickname = it.nickname
            myProfileImg = it.profileImage
        })

        intent?.let {
            roomName = it.getStringExtra("roomName")
            otherUserId = it.getIntExtra("otherUserId", -1)
            otherNickname = it.getStringExtra("nickname")
            otherProfileImg = it.getStringExtra("profileImg")
            setOtherProfile(otherProfileImg, otherNickname)
        }

        if(roomName.isBlank()){ // 방 이름 만들기 (프로필에서 채팅 누를때 생성)
            roomName = if (myUserId < otherUserId) "r" + myUserId + "w" + otherUserId
            else "r" + otherUserId + "w" + myUserId
        }
        viewModel.getChatLog(roomName)

        Timber.tag(tag).d(roomName)

        requestManager = Glide.with(this)
        setText()
        setRv()
        setClick()
        setSocket()
    }

    private fun setOtherProfile(profileImg: String?, nickname: String) {
        if (this.isFinishing) return
        Glide.with(this)
            .load(profileImg)
            .error(R.drawable.img_non_profile_photo)
            .into(viewDataBinding.actChatCivProfile)
        viewDataBinding.actChatTvName.text = nickname
    }

    private fun setText() {
        viewDataBinding.actChatEt.apply {
            addTextChangedListener {
                if (!it.isNullOrBlank()) {
                    viewDataBinding.actChatTvSend.apply {
                        isEnabled = true
                        setBackgroundResource(R.drawable.solid_5_292a2c)
                        setTextColor(ContextCompat.getColor(this@ChatActivity, R.color.main_green))
                    }
                    if (layout.lineCount > 1) {
                        viewDataBinding.actChatRv.run {
                            scrollToPosition(adapter!!.itemCount - 1)
                        }
                    }
                } else {
                    viewDataBinding.actChatTvSend.apply {
                        isEnabled = false
                        setBackgroundResource(R.drawable.solid_5_gray)
                        setTextColor(ContextCompat.getColor(this@ChatActivity, R.color.white))
                    }
                }
            }
        }
    }

    private fun setRv() {
        viewDataBinding.actChatRv.run {
            adapter = chatAdapter.apply {
                setRequestManager(requestManager)
                setHasStableIds(true)
                myUserId = this@ChatActivity.myUserId
                otherUserId = this@ChatActivity.otherUserId
                otherProfileImg = this@ChatActivity.otherProfileImg
            }
            layoutManager = LinearLayoutManager(this@ChatActivity)
        }
        viewModel.chatLogList.observe(this, Observer {
            chatAdapter.run {
                replaceAll(it)
                notifyDataSetChanged()
            }
        })
    }

    private fun setClick() {
        viewDataBinding.run {
            actChatTvSend.setSafeOnClickListener {
                sendMessage()
                viewDataBinding.actChatEt.text.clear()
            }
            actChatIvBack.setSafeOnClickListener { // 방 나가기
                finish()
            }
        }
    }

    @SuppressLint("NewApi")
    private fun sendMessage() {
        val content: String = viewDataBinding.actChatEt.text.toString()
        dateTime = LocalDateTime.now()
        val formatted = dateTime.format(formatter)

        val message = Message(CHAT_TYPE_MINE, content, myUserId, myNickname, myProfileImg, formatted, roomName)
        val json = gson.toJson(message)
        socket.emit("message", json)

        addItemToRecyclerView(message)
    }

    private fun setSocket() {
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
        socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError)

        socket.on(Socket.EVENT_CONNECT, onConnect) // join room
        socket.on("newMessage", onMessageReceived) // send new message
        socket.connect()
    }

    // 채팅 방 입장 & 소켓 연결
    var onConnect = Emitter.Listener {
        val data = ChatConnect(roomName, myUserId, myNickname, myProfileImg, otherUserId, otherNickname, otherProfileImg)
        val json = gson.toJson(data)
        socket.emit("joinRoom", json)
    }

    // 상대방의 메세지를 받음
    private val onMessageReceived = Emitter.Listener {
        val message = Gson().fromJson<Message>(it[0].toString(), Message::class.java)
        message.run {
            type = CHAT_TYPE_OTHER
            profileImg = otherProfileImg
            nickname = otherNickname
        }
        addItemToRecyclerView(message)
    }

    // 채팅 방 퇴장
    override fun onDestroy() {
        super.onDestroy()
        val data = ChatConnect(roomName, myUserId, myNickname, myProfileImg, otherUserId, otherNickname, otherProfileImg)
        val jsonData = gson.toJson(data)
        socket.emit("leaveRoom", jsonData)
        socket.disconnect()
    }

    private fun addItemToRecyclerView(message: Message) {
        runOnUiThread {
            chatAdapter.addItem(message)
            chatAdapter.notifyItemInserted(chatAdapter.data.size-1)
            viewDataBinding.actChatRv.run {
                scrollToPosition(adapter!!.itemCount - 1)
            }
        }
    }

    private val onConnectError = Emitter.Listener { runOnUiThread { } }
}