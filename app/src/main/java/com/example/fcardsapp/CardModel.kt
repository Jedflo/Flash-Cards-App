package com.example.fcardsapp

data class CardModel(
    var id: String,
    var question: String,
    var answer: String,
    var deckId: String,
    var stack: String,
    var timesCorrect: Int,
    var timesWrong: Int
)
