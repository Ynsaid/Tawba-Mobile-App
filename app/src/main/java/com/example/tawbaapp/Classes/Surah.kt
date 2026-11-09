package com.example.tawbaapp.Classes
data class Verse(
    val id: Int,
    val text: String,
    val translation: String
)


data class Surah(
    val id: Int,
    val name: String,
    val transliteration: String,
    val translation: String,
    val type: String,
    val totalverses: Int,
    val verses: List<Verse>
)
