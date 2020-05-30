package com.sports.sportsflashes.model

data class HomeScreenData(val name: String)
data class Language(
    val `data`: List<Data>,
    val error: Boolean,
    val message: String
)

data class Data(
    val id: String,
    val name: String
)