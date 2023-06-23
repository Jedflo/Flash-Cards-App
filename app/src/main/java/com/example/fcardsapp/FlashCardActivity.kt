package com.example.fcardsapp

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.animation.doOnEnd

class FlashCardActivity : AppCompatActivity() {

    private lateinit var mySQLiteHelper: SQLiteHelper
    private lateinit var cards: ArrayList<CardModel>
    private lateinit var frontAnimation: AnimatorSet
    private lateinit var backAnimation: AnimatorSet
    private lateinit var tvQuestion: TextView
    private lateinit var tvAnswer: TextView
    private lateinit var ibCorrect: ImageButton
    private lateinit var ibWrong: ImageButton

    var isFront = true
    val DECK_OBJ_KEY = Constants.DECK_OBJECT_INTENT_KEY
    var CARD_STEP = 0
    var deckTotal = 0
    var deckAnsweredCorrect = 0
    var deckAnsweredWrong = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash_card)
        initViews()

        mySQLiteHelper = SQLiteHelper(this)

//        val deck = intent.getSerializableExtra(DECK_OBJ_KEY, DeckModel::class.java)
        val deck = intent.getSerializableExtra(DECK_OBJ_KEY) as DeckModel
        val stack = intent.getStringExtra(Constants.STACK_INTENT_KEY)
        val flashCardEndIntent = Intent(this, FlashCardEndActivity::class.java)
        flashCardEndIntent.putExtra(Constants.DECK_OBJECT_INTENT_KEY, deck)

        if (deck == null){
            Log.e("Missing Deck", "ERROR: Deck is null")
            Toast.makeText(this, "ERROR: Deck is null", Toast.LENGTH_SHORT).show()
            return
        }

        if (stack == null){
            Log.e("Missing Stack", "ERROR: Stack is null")
            Toast.makeText(this, "ERROR: Stack is null", Toast.LENGTH_SHORT).show()
            return
        }

        cards =
            if (stack!! != Constants.STACK_NEUTRAL)
                mySQLiteHelper.getCardsFromDeckAndStack(deck!!.id,stack!!)
            else
                mySQLiteHelper.getAllCardsFromDeck(deck!!.id)

        deckTotal = cards.size
        val cardIterator: ListIterator<CardModel> = cards.listIterator()

        var card: CardModel? = null
        if (cardIterator.hasNext()){
            card = cardIterator.next()
        }

        tvQuestion.text = card!!.question
        tvAnswer.text = card!!.answer

        backAnimation.doOnEnd {
            if (CARD_STEP==1){
                Log.e("TRIGGERED DO ON END", CARD_STEP.toString())
                CARD_STEP--
                tvAnswer.text = card!!.answer
                ibCorrect.visibility = ImageView.INVISIBLE
                ibWrong.visibility = ImageView.INVISIBLE
            }
        }

        tvQuestion.setOnClickListener {
            if(isFront)
            {
                frontAnimation.setTarget(tvQuestion);
                backAnimation.setTarget(tvAnswer);
                frontAnimation.start()
                backAnimation.start()
                isFront = false
                ibCorrect.visibility = ImageView.VISIBLE
                ibWrong.visibility = ImageView.VISIBLE

            }
            else
            {
                frontAnimation.setTarget(tvAnswer)
                backAnimation.setTarget(tvQuestion)
                backAnimation.start()
                frontAnimation.start()
                isFront =true
            }
        }

        ibCorrect.setOnClickListener {
            deckAnsweredCorrect++
            answerFlipCard("Correct")
            card!!.stack = Constants.STACK_CORRECT
            mySQLiteHelper.updateCard(card!!)
            if (!cardIterator.hasNext()){
                startFlashCardEndActivity(flashCardEndIntent,stack)
                return@setOnClickListener
            }
            Log.e("FELL THROUGH:", "continued beyond if")
            card = cardIterator.next()
            tvQuestion.text = card!!.question
        }

        ibWrong.setOnClickListener {
            deckAnsweredWrong++
            answerFlipCard("Wrong")
            card!!.stack = Constants.STACK_WRONG
            mySQLiteHelper.updateCard(card!!)
            if (!cardIterator.hasNext()){
                startFlashCardEndActivity(flashCardEndIntent, stack)
                return@setOnClickListener
            }
            card = cardIterator.next()
            tvQuestion.text = card!!.question
        }

    }

    private fun startFlashCardEndActivity(flashCardEndIntent: Intent, stack: String){
        flashCardEndIntent.putExtra(Constants.DECK_TOTAL, deckTotal.toFloat())
        flashCardEndIntent.putExtra(Constants.DECK_ANSWERED_CORRECT, deckAnsweredCorrect.toFloat())
        flashCardEndIntent.putExtra(Constants.DECK_ANSWERED_WRONG, deckAnsweredWrong.toFloat())
        flashCardEndIntent.putExtra(Constants.STACK_INTENT_KEY,stack)
        startActivity(flashCardEndIntent)
        finish()
    }

    fun answerFlipCard(logMessage: String){
        CARD_STEP++
        Toast.makeText(this, logMessage, Toast.LENGTH_SHORT ).show()
        Log.e("logMessage", CARD_STEP.toString())
        frontAnimation.setTarget(tvAnswer)
        backAnimation.setTarget(tvQuestion)
        backAnimation.start()
        frontAnimation.start()
        isFront =true
    }



    fun initViews(){
        var scale = applicationContext.resources.displayMetrics.density
        tvQuestion = findViewById(R.id.card_front)
        tvAnswer = findViewById(R.id.card_back)
        frontAnimation = AnimatorInflater.loadAnimator(applicationContext, R.animator.front_animator) as AnimatorSet
        backAnimation = AnimatorInflater.loadAnimator(applicationContext, R.animator.back_animator) as AnimatorSet
        tvQuestion.cameraDistance = 8000 * scale
        tvAnswer.cameraDistance = 8000 * scale

        ibCorrect = findViewById(R.id.ibCorrectCard)
        ibWrong = findViewById(R.id.ibWrongCard)

    }

}
