package com.soulmates.valley.ui.post.write.image

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.soulmates.valley.databinding.ItemPostAttatchImgBinding
import com.soulmates.valley.util.extension.setSafeOnClickListener

class ImageRvAdapter : RecyclerView.Adapter<ImageViewHolder>(){

    var data = mutableListOf<Uri>()
    lateinit var binding: ItemPostAttatchImgBinding
    private var listener: ImageViewHolder.OnImageClickListener? = null

    fun setImageClickListener(listener: ImageViewHolder.OnImageClickListener) {
        this.listener = listener
    }

    fun clearAll(){
        data.clear()
        notifyDataSetChanged()
    }

    fun replaceAll(array : List<Uri>?){
        array?.let {
            data.run {
                clear()
                addAll(it)
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        binding = ItemPostAttatchImgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding, listener!!)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.binding.uri = data[position].toString()
    }
}

class ImageViewHolder(val binding : ItemPostAttatchImgBinding,
                      listener: OnImageClickListener) : RecyclerView.ViewHolder(binding.root){
    init {
        binding.itemPostAttachIvCancel.setSafeOnClickListener {
            listener.onDeleteItem(layoutPosition)
        }
    }
    interface OnImageClickListener {
        fun onDeleteItem(position: Int)
    }
}