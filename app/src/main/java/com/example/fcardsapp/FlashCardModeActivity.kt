package com.example.fcardsapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts

class FlashCardModeActivity : AppCompatActivity() {
    private lateinit var deck: DeckModel
    private lateinit var sqLiteHelper: SQLiteHelper
    private lateinit var tvDeckName: TextView
    private lateinit var tvDeckDescription: TextView
    private lateinit var tvWholeDeckCount: TextView
    private lateinit var tvMistakesStackCount: TextView
    private lateinit var tvCorrectStackCount: TextView
    private lateinit var tvAttempts: TextView
    private lateinit var tvScore: TextView
    private lateinit var tvLastAttempt: TextView
    private lateinit var bWholeDeck: Button
    private lateinit var bMistakes: Button
    private lateinit var bCorrect: Button
    private lateinit var bBack: Button
    private val CORRECT = Constants.STACK_CORRECT
    private val WRONG = Constants.STACK_WRONG
    private val NEUTRAL = Constants.STACK_NEUTRAL

    private var activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if (result.resultCode == RESULT_OK){

        }
        else if(result.resultCode == RESULT_CANCELED){

        }
    }


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash_card_mode)
        initViews()

        val deckId = intent.getStringExtra(Constants.DECK_ID_INTENT_KEY)
        sqLiteHelper = SQLiteHelper(this)

        if (deckId == null||sqLiteHelper.getDeck(deckId!!) == null){
            Toast.makeText(this, "ERROR: Deck not found!", Toast.LENGTH_SHORT)
            return
        }
        deck = sqLiteHelper.getDeck(deckId!!)!!

        tvDeckName.text = deck.deckName
        tvDeckDescription.text = deck.deckDescription

        val correctCount = sqLiteHelper.getCardsFromDeckAndStack(deck.id, CORRECT).size
        val wrongCount = sqLiteHelper.getCardsFromDeckAndStack(deck.id, WRONG).size
        val wholeCount = sqLiteHelper.getAllCardsFromDeck(deck.id).size
        tvWholeDeckCount.text = wholeCount.toString()
        tvMistakesStackCount.text = wrongCount.toString()
        tvCorrectStackCount.text = correctCount.toString()

        //Disable Buttons if stack associated contains no cards.
        if (wrongCount == 0 ){
            bMistakes.isEnabled = false
        }

        if (correctCount == 0){
            bCorrect.isEnabled = false
        }

        if (wholeCount == 0){
            bWholeDeck.isEnabled = false
        }

        val bestAttemptScore = sqLiteHelper.getBestAttemptScoreOfDeck(deck.id)
        val accuracy = safeDivide(correctCount,wholeCount)
        val latestAttempt = sqLiteHelper.getLatestAttempt(deck.id)
        val latestAttemptValue = latestAttempt?.dateTime ?: "N/A"
        val latestScore = latestAttempt?.score ?: 0
        val latestTotal = latestAttempt?.total ?: 0
        val latestAttemptType = latestAttempt?.stack ?: ""
        val latestScoreValue = "$latestScore/$latestTotal ($latestAttemptType)"
        val attemptCount = sqLiteHelper.getAllAttemptsOnDeck(deck.id).size


        tvAttempts.text = attemptCount.toString()
        tvScore.text = latestScoreValue
        tvLastAttempt.text = latestAttemptValue
        val intent = Intent(this, FlashCardActivity::class.java)
        intent.putExtra(Constants.DECK_OBJECT_INTENT_KEY, deck)

        bWholeDeck.setOnClickListener {
            intent.putExtra(Constants.STACK_INTENT_KEY,NEUTRAL)
            activityResultLauncher.launch(intent)
        }

        bMistakes.setOnClickListener {
            intent.putExtra(Constants.STACK_INTENT_KEY,WRONG)
            activityResultLauncher.launch(intent)
        }

        bCorrect.setOnClickListener {
            intent.putExtra(Constants.STACK_INTENT_KEY,CORRECT)
            activityResultLauncher.launch(intent)
        }

        bBack.setOnClickListener {
            finish()
        }

    }

    override fun onRestart() {
        super.onRestart()
        finish()
        startActivity(intent)
    }

    private fun initViews(){
        tvDeckName = findViewById(R.id.tvFlashCardDeckName)
        tvDeckDescription = findViewById(R.id.tvFlashCardDeckDescription)
        tvWholeDeckCount = findViewById(R.id.tvFlashCardCountWholeDeckValue)
        tvMistakesStackCount = findViewById(R.id.tvFlashCardCountMistakesValue)
        tvCorrectStackCount = findViewById(R.id.tvFlashCardCountCorrectValue)
        tvAttempts = findViewById(R.id.tvFlashCardSetAttemptCountValue)
        tvScore = findViewById(R.id.tvFlashCardSetLastScoreValue)
        tvLastAttempt = findViewById(R.id.tvFlashCardLastAttempt)
        bWholeDeck = findViewById(R.id.bWholeDeck)
        bCorrect = findViewById(R.id.bCorrect)
        bMistakes = findViewById(R.id.bMistakes)
        bBack = findViewById(R.id.bBack)
    }

    private fun safeDivide(numerator: Int, denominator: Int): Int{
        var output = 0
        try {
            return numerator/denominator
        }catch (e: java.lang.ArithmeticException){
            return output
        }
    }
}