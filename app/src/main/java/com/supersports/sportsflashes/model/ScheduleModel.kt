package com.supersports.sportsflashes.model

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
        val creator: String = "",
        val description: String = "",
        val featured: Boolean = false,
        val icon: String = "",
        val likes: Int = 0,
        val live: Boolean = false,
        val original: Boolean = false,
        val releaseTime: String = "",
        val seasons: List<Any> = listOf(),
        val thumbnail: String = "",
        val title: String = "",
        val type: String = "",
        val updatedAt: String = "",
        val link: String = "",
        val startTime: String = "",
        val endTime: String = "",
        val radio: Boolean = false
    )
}