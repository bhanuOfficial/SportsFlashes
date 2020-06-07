package com.sports.sportsflashes.model

data class HomeScreenData(val name: String)
data class FeaturedShows(
    val __v: Int,
    val _id: String,
    val category: String,
    val comments: List<Any>,
    val createdAt: String,
    val description: String,
    val episodes: List<Any>,
    val featured: Boolean,
    val likes: Int,
    val original: Boolean,
    val seasons: List<String>,
    val tags: List<Any>,
    val thumbnail: String,
    val title: String,
    val updatedAt: String
)
data class SportCategories(
    val __v: Int,
    val _id: String,
    val category: String,
    val createdAt: String,
    val updatedAt: String
)
