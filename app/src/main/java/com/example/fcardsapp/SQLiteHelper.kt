package com.example.fcardsapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteStatement
import android.util.Log


class SQLiteHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME,
    null, DATABASE_VERSION) {
    companion object{
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "flash_cards.db"

        private const val TBL_DECK = "tbl_deck"
        private const val DECK_ID = "deck_id"
        private const val DECK_NAME = "deck_name"
        private const val DECK_DESC = "deck_description"

        private const val TBL_CARDS = "tbl_card"
        private const val CARD_ID = "card_id"
        private const val QUESTION = "question"
        private const val ANSWER = "answer"
        //DECK ID
        private const val STACK = "stack"
        private const val TIMES_CORRECT = "times_correct"
        private const val TIMES_WRONG = "times_wrong"

        private const val TBL_SCORE_HISTORY = "tbl_score_history"
        private const val ATTEMPT_ID = "attempt_id"
        private const val SCORE = "score"
        private const val TOTAL = "total"
        private const val DATETIME_OF_ATTEMPT = "datetime_of_attempt"
        //DECK ID
        //STACK
        private const val ATTEMPT_NUMBER = "attempt_number"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableDeck = ("CREATE TABLE " + TBL_DECK + "("
                + DECK_ID + " TEXT PRIMARY KEY, "
                + DECK_NAME + " TEXT, "
                + DECK_DESC + " TEXT "
                +")"
                )

        val createTableCard = ("CREATE TABLE " + TBL_CARDS + "("
                + CARD_ID + " TEXT PRIMARY KEY, "
                + QUESTION + " TEXT, "
                + ANSWER + " TEXT, "
                + DECK_ID + " TEXT, "
                + STACK + " TEXT, "
                + TIMES_CORRECT + " INTEGER, "
                + TIMES_WRONG + " INTEGER "
                +")"
                )

        val createTableScoreHistory = ("CREATE TABLE " + TBL_SCORE_HISTORY + "("
                + ATTEMPT_ID + " TEXT PRIMARY KEY, "
                + SCORE + " INTEGER, "
                + TOTAL + " INTEGER, "
                + DATETIME_OF_ATTEMPT + " TEXT, "
                + DECK_ID + " TEXT, "
                + STACK + " TEXT, "
                + ATTEMPT_NUMBER + " INTEGER "
                +")"
                )

        db?.execSQL(createTableDeck)
        db?.execSQL(createTableCard)
        db?.execSQL(createTableScoreHistory)
    }

    //TODO UPDATE ON UPGRADE
    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TBL_DECK")
        db!!.execSQL("DROP TABLE IF EXISTS $TBL_CARDS")
        db!!.execSQL("DROP TABLE IF EXISTS $TBL_SCORE_HISTORY")
        onCreate(db)
    }

    fun insertDeck(deck: DeckModel): Long{
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(DECK_ID, deck.id)
        contentValues.put(DECK_NAME, deck.deckName)
        contentValues.put(DECK_DESC, deck.deckDescription)

        val success = db.insert(TBL_DECK, null, contentValues)
        db.close()
        return success
    }

    fun updateDeck(deck: DeckModel): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DECK_ID, deck.id)
        contentValues.put(DECK_NAME, deck.deckName)
        contentValues.put(DECK_DESC, deck.deckDescription)

        val success = db.update(
            TBL_DECK,
            contentValues,
            "$DECK_ID = ?",
            arrayOf(deck.id)
        )

        db.close()
        return success
    }

    fun deleteDeck(deck: DeckModel): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DECK_ID, deck.id)
        val success = db.delete(TBL_DECK, "$DECK_ID = ?", arrayOf(deck.id))
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun getAllDecks(): ArrayList<DeckModel>{
        val deckList: ArrayList<DeckModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_DECK"
        val db = this.writableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: java.lang.Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: String
        var name: String
        var description: String
        if (cursor.moveToFirst()){
            do{
                id = cursor.getString(cursor.getColumnIndex(DECK_ID))
                name = cursor.getString(cursor.getColumnIndex(DECK_NAME))
                description = cursor.getString(cursor.getColumnIndex(DECK_DESC))
                val deck = DeckModel(id, name, description)
                deckList.add(deck)
            }while (cursor.moveToNext())
        }
        return deckList
    }

    @SuppressLint("Range")
    fun getDeck(deckId: String): DeckModel?{
        val selectQuery = "SELECT * FROM $TBL_DECK WHERE $DECK_ID = '$deckId'"
        val db = this.writableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: java.lang.Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return null
        }

        val id: String
        val name: String
        val description: String
        if (cursor.moveToFirst()){
            id = cursor.getString(cursor.getColumnIndex(DECK_ID))
            name = cursor.getString(cursor.getColumnIndex(DECK_NAME))
            description = cursor.getString(cursor.getColumnIndex(DECK_DESC))
            return DeckModel(id, name, description)
        }
        return null
    }

    fun insertCard(card: CardModel): Long{
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(CARD_ID, card.id)
        contentValues.put(QUESTION, card.question)
        contentValues.put(ANSWER, card.answer)
        contentValues.put(DECK_ID, card.deckId)
        contentValues.put(STACK, card.stack)
        contentValues.put(TIMES_CORRECT, card.timesCorrect)
        contentValues.put(TIMES_WRONG, card.timesWrong)

        val success = db.insert(TBL_CARDS, null, contentValues)
        db.close()
        return success
    }

    fun updateCard(card: CardModel): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(CARD_ID, card.id)
        contentValues.put(QUESTION, card.question)
        contentValues.put(ANSWER, card.answer)
        contentValues.put(DECK_ID, card.deckId)
        contentValues.put(STACK, card.stack)
        contentValues.put(TIMES_CORRECT, card.timesCorrect)
        contentValues.put(TIMES_WRONG, card.timesWrong)

        val success = db.update(
            TBL_CARDS,
            contentValues,
            "$CARD_ID = ?",
            arrayOf(card.id)
        )

        db.close()
        return success
    }

    fun deleteCard(card: CardModel): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(CARD_ID, card.id)
        val success = db.delete(
            TBL_CARDS,
            "$CARD_ID = ?",
            arrayOf(card.id)
        )
        db.close()
        return success
    }

    fun deleteCard(cardId: String): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(CARD_ID, cardId)
        val success = db.delete(
            TBL_CARDS,
            "$CARD_ID = ?",
            arrayOf(cardId)
        )
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun getAllCardsFromDeck(deckId: String): ArrayList<CardModel>{
        val cardList: ArrayList<CardModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_CARDS WHERE $DECK_ID = '$deckId'"
        val db = this.writableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery,null)
        }catch (e: java.lang.Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: String
        var question: String
        var answer: String
        var stack: String
        var timesCorrect: Int
        var timesWrong: Int

        if (cursor.moveToFirst()){
            do{
                id = cursor.getString(cursor.getColumnIndex(CARD_ID))
                question = cursor.getString(cursor.getColumnIndex(QUESTION))
                answer = cursor.getString(cursor.getColumnIndex(ANSWER))
                stack = cursor.getString(cursor.getColumnIndex(STACK))
                timesCorrect = cursor.getInt(cursor.getColumnIndex(TIMES_CORRECT))
                timesWrong = cursor.getInt(cursor.getColumnIndex(TIMES_WRONG))
                val card = CardModel(id, question, answer, deckId, stack, timesCorrect, timesWrong)
                cardList.add(card)
            }while (cursor.moveToNext())
        }
        return cardList
    }

    @SuppressLint("Range")
    fun getAllCardsFromDeckWhereQuestionLike(deckId: String, pattern: String): ArrayList<CardModel>{
        val cardList: ArrayList<CardModel> = ArrayList()
        val selectQuery =
                "SELECT * " +
                "FROM $TBL_CARDS WHERE $DECK_ID = '$deckId' " +
                "AND $QUESTION LIKE '$pattern%' " +
                "GROUP BY $QUESTION " + "ORDER BY COUNT(*) ASC"
        val db = this.writableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery,null)
        }catch (e: java.lang.Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: String
        var question: String
        var answer: String
        var stack: String
        var timesCorrect: Int
        var timesWrong: Int

        if (cursor.moveToFirst()){
            do{
                id = cursor.getString(cursor.getColumnIndex(CARD_ID))
                question = cursor.getString(cursor.getColumnIndex(QUESTION))
                answer = cursor.getString(cursor.getColumnIndex(ANSWER))
                stack = cursor.getString(cursor.getColumnIndex(STACK))
                timesCorrect = cursor.getInt(cursor.getColumnIndex(TIMES_CORRECT))
                timesWrong = cursor.getInt(cursor.getColumnIndex(TIMES_WRONG))
                val card = CardModel(id, question, answer, deckId, stack, timesCorrect, timesWrong)
                cardList.add(card)
            }while (cursor.moveToNext())
        }
        return cardList
    }

    @SuppressLint("Range")
    fun getAllCardsFromDeckWhereAnswerLike(deckId: String, pattern: String): ArrayList<CardModel>{
        val cardList: ArrayList<CardModel> = ArrayList()
        val selectQuery =
                    "SELECT * " +
                    "FROM $TBL_CARDS WHERE $DECK_ID = '$deckId' " +
                    "AND $ANSWER LIKE '$pattern%' " +
                    "GROUP BY $QUESTION " + "ORDER BY COUNT(*) ASC"
        val db = this.writableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery,null)
        }catch (e: java.lang.Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: String
        var question: String
        var answer: String
        var stack: String
        var timesCorrect: Int
        var timesWrong: Int

        if (cursor.moveToFirst()){
            do{
                id = cursor.getString(cursor.getColumnIndex(CARD_ID))
                question = cursor.getString(cursor.getColumnIndex(ANSWER))
                answer = cursor.getString(cursor.getColumnIndex(QUESTION))
                stack = cursor.getString(cursor.getColumnIndex(STACK))
                timesCorrect = cursor.getInt(cursor.getColumnIndex(TIMES_CORRECT))
                timesWrong = cursor.getInt(cursor.getColumnIndex(TIMES_WRONG))
                val card = CardModel(id, question, answer, deckId, stack, timesCorrect, timesWrong)
                cardList.add(card)
            }while (cursor.moveToNext())
        }
        return cardList
    }

    /**
     * Fetches all cards from a deck given it's ID. But flips the Question and Answer values
     * assigned to each object in the ArrayList.
     * Example CardModel{question = answerSQLresult, answer = questionSQLresult}
     * @param deckId Deck Id
     * @return Arraylist of CardModels
     */
    @SuppressLint("Range")
    fun getAllCardsFromDeckFlipQnA(deckId: String): ArrayList<CardModel>{
        val cardList: ArrayList<CardModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_CARDS WHERE $DECK_ID = '$deckId'"
        val db = this.writableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery,null)
        }catch (e: java.lang.Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: String
        var question: String
        var answer: String
        var stack: String
        var timesCorrect: Int
        var timesWrong: Int

        if (cursor.moveToFirst()){
            do{
                id = cursor.getString(cursor.getColumnIndex(CARD_ID))
                question = cursor.getString(cursor.getColumnIndex(ANSWER))
                answer = cursor.getString(cursor.getColumnIndex(QUESTION))
                stack = cursor.getString(cursor.getColumnIndex(STACK))
                timesCorrect = cursor.getInt(cursor.getColumnIndex(TIMES_CORRECT))
                timesWrong = cursor.getInt(cursor.getColumnIndex(TIMES_WRONG))
                val card = CardModel(id, question, answer, deckId, stack, timesCorrect, timesWrong)
                cardList.add(card)
            }while (cursor.moveToNext())
        }
        return cardList
    }

    @SuppressLint("Range")
    fun getAllCards(): ArrayList<CardModel>{
        val cardList: ArrayList<CardModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_CARDS"
        val db = this.writableDatabase
        val cursor: Cursor?

        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: java.lang.Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: String
        var question: String
        var answer: String
        var deckId: String
        var stack: String
        var timesCorrect: Int
        var timesWrong: Int

        if(cursor.moveToFirst()){
            do {
                id = cursor.getString(cursor.getColumnIndex(CARD_ID))
                question = cursor.getString(cursor.getColumnIndex(QUESTION))
                answer = cursor.getString(cursor.getColumnIndex(ANSWER))
                deckId = cursor.getString(cursor.getColumnIndex(DECK_ID))
                stack = cursor.getString(cursor.getColumnIndex(STACK))
                timesCorrect = cursor.getInt(cursor.getColumnIndex(TIMES_CORRECT))
                timesWrong = cursor.getInt(cursor.getColumnIndex(TIMES_WRONG))
                val card = CardModel(id, question, answer, deckId, stack, timesCorrect, timesWrong)
                cardList.add(card)
            }while (cursor.moveToNext())
        }
        return cardList
    }

    fun insertAttempt(attempt: DeckAttemptModel): Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ATTEMPT_ID, attempt.id)
        contentValues.put(SCORE,attempt.score)
        contentValues.put(TOTAL, attempt.total)
        contentValues.put(DATETIME_OF_ATTEMPT, attempt.dateTime)
        contentValues.put(DECK_ID, attempt.deckId)
        contentValues.put(STACK, attempt.stack)
        contentValues.put(ATTEMPT_NUMBER, attempt.attemptNumber)
        val success = db.insert(TBL_SCORE_HISTORY, null, contentValues)
        db.close()
        return success
    }

    fun updateAttempt(attempt: DeckAttemptModel): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ATTEMPT_ID, attempt.id)
        contentValues.put(SCORE,attempt.score)
        contentValues.put(TOTAL, attempt.total)
        contentValues.put(DATETIME_OF_ATTEMPT, attempt.dateTime)
        contentValues.put(DECK_ID, attempt.deckId)
        contentValues.put(STACK, attempt.stack)

        val success = db.update(
            TBL_SCORE_HISTORY,
            contentValues,
            "$ATTEMPT_ID = ?",
            arrayOf(attempt.id)
        )
        db.close()
        return success
    }

    fun deleteAttempt(attemptId: String): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ATTEMPT_ID, attemptId)
        val success = db.delete(
            TBL_SCORE_HISTORY,
            "$ATTEMPT_ID = '$attemptId'",
            null
        )
        db.close()
        return success
    }

    fun getNumberOfAttemptsOnDeck(deckId: String): Long{
        val selectQuery = "SELECT COUNT(*) FROM $TBL_SCORE_HISTORY WHERE $DECK_ID = $deckId"
        val db = this.writableDatabase
        val statement: SQLiteStatement = db.compileStatement(selectQuery)
        return statement.simpleQueryForLong()
    }


    fun getLatestAttemptNumber(deckId: String): Long{
        val selectQuery = "SELECT MAX($ATTEMPT_NUMBER) FROM $TBL_SCORE_HISTORY " +
                "WHERE $DECK_ID = $deckId"
        val db = this.writableDatabase
        val statement: SQLiteStatement = db.compileStatement(selectQuery)
        return statement.simpleQueryForLong()
    }

    @SuppressLint("Range")
    fun getLatestAttempt(deckId: String): DeckAttemptModel? {
        val selectQuery =
            "SELECT * FROM $TBL_SCORE_HISTORY WHERE $DECK_ID = '$deckId' ORDER BY $ATTEMPT_NUMBER DESC"
//        val arguements = arrayOf(,ATTEMPT_NUMBER)
        val db = this.writableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        val hasValues = cursor.moveToFirst()
        if (!hasValues){
            return null
        }
        return DeckAttemptModel(
            cursor.getString(cursor.getColumnIndex(ATTEMPT_ID)),
            cursor.getLong(cursor.getColumnIndex(SCORE)),
            cursor.getLong(cursor.getColumnIndex(TOTAL)),
            cursor.getString(cursor.getColumnIndex(DATETIME_OF_ATTEMPT)),
            cursor.getString(cursor.getColumnIndex(DECK_ID)),
            cursor.getString(cursor.getColumnIndex(STACK)),
            cursor.getLong(cursor.getColumnIndex(ATTEMPT_NUMBER))
        )
    }

    fun getBestAttemptScoreOfDeck(deck_id: String): Long{
        val selectQuery = "SELECT MAX($SCORE) FROM $TBL_SCORE_HISTORY WHERE $DECK_ID = '$deck_id'"
        val db = this.writableDatabase
        val statement: SQLiteStatement = db.compileStatement(selectQuery)
        return statement.simpleQueryForLong()
    }

    @SuppressLint("Range")
    fun getCardsFromDeckAndStack(deckId: String, stack:String): ArrayList<CardModel>{
        var cardList: ArrayList<CardModel> = ArrayList()
        val query = "SELECT * FROM $TBL_CARDS WHERE $DECK_ID = ? AND $STACK = ?"
        val arguments = arrayOf(deckId, stack)
        val db = this.writableDatabase
        val cursor: Cursor = db.rawQuery(query,arguments)

        val hasResult =  cursor.moveToFirst()
        if (!cursor.moveToFirst()){
            return ArrayList()
        }

        var id: String
        var question: String
        var answer: String
        //deckId
        //stack
        var timesCorrect: Int
        var timesWrong: Int
        var card: CardModel

        do {
            id = cursor.getString(cursor.getColumnIndex(CARD_ID))
            question = cursor.getString(cursor.getColumnIndex(QUESTION))
            answer = cursor.getString(cursor.getColumnIndex(ANSWER))
            timesCorrect = cursor.getInt(cursor.getColumnIndex(TIMES_CORRECT))
            timesWrong = cursor.getInt(cursor.getColumnIndex(TIMES_WRONG))
            card = CardModel(id, question,answer,deckId,stack,timesCorrect,timesWrong)
            cardList.add(card)

        } while (cursor.moveToNext())
        return cardList
    }

    @SuppressLint("Range")
    fun getAllAttemptsOnDeck(deckId: String): ArrayList<DeckAttemptModel>{
        val selectQuery = "SELECT * FROM $TBL_SCORE_HISTORY WHERE $DECK_ID = '$deckId' ORDER BY $ATTEMPT_NUMBER DESC"
        val db = this.writableDatabase
        var attemptList = arrayListOf<DeckAttemptModel>()
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery,null)
        }catch (e: java.lang.Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return attemptList
        }

        var id: String
        var score: Int
        var total: Int
        var dateTime: String
        var deckId: String
        var stack: String
        var attemptNumber: Int

        if (cursor.moveToFirst()){
            do{
                id = cursor.getString(cursor.getColumnIndex(ATTEMPT_ID))
                score = cursor.getInt(cursor.getColumnIndex(SCORE))
                total = cursor.getInt(cursor.getColumnIndex(TOTAL))
                dateTime = cursor.getString(cursor.getColumnIndex(DATETIME_OF_ATTEMPT))
                deckId = cursor.getString(cursor.getColumnIndex(DECK_ID))
                stack = cursor.getString(cursor.getColumnIndex(STACK))
                attemptNumber = cursor.getInt(cursor.getColumnIndex(ATTEMPT_NUMBER))
                val attempt = DeckAttemptModel(
                    id,
                    score.toLong(),
                    total.toLong(),
                    dateTime,
                    deckId,
                    stack,
                    attemptNumber.toLong()
                )
                attemptList.add(attempt)
            }while (cursor.moveToNext())
        }
        return attemptList
    }

    @SuppressLint("Range")
    fun getAllAttemptsOnDeckThenLog(deckId: String){
        val selectQuery = "SELECT * FROM $TBL_SCORE_HISTORY WHERE $DECK_ID = '$deckId' ORDER BY $ATTEMPT_NUMBER DESC"
        val db = this.writableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery,null)
        }catch (e: java.lang.Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return
        }

        var id: String
        var score: Int
        var total: Int
        var dateTime: String
        var deckId: String
        var stack: String
        var attemptNumber: Int

        if (cursor.moveToFirst()){
            do{
                id = cursor.getString(cursor.getColumnIndex(ATTEMPT_ID))
                score = cursor.getInt(cursor.getColumnIndex(SCORE))
                total = cursor.getInt(cursor.getColumnIndex(TOTAL))
                dateTime = cursor.getString(cursor.getColumnIndex(DATETIME_OF_ATTEMPT))
                deckId = cursor.getString(cursor.getColumnIndex(DECK_ID))
                stack = cursor.getString(cursor.getColumnIndex(STACK))
                attemptNumber = cursor.getInt(cursor.getColumnIndex(ATTEMPT_NUMBER))
                Log.d("CONTENTS of Attempt $id","$score, $total, $dateTime, $deckId, $stack, $attemptNumber")
            }while (cursor.moveToNext())
        }

    }


}