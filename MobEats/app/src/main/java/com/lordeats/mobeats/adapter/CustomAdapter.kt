package com.lordeats.mobeats.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lordeats.mobeats.R
import com.lordeats.mobeats.listeners.CustomListeners
import com.lordeats.mobeats.viewHolder.BaseViewHolder
import com.lordeats.mobeats.viewHolder.CustomViewHolder
import com.lordeats.mobeats.viewModel.CustomViewModel
import com.lordeats.mobeats.viewModel.SwipeState

class CustomAdapter : RecyclerView.Adapter<BaseViewHolder> {

    companion object {
        private val TAG : String = CustomAdapter::class.java.getSimpleName()
    }
    /**Main */
    private val customListeners : CustomListeners
    private val swipeState : SwipeState

    private var list : MutableList<CustomViewModel>

    constructor(customListeners : CustomListeners, swipeState : SwipeState) : super() {
        this.customListeners = customListeners
        this.swipeState = swipeState
    }

    init {
        list = mutableListOf<CustomViewModel>()
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : BaseViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(parent.context)
        val view : View = layoutInflater.inflate(R.layout.item_cell, parent, false)
        return CustomViewHolder(parent.context, view, customListeners)
    }

    override fun onBindViewHolder(holder : BaseViewHolder, position : Int) {
        holder.bindDataToViewHolder(list[position], position, swipeState)
    }

    override fun getItemCount() : Int {
        return list.size
    }

    fun setItems(items : MutableList<CustomViewModel>) {
        list.clear()
        list.addAll(items)
        notifyDataSetChanged()
    }

    fun insertItem(item : CustomViewModel, position : Int) {
        list.add(position, item)
        notifyItemInserted(position)
    }

    fun insertItems(items : List<CustomViewModel>, position : Int) {
        list.addAll(items)
        notifyItemRangeChanged(position, itemCount)
    }

    fun updateItem(item : CustomViewModel, position : Int) {
        list[position] = item
        notifyItemChanged(position)
    }

    fun deleteItem(position : Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun deleteAllItems() {
        list.clear()
        notifyItemRangeRemoved(0, itemCount)
    }
}