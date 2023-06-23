package com.example.fcardsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class FlashCardEndActivity : AppCompatActivity() {
    private lateinit var mySQLiteHelper: SQLiteHelper
    private lateinit var tvDeckTitleFinished: TextView
    private lateinit var tvDeckStatistics: TextView
    private lateinit var tvDeckPercentage: TextView
    private lateinit var bContinue: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash_card_end)
        mySQLiteHelper = SQLiteHelper(this)
        initViews()

        val deck = intent.getSerializableExtra(Constants.DECK_OBJECT_INTENT_KEY) as DeckModel
        val deckTotal = intent.getFloatExtra(Constants.DECK_TOTAL,0f)
        val deckCorrect = intent.getFloatExtra(Constants.DECK_ANSWERED_CORRECT,0f)
        val deckWrong = intent.getFloatExtra(Constants.DECK_ANSWERED_WRONG,0f)
        val stackAttempted = intent.getStringExtra(Constants.STACK_INTENT_KEY)

        if (deck==null){
            Toast.makeText(this,"ERROR: NO DECK FOUND!", Toast.LENGTH_SHORT).show()
            finish()
        }

        val deckStats =
            "Total Number of Cards: ${deckTotal.roundToInt()} \n " +
                    "Correct: ${deckCorrect.roundToInt()} \n " +
                    "Wrong: ${deckWrong.roundToInt()}"

        val percentage = (deckCorrect/deckTotal)*100

        tvDeckTitleFinished.text = "Finished ${deck!!.deckName}"
        tvDeckStatistics.text = deckStats
        tvDeckPercentage.text = "${percentage.roundToInt()}%"
        var attemptNumber = 1L

        val latestAttempt = mySQLiteHelper.getLatestAttempt(deck.id)

        if (latestAttempt!=null){
            attemptNumber = latestAttempt.attemptNumber+1
        }

        val idSuffix = attemptIdSuffix.format(attemptNumber)
        val attemptId = "${deck.id}$idSuffix"
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm")
        val attemptDate = sdf.format(Date())

        val attempt = DeckAttemptModel(
            attemptId,
            deckCorrect.toLong(),
            deckTotal.toLong(),
            attemptDate,
            deck.id,
            stackAttempted.toString(),
            attemptNumber
        )

        mySQLiteHelper.insertAttempt(attempt)

        Log.e("Attempt Date","$attemptDate")

        bContinue.setOnClickListener {
            finish()
        }

    }

    private fun initViews(){
        tvDeckTitleFinished = findViewById(R.id.tvFlashCardEndFinishedDeckTitleValue)
        tvDeckStatistics = findViewById(R.id.tvFlashCardEndStatsValue)
        tvDeckPercentage = findViewById(R.id.tvFlashCardEndPercentage)
        bContinue = findViewById(R.id.bContinue)
    }
}