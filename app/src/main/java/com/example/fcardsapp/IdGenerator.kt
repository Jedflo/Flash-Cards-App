package com.example.fcardsapp

import java.text.DecimalFormat
import kotlin.streams.asSequence

val attemptIdSuffix= DecimalFormat("0000")


/**
 * Generates an ID using source parameter with length idSize
 * @param source What numbers, letters, and symbols the ID can be composed of.
 * @param idSize Lenght of ID to be generated.
 * @return generated ID.
 */
fun generateId(source: String, idSize: Long): String{
    val out = java.util.Random().ints(idSize, 0, source.length)
        .asSequence().map(source::get).joinToString("")
    return out
}

/**
 * Generates an ID with length idSize. ID generated is alphanumeric
 * @param source What numbers, letters, and symbols the ID can be composed of.
 * @param idSize Length of ID to be generated.
 * @return generated ID.
 */
fun generateAlphaNumericId(idSize: Long): String{
    val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    val out = java.util.Random().ints(idSize, 0, source.length)
        .asSequence().map(source::get).joinToString("")
    return out
}

/**
 * Generates an ID with length of idSize. ID will be composed of letters.
 * @param idSize length of ID to be generated.
 */
fun generateAlphaId(idSize: Long): String{
    val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    val out = java.util.Random().ints(idSize, 0, source.length)
        .asSequence().map(source::get).joinToString("")
    return out
}


/**
 * Generates an ID with length of idSize. ID will be composed of letters.
 * @param idSize length of ID to be generated.
 * @param allCaps if true ID will only compose of capital letters, else lower case letters.
 */
fun generateAlphaId(idSize: Long, allCaps:Boolean): String{

    var source = if (allCaps) "ABCDEFGHIJKLMNOPQRSTUVWXYZ" else "abcdefghijklmnopqrstuvwxyz"

    val out = java.util.Random().ints(idSize, 0, source.length)
        .asSequence().map(source::get).joinToString("")
    return out
}

/**
 * Generates an ID with length of idSize. ID will be composed of numbers
 * @param idSize length of ID to be generated.
 */
fun generateNumericId(idSize: Long): String{
    val source = "0123456789"
    val out = java.util.Random().ints(idSize, 0, source.length)
        .asSequence().map(source::get).joinToString("")
    return out
}





fun main(args : Array<String>){
//    val source = "!@#$%^&*()"
//    println("ID custome source of \" $source \": " + com.example.fcardsapp.generateId(source, 10))
//    println("ID Alphanumeric length 12: " + com.example.fcardsapp.generateAlphaNumericId(12))
//    println("ID Alpha length 6: " + com.example.fcardsapp.generateAlphaId(6))
//    println("ID Alpha length 8 all caps: " + com.example.fcardsapp.generateAlphaId(8, true))
//    println("ID Alpha length 10 all lower case: " + com.example.fcardsapp.generateAlphaId(10, false))
//    println("ID Numeric length 16: " + com.example.fcardsapp.generateNumericId(16))

}