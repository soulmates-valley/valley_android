package com.soulmates.valley.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.soulmates.valley.R
import com.soulmates.valley.data.model.chat.Message
import com.soulmates.valley.databinding.ItemChatMeBinding
import com.soulmates.valley.databinding.ItemChatOtherBinding

class ChatRvAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var requestManager: RequestManager? = null
    var data = arrayListOf<Message>()
    var myUserId = 0
    var otherUserId = 0
    var otherProfileImg: String = ""

    fun replaceAll(array: ArrayList<Message>?) {
        array?.let {
            data.clear()
            for(i in 0..array.size-1){
                if (i>0 && (array[i]== array[i-1])) continue
                data.add(array[i])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val chatMessage = data[position]
        chatMessage.type?.let {
            return chatMessage.type!!
        }
        if(chatMessage.userId == myUserId) return 0
        data[position].profileImg = otherProfileImg
        return 1
    }

    override fun getItemId(position: Int) = position.toLong()
    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        lateinit var viewHolder: RecyclerView.ViewHolder

        return when (viewType) {
            0 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_me, parent, false)
                ChatMeViewHolder(ItemChatMeBinding.bind(view))
            }
            1 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_other, parent, false)
                ChatOtherViewHolder(ItemChatOtherBinding.bind(view))
            }
            else -> viewHolder
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder){
            is ChatMeViewHolder -> {
                holder.binding.apply {
                    message = data[position]
                    executePendingBindings()
                }
            }
            is ChatOtherViewHolder -> {
                holder.binding.apply {
                    message = data[position]
                    executePendingBindings()
                }
            }
        }
    }

    fun addItem(item: Message) {
        data.add(item)
    }

    fun setRequestManager(requestManager: RequestManager){
        this.requestManager = requestManager
    }

    class ChatMeViewHolder(val binding: ItemChatMeBinding) : RecyclerView.ViewHolder(binding.root)
    class ChatOtherViewHolder(val binding: ItemChatOtherBinding) : RecyclerView.ViewHolder(binding.root)
}
