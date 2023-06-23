package com.example.fcardsapp

import android.content.ContentResolver
import android.net.Uri
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import java.io.BufferedReader
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Reads a CSV file that contains 2 columns, one for question and one for answer.
 * This then creates a CardModel related to the provided deckId for each row retrieved in the CSV
 * file.
 * @param csvFilePath   complete file path of the CSV file to be read.
 * @param deckId        deck ID where the Cards will be associated.
 */
fun readQnAFromCsv(csvReader: BufferedReader, deckId: String):ArrayList<CardModel>{
    val csvParser = CSVParser(csvReader, CSVFormat.DEFAULT)
    val retrievedCards = arrayListOf<CardModel>()
    for (csvRecord in csvParser){
        val question = csvRecord.get(0).trim()
        val answer = csvRecord.get(1).trim()

        if (question.isNullOrBlank()||answer.isNullOrBlank()){
            continue
        }
        val newCard = CardModel(
            deckId+ generateAlphaNumericId(4),
            question,
            answer,
            deckId,
            Constants.STACK_NEUTRAL,
            0,
            0
        )
        retrievedCards.add(newCard)
    }
    return retrievedCards
}

fun writeQnAToCsv(csvFilePath: String, csvFileName: String, cardList: ArrayList<CardModel>){
    val writer = Files.newBufferedWriter(Paths.get("$csvFilePath\\$csvFileName"))
    val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT)
    for (card in cardList){
        csvPrinter.printRecord(card.question,card.answer)
    }
    csvPrinter.flush()
    csvPrinter.close()
}