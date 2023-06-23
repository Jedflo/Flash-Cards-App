package com.example.fcardsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DeckAdapter: RecyclerView.Adapter<DeckAdapter.DeckViewHolder>() {
    private var deckList: ArrayList<DeckModel> = ArrayList()
    private var onClickItem: ((DeckModel)->Unit)? = null
    private var onClickEditDeck: ((DeckModel)->Unit)? = null

    fun addDecks(items: ArrayList<DeckModel>){
        this.deckList=items
        notifyDataSetChanged()
    }

    fun setOnClickItem(callback: (DeckModel)->Unit){
        this.onClickItem = callback
    }


    fun setOnClickEditDeck(callback: (DeckModel) -> Unit){
        this.onClickEditDeck = callback
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DeckViewHolder (
        LayoutInflater.from(parent.context)
            .inflate(R.layout.list_deck_items, parent, false)
            )

    override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
        val deck = deckList[position]
        holder.bindView(deck)
        holder.itemView.setOnClickListener { onClickItem?.invoke(deck) }
        holder.ibEditDeck.setOnClickListener { onClickEditDeck?.invoke(deck) }
    }

    override fun getItemCount(): Int {
        return deckList.size
    }

    class DeckViewHolder(var view: View): RecyclerView.ViewHolder(view) {
        private var deckName = view.findViewById<TextView>(R.id.tvDeckName)
        private var deckDescription = view.findViewById<TextView>(R.id.tvDeckDescription)
        var ibEditDeck: ImageButton = view.findViewById(R.id.ibEditDeck)
        val sqLiteHelper = SQLiteHelper(this.view.context)


        fun bindView(deck:DeckModel){
            val deckSize = sqLiteHelper.getAllCardsFromDeck(deck.id).size
            deckName.text = deck.deckName
            deckDescription.text = deck.deckDescription
            val editDeckButtonImage = getEditDeckImageButton(deckSize)
            ibEditDeck.setImageResource(editDeckButtonImage)

        }

        private fun getEditDeckImageButton(deckSize: Int): Int {
            var output = R.drawable.ic_0_card
            if (deckSize in 1..4){
                output = R.drawable.ic_1_card
            }
            else if (deckSize in 5..9){
                output = R.drawable.ic_2_card
            }
            else if (deckSize in 10..14){
                output = R.drawable.ic_3_card
            }
            else if (deckSize >= 15){
                output = R.drawable.ic_4_card
            }
            return output
        }

    }
}