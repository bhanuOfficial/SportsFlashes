package com.sports.sportsflashes.model

/**
 *Created by Bhanu on 06-07-2020
 */

data class ScheduleModel(
    val `6`: List<WeekScheduleData> = listOf(),
    val `5`: List<WeekScheduleData> = listOf(),
    val `4`: List<WeekScheduleData> = listOf(),
    val `3`: List<WeekScheduleData> = listOf(),
    val `2`: List<WeekScheduleData> = listOf(),
    val `1`: List<WeekScheduleData> = listOf(),
    val `0`: List<WeekScheduleData> = listOf()
) {
    data class WeekScheduleData(
        val __v: Int = 0,
        val _id: String = "",
        val createdAt: String = "",
        val description: String = "",
        val duration: Int = 0,
        val episodeNumber: Int = 0,
        val link: String = "",
        val releaseTime: String = "",
        val thumbnail: String = "",
        val title: String = "",
        val updatedAt: String = "",
        val live: Boolean = false
    )
}