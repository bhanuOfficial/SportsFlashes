package com.supersports.sportsflashes.model

/**
 *Created by Bhanu on 25-08-2020
 */
data class LiveRadioModel(
    val _id: String = "",
    val description: String = "",
    val icon: String = "",
    val link: String = "",
    val thumbnailData: List<ThumbnailData> = listOf(),
    val title: String = "",
    val radio: Boolean = true
) {
    data class ThumbnailData(
        val _id: String = "",
        val endTime: String = "",
        val startTime: String = "",
        val thumbnail: String = "",
        val title: String = ""
    )
}