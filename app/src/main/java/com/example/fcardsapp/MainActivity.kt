package com.example.fcardsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var sqliteHelper: SQLiteHelper
    private lateinit var btnAddDeck: Button
    private lateinit var ibtEditDeck: ImageButton
    private lateinit var rvDeckList: RecyclerView
    private var deckAdapter: DeckAdapter? = null

    private var activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if(result.resultCode == RESULT_FIRST_USER) {

        }
        else{

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sqliteHelper = SQLiteHelper(this)
        initViews()
        initRecyclerView()
        getDecks()


        btnAddDeck.setOnClickListener {
            goToAddDeck()
        }


        deckAdapter?.setOnClickEditDeck {
            val deckId = it.id
            val intent = Intent(this, DeckActivity::class.java)
            intent.putExtra(Constants.DECK_ID_INTENT_KEY,deckId)
            startActivity(intent)
        }

        deckAdapter?.setOnClickItem {
            val deckId = it.id
            val intent = Intent(this, FlashCardModeActivity::class.java)
            intent.putExtra(Constants.DECK_ID_INTENT_KEY,deckId)
            activityResultLauncher.launch(intent)
        }


    }

    fun initRecyclerView(){
        rvDeckList.layoutManager = LinearLayoutManager(this)
        deckAdapter = DeckAdapter()
        rvDeckList.adapter = deckAdapter

    }

    fun initViews(){
        btnAddDeck = findViewById(R.id.bAddDeckMa)
        rvDeckList = findViewById(R.id.rvDeckList)
    }

    fun goToAddDeck(){
        val intent = Intent(this, CreateDeckActivity::class.java)
        intent.putExtra(Constants.CREATE_DECK_MODE_INTENT_KEY,Constants.MODE_CREATE )
        startActivity(intent)
    }

    fun getDecks(){
        val deckList = sqliteHelper.getAllDecks()
        Log.e("DECK LIST SIZE:", "${deckList.size}")
        deckAdapter?.addDecks(deckList)
    }

    override fun onRestart() {
        super.onRestart()
        finish();
        startActivity(intent);
    }


}