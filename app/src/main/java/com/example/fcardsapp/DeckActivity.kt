package com.example.fcardsapp

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Switch
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toFile
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class DeckActivity : AppCompatActivity() {

    private lateinit var tvFlashCardDeckName: TextView
    private lateinit var tvDeckDescription: TextView
    private lateinit var tvcImport: TextView
    private lateinit var etQuestion: EditText
    private lateinit var etAnswer: EditText
    private lateinit var bAdd: Button
    private lateinit var bSave: Button
    private lateinit var bDelete: Button
    private lateinit var ibEditDeckDetails: ImageButton
    private lateinit var rvCardList: RecyclerView
    private lateinit var cardAdapter: CardAdapter
    private lateinit var cardModel: CardModel
    private lateinit var searchView: SearchView
    private lateinit var swQuestionOrAnswer: Switch
    private var tvCardItem: TextView? = null

    private lateinit var sqLiteHelper: SQLiteHelper
    private lateinit var deckId: String
    private var addMode = true
    private var rvCardListMode = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deck)

        sqLiteHelper = SQLiteHelper(this)
        deckId = intent.getStringExtra(Constants.DECK_ID_INTENT_KEY).toString()
        initViews()
        initRecyclerView()
        val deck = sqLiteHelper.getDeck(deckId)
        tvFlashCardDeckName.text = deck?.deckName
        tvDeckDescription.text = deck?.deckDescription


        cardAdapter.setOnClickItem {
            /* The check for card list mode is included because of the logic of the show question
            or answer in the card recycler view. When recycler view mode is in question mode, it
            uses the method getAllCardsFromDeckFlipQnA() which returns a list of card object but
            the question field contains the answer and the answer field contains the question.
             */
            if (rvCardListMode){
                etQuestion.setText(it.answer)
                etAnswer.setText(it.question)
            }else{
                etQuestion.setText(it.question)
                etAnswer.setText(it.answer)
            }
            addMode = false
            bSave.isEnabled = true
            bDelete.isEnabled = true
            isAddMode(false)
            cardModel = it
        }

        //NOT WORKING
        cardAdapter.setOnLongClickItem {
            val cardContents = tvCardItem?.text.toString()
            val newCardContents = if(cardContents.equals(it.question)) it.answer else it.question
            tvCardItem?.text = newCardContents
        }

        bAdd.setOnClickListener {
            if (addMode){
                insertCard()
                return@setOnClickListener
            }
            isAddMode(true)
            clearAllEditTexts()
            getCards()
        }

        bSave.setOnClickListener {
            updateCard()
        }

        bDelete.setOnClickListener {
            deleteCard(cardModel.id)
        }

        ibEditDeckDetails.setOnClickListener {
            goToCreateDeck()
        }

        tvcImport.setOnClickListener {
            var importFile = Intent(Intent.ACTION_GET_CONTENT)
            importFile.type = "*/*"
            val mimeTypes = arrayOf("text/csv","text/comma-separated-values","application/csv")
            importFile.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            importFile = Intent.createChooser(importFile,"Import Cards from CSV")
            startActivityForResult(importFile, IMPORT_REQUEST_CODE)
        }

        searchView.clearFocus()
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val queryResult: ArrayList<CardModel>

                if (newText.isNullOrEmpty()){
                    return true
                }
                queryResult = if (swQuestionOrAnswer.isChecked){
                    sqLiteHelper.getAllCardsFromDeckWhereAnswerLike(deckId,newText)
                } else{
                    sqLiteHelper.getAllCardsFromDeckWhereQuestionLike(deckId,newText)
                }
                Log.d("Search Query Size:", queryResult.size.toString())
                cardAdapter?.addCards(queryResult)
                return true
            }

        })

        swQuestionOrAnswer.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                rvCardListMode = true
                swQuestionOrAnswer.text = "Show: Answers"
                getCards()
            }
            else{
                rvCardListMode = false
                swQuestionOrAnswer.text = "Show: Questions"
                getCards()
            }
        }




    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == IMPORT_REQUEST_CODE){
            val cardsCsvUri = data?.data ?: return
            val stream = contentResolver.openInputStream(cardsCsvUri)
            val reader = BufferedReader(InputStreamReader(stream))
            val retrievedCards = readQnAFromCsv(reader,deckId)
            Toast.makeText(this,"$retrievedCards", Toast.LENGTH_SHORT).show()
            Log.d("Retrieved Cards:", "$retrievedCards")
            for (card in retrievedCards){
                sqLiteHelper.insertCard(card)
            }
            getCards()
        }
    }

    fun getPathFromUriPath(uri:Uri):String?{
        val uriPath = uri.path
        val split = uriPath?.split(":")
        return split?.get(1)
    }

    fun updateCard(){
        val question = etQuestion.text.toString()
        val answer = etAnswer.text.toString()

        if (question.isNullOrEmpty()){
            Toast.makeText(this, "Question cannot be blank.", Toast.LENGTH_SHORT).show()
            return
        }
        if (answer.isNullOrEmpty()){
            Toast.makeText(this, "Answer cannot be blank.", Toast.LENGTH_SHORT).show()
            return
        }

        /*
        Again, Check for rvCardListMode is added because of the logic of the show question or answer
        in the card recycler view.
         */
        if (rvCardListMode){
            if ((question == cardModel.answer && answer == cardModel.question)){
                Toast.makeText(this, "No changes in record.", Toast.LENGTH_SHORT).show()
                return
            }
        }else{
            if ((question == cardModel.question && answer == cardModel.answer)){
                Toast.makeText(this, "No changes in record.", Toast.LENGTH_SHORT).show()
                return
            }
        }



        if (cardModel == null){
            return
        }

        val newCard = CardModel(
            cardModel!!.id,
            question,
            answer,
            cardModel!!.deckId,
            cardModel!!.stack,
            cardModel!!.timesCorrect,
            cardModel!!.timesWrong
        )

        val updateStatus = sqLiteHelper.updateCard(newCard)

        if (updateStatus > -1){
            clearAllEditTexts()
            isAddMode(true)
            getCards()
            Toast.makeText(this,"Update successful", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this,"Error: Update Failed.", Toast.LENGTH_SHORT).show()
        }

    }

    fun deleteCard(id: String){
        if (id == null) return

        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setMessage("Deleting this card cannot be undone. Do you wish to proceed?")
        alertDialog.setCancelable(true)

        alertDialog.setPositiveButton("OK") { dialog, _ ->
            sqLiteHelper.deleteCard(id)
            getCards()
            clearAllEditTexts()
            isAddMode(true)
            dialog.dismiss()
            Toast.makeText(this, "Delete succesful", Toast.LENGTH_SHORT).show()
        }

        alertDialog.setNegativeButton("Cancel"){ dialog, _ ->
            dialog.dismiss()
        }

        val alert = alertDialog.create()
        alert.show()
    }

    fun insertCard(){
        val question = etQuestion.text.toString()
        val answer = etAnswer.text.toString()
        if (question.isEmpty()&&answer.isEmpty()){
            Toast.makeText(
                this,
                "Question and Answer fields cannot be empty.",
                Toast.LENGTH_SHORT
            )
        }
        else if(question.isEmpty()){
            Toast.makeText(
                this,
                "Question cannot be be blank",
                Toast.LENGTH_SHORT
            )
        }else if(answer.isEmpty()){
            Toast.makeText(
                this,
                "Answer cannot be blank",
                Toast.LENGTH_SHORT
            )
        }
        else{
            val cardId = deckId + generateAlphaNumericId(4)
            val question = etQuestion.text.toString()
            val answer = etAnswer.text.toString()
            Log.e("ID GENERATED FOR NEW CARD", cardId)

            val newCard = CardModel(
                cardId,
                question,
                answer,
                deckId,
                Constants.STACK_NEUTRAL,
                0,
                0
            )

            val addStatus = sqLiteHelper.insertCard(newCard)
            if (addStatus > -1){
                Toast.makeText(this, "Card Successfully Added.", Toast.LENGTH_SHORT)
                clearAllEditTexts()
                getCards()
            }
            else{
                Toast.makeText(this, "Card could not be created", Toast.LENGTH_SHORT)
            }
        }
    }

    fun isAddMode(addMode: Boolean){
        if (addMode){
            this.addMode = true
            bSave.isEnabled = false
            bDelete.isEnabled = false
            bAdd.text = Constants.ADD_MODE_LABEL
        }
        else{
            this.addMode = false
            bSave.isEnabled = true
            bDelete.isEnabled = true
            bAdd.text = Constants.NOT_ADD_MODE_LABEL
        }
    }

    fun initViews(){
        tvFlashCardDeckName = findViewById(R.id.tvDeckActivityFlashCardDeckName)
        tvDeckDescription = findViewById(R.id.tvDeckActivityDeckDescription)
        tvcImport = findViewById(R.id.tvcImportCards)
        etQuestion = findViewById(R.id.etDeckActivityQuestion)
        etAnswer = findViewById(R.id.etDeckActivityAnswer)
        bAdd = findViewById(R.id.bDeckActivityAdd)
        bSave = findViewById(R.id.bDeckActivitySave)
        bDelete = findViewById(R.id.bDeckActivityDelete)
        rvCardList = findViewById(R.id.rvDeckActivityQuestions)
        ibEditDeckDetails = findViewById(R.id.ibDeckActivityEditDeckDetails)
        searchView = findViewById(R.id.svCardSearch)
        swQuestionOrAnswer = findViewById(R.id.swQuestionOrAnswer)
        if (findViewById<TextView>(R.id.tvCardItem)==null){
            return
        }
        tvCardItem = findViewById(R.id.tvCardItem)

    }

    fun initRecyclerView(){
        rvCardList.layoutManager = LinearLayoutManager(this)
        cardAdapter = CardAdapter()
        rvCardList.adapter = cardAdapter
        getCards()
    }

    fun getCards(){
        val cardList =
            if (!swQuestionOrAnswer.isChecked) sqLiteHelper.getAllCardsFromDeck(deckId)
            else sqLiteHelper.getAllCardsFromDeckFlipQnA(deckId)
        Log.e("CARD LIST SIZE", "${cardList.size}")
        cardAdapter?.addCards(cardList)
    }

    fun clearAllEditTexts(){
        etQuestion.setText("")
        etAnswer.setText("")
    }

    private var activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result, ->
        //If deck is deleted from activity it will enter if condition.
        if(result.resultCode == RESULT_FIRST_USER) {
            finish()

        }
        else{
            finish();
            startActivity(intent);
        }
    }

    fun goToCreateDeck(){
        val intent = Intent(this, CreateDeckActivity::class.java)
        intent.putExtra(Constants.CREATE_DECK_MODE_INTENT_KEY, Constants.MODE_EDIT)
        intent.putExtra(Constants.DECK_ID_INTENT_KEY, deckId)
        activityResultLauncher.launch(intent)
        }

    companion object RequestCode{
        private const val IMPORT_REQUEST_CODE = 111
    }

}