package com.supersports.sportsflashes.model

/**
 *Created by Bhanu on 20-07-2020
 */
data class LiveSeasonModel(
    val __v: Int = 0,
    val _id: String = "",
    val category: Category = Category(),
    val createdAt: String = "",
    val description: String = "",
    val episodes: List<Episode> = listOf(),
    val live: List<Live> = listOf(),
    val releaseTime: String = "",
    val scheduled: List<GenericUpcomingShows> = listOf(),
    val seasonNumber: Int = 0,
    val thumbnail: String = "",
    val title: String = "",
    val link: String = "",
    val icon: String = "",
    val upcoming: List<GenericUpcomingShows> = listOf(),
    val updatedAt: String = ""
) {
    data class Category(
        val __v: Int = 0,
        val _id: String = "",
        val category: String = "",
        val createdAt: String = "",
        val icon: String = "",
        val updatedAt: String = ""
    )

    data class Episode(
        val __v: Int = 0,
        val _id: String = "",
        val category: String = "",
        val createdAt: String = "",
        val description: String = "",
        val duration: Int = 0,
        val episodeNumber: Int = 0,
        val link: String = "",
        val releaseTime: String = "",
        val thumbnail: String = "",
        val title: String = "",
        val updatedAt: String = ""
    )

    data class Live(
        val __v: Int = 0,
        val _id: String = "",
        /*val category: String = "",*/
        val createdAt: String = "",
        val description: String = "",
        val duration: Int = 0,
        val episodeNumber: Int = 0,
        var link: String = "",
        val releaseTime: String = "",
        val thumbnail: String = "",
        val title: String = "",
        val live: Boolean = false,
        val radio: Boolean = false,
        val updatedAt: String = ""
    )

    data class GenericUpcomingShows(
        val __v: Int = 0,
        val _id: String = "",
        val category: String = "",
        val createdAt: String = "",
        var description: String = "",
        val duration: Int = 0,
        val episodeNumber: Int = 0,
        val link: String = "",
        val releaseTime: String = "",
        val thumbnail: String = "",
        val title: String = "",
        val updatedAt: String = "",
        val startTime: String = ""
    )


}