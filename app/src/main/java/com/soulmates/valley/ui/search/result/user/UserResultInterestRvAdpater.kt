package com.soulmates.valley.ui.search.result.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.soulmates.valley.databinding.ItemSearchResultUserInterestBinding
import timber.log.Timber
import kotlin.math.min

class UserResultInterestRvAdpater: RecyclerView.Adapter<UserResultInterestRvAdpater.UserResultInterestViewHolder>(){

    lateinit var binding: ItemSearchResultUserInterestBinding
    var data = mutableListOf<Int>()
    private val limit = 4

    fun replaceAll(array: ArrayList<Int>?) {
        array?.let {
            data.run {
                clear()
                addAll(it)
            }
        }
    }

    override fun getItemViewType(position: Int): Int = position

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemCount() = min(data.size, limit)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserResultInterestViewHolder {
        binding = ItemSearchResultUserInterestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserResultInterestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserResultInterestViewHolder, position: Int) {
        holder.binding.interestIdx = data[position]
    }

    inner class UserResultInterestViewHolder(val binding: ItemSearchResultUserInterestBinding): RecyclerView.ViewHolder(binding.root)
}