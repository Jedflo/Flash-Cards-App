package com.example.fcardsapp

import android.text.Editable
import android.text.Layout
import android.view.LayoutInflater
import android.view.ScrollCaptureCallback
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class CardAdapter: RecyclerView.Adapter<CardAdapter.CardViewHolder>() {
    private var cardList: ArrayList<CardModel> = ArrayList()
    private var onClickItem: ((CardModel)->Unit)? = null
    private var onLongClickItem: ((CardModel)->Unit)? = null

    fun addCards(items: ArrayList<CardModel>){
        this.cardList = items
        notifyDataSetChanged()
    }

    fun setOnClickItem(callback: (CardModel) -> Unit){
        this.onClickItem = callback
    }

    fun setOnLongClickItem(callback: (CardModel) -> Unit){
        this.onLongClickItem = callback
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CardViewHolder (
            LayoutInflater.from(parent.context).
            inflate(R.layout.list_card_items, parent,false)
            )

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cardList[position]
        holder.bindView(card)
        holder.itemView.setOnClickListener { onClickItem?.invoke(card) }
        holder.itemView.setOnLongClickListener {
            onLongClickItem?.invoke(card)
            return@setOnLongClickListener true
        }

    }

    override fun getItemCount(): Int {
        return cardList.size
    }


    class CardViewHolder(var view:View): RecyclerView.ViewHolder(view) {
        private var cardQuestion = view.findViewById<TextView>(R.id.tvCardItem)


        fun bindView(card: CardModel){
            cardQuestion.text = card.question
        }


    }


}