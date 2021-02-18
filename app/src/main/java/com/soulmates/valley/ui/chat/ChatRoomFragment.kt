package com.soulmates.valley.ui.chat

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.soulmates.valley.BR
import com.soulmates.valley.R
import com.soulmates.valley.base.BaseFragment
import com.soulmates.valley.base.BaseRecyclerViewAdapter
import com.soulmates.valley.data.model.chat.ChatRoom
import com.soulmates.valley.databinding.FragmentChatRoomBinding
import com.soulmates.valley.databinding.ItemChatRoomBinding
import com.soulmates.valley.util.view.SwipeHelper
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatRoomFragment : BaseFragment<FragmentChatRoomBinding, ChatViewModel>() {
    override val layoutResID: Int = R.layout.fragment_chat_room
    override val viewModel: ChatViewModel by viewModel()

    val onRoomClickListener = object : BaseRecyclerViewAdapter.OnItemClickListener {
        override fun onItemClicked(item: Any?, position: Int?) {
            startActivity(
                Intent(requireActivity(), ChatActivity::class.java)
                    .putExtra("otherUserId", (item as ChatRoom).userId)
                    .putExtra("profileImg", item.profileImg)
                    .putExtra("nickname", item.nickname)
                    .putExtra("roomName", item.roomName)
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRv()
        observe()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getChatRoomList()
    }

    private fun setRv() {
        viewDataBinding.fragChatRoomRv.apply {
            adapter = object : BaseRecyclerViewAdapter<ChatRoom, ItemChatRoomBinding>() {
                override val bindingVariableId: Int = BR.chatRoom
                override val layoutResID: Int = R.layout.item_chat_room
                override val listener: OnItemClickListener? = onRoomClickListener
            }
            layoutManager = LinearLayoutManager(requireActivity())
            ItemTouchHelper(object : SwipeHelper(requireContext(), viewDataBinding.fragChatRoomRv) {
                override fun instantiateUnderlayButton(
                    viewHolder: RecyclerView.ViewHolder?,
                    underlayButtons: MutableList<UnderlayButton>?
                ) {
                    underlayButtons?.add(UnderlayButton(
                        null,
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_chat_out
                        ),
                        Color.parseColor("#f57653"), null,
                        object : UnderlayButtonClickListener {
                            override fun onClick(position: Int) { // 방 나가기
                                (viewDataBinding.fragChatRoomRv.adapter as BaseRecyclerViewAdapter<ChatRoom, ItemChatRoomBinding>).run {
                                    this.items.removeAt(position)
                                    this.notifyItemRemoved(position)
                                }
                            }
                        }
                    ))
                }
            }).attachToRecyclerView(this)
        }
    }

    private fun observe() {
        viewModel.chatRoomList.observe(requireActivity(), Observer {
            (viewDataBinding.fragChatRoomRv.adapter as BaseRecyclerViewAdapter<ChatRoom, ItemChatRoomBinding>).run {
                replaceAll(it)
                notifyDataSetChanged()
            }
        })
    }
}