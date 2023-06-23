package com.example.fcardsapp

data class DeckModel(
    var id: String = generateAlphaNumericId(8),
    var deckName: String = "",
    var deckDescription: String = ""
) : java.io.Serializable


