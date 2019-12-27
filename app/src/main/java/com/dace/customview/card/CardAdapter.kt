package com.dace.customview.card

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.dace.customview.R

class CardAdapter(private val context: Context, private val data: MutableList<String>?) :
    RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): CardViewHolder {
        return CardViewHolder(LayoutInflater.from(context).inflate(R.layout.item_card, parent, false))
    }

    override fun getItemCount(): Int = data?.size!!

    override fun onBindViewHolder(viewHolder: CardViewHolder, position: Int) {
        Glide.with(context).load(data?.get(position)).into(viewHolder.img)
    }

    inner class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var img: ImageView = view.findViewById(R.id.iv_content)
    }
}