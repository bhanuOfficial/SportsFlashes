package com.sports.sportsflashes.model

data class FeaturedShows(
    val __v: Int = 0,
    val _id: String = "",
    val categories: List<Any> = listOf(),
    val category: String = "",
    val createdAt: String = "",
    val creator: String = "",
    val description: String = "",
    val duration: Int = 0,
    val featured: Boolean = false,
    val likes: Int = 0,
    val original: Boolean = false,
    val playing: Playing = Playing(),
    val releaseTime: String = "",
    val seasons: List<Season> = listOf(),
    val seasonsEpisodes: List<SeasonsEpisode> = listOf(),
    val thumbnail: String = "",
    val title: String = "",
    val type: String = "",
    val updatedAt: String = ""
) {
    data class Playing(
        val __v: Int = 0,
        val _id: String = "",
        val createdAt: String = "",
        val description: String = "",
        val duration: Int = 0,
        val episodeNumber: Int = 0,
        val link: String = "",
        val live: Boolean = false,
        val releaseTime: String = "",
        val thumbnail: String = "",
        val title: String = "",
        val updatedAt: String = ""
    )

    data class Season(
        val __v: Int = 0,
        val _id: String = "",
        val createdAt: String = "",
        val description: String = "",
        val episodes: List<String> = listOf(),
        val releaseTime: String = "",
        val seasonNumber: Int = 0,
        val thumbnail: String = "",
        val title: String = "",
        val updatedAt: String = ""
    )

    data class SeasonsEpisode(
        val __v: Int = 0,
        val _id: String = "",
        val createdAt: String = "",
        val description: String = "",
        val duration: Int = 0,
        val episodeNumber: Int = 0,
        val link: String = "",
        val live: Boolean = false,
        val releaseTime: String = "",
        val thumbnail: String = "",
        val title: String = "",
        val updatedAt: String = ""
    )
}

data class SportCategories(
    val __v: Int,
    val _id: String,
    val category: String,
    val createdAt: String,
    val updatedAt: String
)

data class WeekDays(
    val date: String,
    val day: String
)
