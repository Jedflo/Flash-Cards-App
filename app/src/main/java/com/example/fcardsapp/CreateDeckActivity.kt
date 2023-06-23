package com.example.fcardsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class CreateDeckActivity : AppCompatActivity() {
    private lateinit var etDeckName: EditText
    private lateinit var etDeckDescription: EditText
    private lateinit var bCreateDeck: Button
    private lateinit var ibDeleteDeck: ImageButton
    private lateinit var sqLiteHelper: SQLiteHelper
    private lateinit var mode: String
    private var deckID:String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_deck)
        sqLiteHelper = SQLiteHelper(this)
        initViews()
        mode = intent.getStringExtra(Constants.CREATE_DECK_MODE_INTENT_KEY).toString()

        if (mode == Constants.MODE_EDIT){
            deckID = intent.getStringExtra(Constants.DECK_ID_INTENT_KEY)
            val deck = sqLiteHelper.getDeck(deckID!!)
            etDeckName.setText(deck?.deckName)
            etDeckDescription.setText(deck?.deckDescription)
            bCreateDeck.text = "Save Changes"
            ibDeleteDeck.visibility = ImageButton.VISIBLE
        }

        bCreateDeck.setOnClickListener {
            if (mode == Constants.MODE_CREATE){
                createDeck()
            }
            else{
                updateDeck()
                setResult(RESULT_OK)
                finish()
            }
        }

        ibDeleteDeck.setOnClickListener {
            deleteDeck()
        }



    }

    private fun initViews(){
        etDeckName = findViewById(R.id.etCreateDeckName)
        etDeckDescription = findViewById(R.id.etCreateDeckDescription)
        bCreateDeck = findViewById(R.id.bCreateDeckCreate)
        ibDeleteDeck = findViewById(R.id.ibCreateDeckDeleteDeck)
    }

    private fun createDeck(){
        val newDeckName = etDeckName.text.toString()
        val newDeckDescription = etDeckDescription.text.toString()

        if (newDeckName.isEmpty()&&newDeckDescription.isEmpty()){
            Toast.makeText(
                this,
                "Deck Name and Description cannot be blank",
                Toast.LENGTH_SHORT).show()
            return
        }
        if (newDeckName.isEmpty()){
            Toast.makeText(
                this,
                "Deck Name cannot be blank.",
                Toast.LENGTH_SHORT
            ).show()
        }
        if (newDeckDescription.isEmpty()){
            Toast.makeText(
                this,
                "Deck Description cannot be blank.",
                Toast.LENGTH_SHORT
            ).show()
        }

        val newDeck = DeckModel(deckName = newDeckName, deckDescription = newDeckDescription)
        sqLiteHelper.insertDeck(newDeck)
        Log.e("DECK CREATED", "${newDeck.deckName} with ID ${newDeck.id}")
        val intent = Intent(this, DeckActivity::class.java)
        intent.putExtra(Constants.DECK_ID_INTENT_KEY, newDeck.id)
        startActivity(intent)
        finish()
    }

    private fun updateDeck(){
        if (deckID?.isBlank() == true || deckID == null){
            Log.e("DECK ID BLANK OR NULL", "CAN NOT PROCEED WITH UPDATING")
            Toast.makeText(this,"Error Updating", Toast.LENGTH_SHORT)
            return
        }
        val deck = sqLiteHelper.getDeck(deckID!!)!!
        val newDeckName = etDeckName.text.toString()
        val newDeckDescription = etDeckDescription.text.toString()
        if (newDeckName == deck.deckName && newDeckDescription == deck.deckDescription){
            Toast.makeText(this, "No changes to save", Toast.LENGTH_SHORT)
            return
        }
        val newDeck = DeckModel(deck.id, newDeckName, newDeckDescription)
        sqLiteHelper.updateDeck(newDeck)
        Toast.makeText(this, "Changes saved successfully!", Toast.LENGTH_SHORT)
    }

    private fun deleteDeck(){
        if (deckID?.isBlank() == true || deckID == null){
            Log.e("DECK ID BLANK OR NULL", "CAN NOT PROCEED WITH DELETE")
            Toast.makeText(this,"Error DELETING", Toast.LENGTH_SHORT)
            return
        }
        val deck = sqLiteHelper.getDeck(deckID!!)!!

        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("DELETE DECK")
        alertDialog.setMessage("Are you sure you want to delete '${deck.deckName}'? \n\n" +
                "After deletion, this deck can no longer be restored.")
        alertDialog.setCancelable(true)

        alertDialog.setPositiveButton("OK"){dialog, _->
            sqLiteHelper.deleteDeck(deck)
            Toast.makeText(this, "${deck.deckName} successfully deleted", Toast.LENGTH_SHORT)
            setResult(RESULT_FIRST_USER)
            finish()
        }

        alertDialog.setNegativeButton("Cancel"){ dialog, _ ->
            dialog.dismiss()
        }

        val alert = alertDialog.create()
        alert.show()
    }


}