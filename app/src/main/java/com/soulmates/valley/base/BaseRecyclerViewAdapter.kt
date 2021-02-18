package com.soulmates.valley.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.soulmates.valley.util.extension.setSafeOnClickListener

abstract class BaseRecyclerViewAdapter<ITEM : Any, VB : ViewDataBinding>
    : RecyclerView.Adapter<BaseRecyclerViewAdapter<ITEM, VB>.ViewHolder>() {

    lateinit var viewDataBinding: VB

    abstract val bindingVariableId: Int
    abstract val layoutResID: Int
    abstract val listener: OnItemClickListener?

    private val viewModel: BaseViewModel? = null
    var items = mutableListOf<ITEM>()

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }

    fun replaceAll(items: List<ITEM>?) {
        items?.let {
            this.items.run {
                clear()
                addAll(it)
            }
        }
    }

//    fun replaceAllWithoutEnter(items: List<ITEM>?){
//        this.items.clear()
//        if(items != null) {
//            for (i in items!!.indices) {
//                (items[i] as ItemBase).contents.replace("\\r\\n", "\n");
//                this.items.add(items[i])
//            }
//        }
//    }

    fun addItems(items:List<ITEM>?){
        if (items != null) {
            for (i in items.indices) {
                this.items.add(items[i])
            }
        }
        notifyDataSetChanged()
    }

    fun addItem(value: ITEM){
        items.add(value)
    }

    fun addItemAtIndex(position: Int, value: ITEM){
        items.add(position, value)
    }

    fun removeItem(position: Int){
        items.removeAt(position)
    }

    fun clearAll(){
        items.clear()
        notifyDataSetChanged()
    }

    fun refreshItem(newItem : ITEM, position: Int){
        items[position] = newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutResID, parent, false)
        return ViewHolder(viewDataBinding)
    }

    override fun getItemCount() = items.size

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getItemViewType(position: Int): Int = position

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    inner class ViewHolder(private val dataBinding: ViewDataBinding) : RecyclerView.ViewHolder(dataBinding.root) {

        fun bind(item: Any?) {
            try {
                bindingVariableId.let {
                    dataBinding.root.setSafeOnClickListener {
                        listener?.onItemClicked(item, layoutPosition)
                    }
                    dataBinding.setVariable(it, item)
                    dataBinding.executePendingBindings()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(item: Any?, position:Int?)
    }
}