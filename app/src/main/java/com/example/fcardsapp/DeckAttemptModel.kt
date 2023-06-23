package com.example.fcardsapp

data class DeckAttemptModel(
    var id: String,
    var score: Long,
    var total: Long,
    var dateTime: String,
    var deckId: String,
    var stack: String,
    var attemptNumber: Long
)
