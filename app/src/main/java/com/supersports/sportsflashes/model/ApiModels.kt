package com.supersports.sportsflashes.model

data class FeaturedShows(
    val __v: Int = 0,
    val _id: String = "",
    val category: Any = Any(),
    val comments: List<Any> = listOf(),
    val createdAt: String = "",
    val creator: String = "",
    val description: String = "",
    val duration: Int = 0,
    val featured: Boolean = false,
    val icon: String = "",
    val likes: Int = 0,
    val original: Boolean = false,
    val playing: Playing = Playing(),
    val releaseTime: String = "",
    val seasons: List<Any> = listOf(),
    val seasonsEpisodes: List<SeasonsEpisode> = listOf(),
    var thumbnail: String = "",
    val title: String = "",
    val type: String = "",
    val updatedAt: String = "",
    val thumbnailData: Any = Any(),
    val radio: Boolean = false,
    var subscribed: Boolean = false,
    val link: String = ""
)

data class ThumbnailData(
    val _id: String = "",
    val endTime: String = "",
    val startTime: String = "",
    val thumbnail: String = "",
    val title: String = ""
)


data class Category(
    val __v: Int = 0,
    val _id: String = "",
    val category: String = "",
    val createdAt: String = "",
    val icon: String = "",
    val updatedAt: String = ""
)

data class Playing(
    val __v: Int = 0,
    val _id: String = "",
    val category: String = "",
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
    val category: String = "",
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
    val category: String = "",
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

data class SportCategories(
    val categories: List<SportsCategoriesList> = listOf()
) {
    data class SportsCategoriesList(
        val __v: Int,
        val _id: String,
        val category: String,
        val createdAt: String,
        val updatedAt: String
    )

}

data class WeekDays(
    val date: String,
    val day: String
)

data class Seasons(
    val __v: Int = 0,
    val _id: String = "",
    val createdAt: String = "",
    val description: String = "",
    val episodes: List<String> = listOf(),
    val identifier: String = "",
    val seasonNumber: Int = 0,
    val title: String = "",
    val updatedAt: String = ""
)
