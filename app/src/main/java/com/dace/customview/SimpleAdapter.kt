package com.dace.customview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class SimpleAdapter(private val context: Context, private val data: List<Menu>) :
    RecyclerView.Adapter<SimpleAdapter.SimpleViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): SimpleViewHolder =
        SimpleViewHolder(LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(viewHolder: SimpleViewHolder, position: Int) {
        viewHolder.tvContent.text = data[position].content
        viewHolder.tvContent.setOnClickListener { view ->
            onItemClickListener?.onItemClick(view, position)
        }
    }

    inner class SimpleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvContent: TextView = view.findViewById(android.R.id.text1)
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
}