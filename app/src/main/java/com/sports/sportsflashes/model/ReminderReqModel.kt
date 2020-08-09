package com.sports.sportsflashes.model

/**
 *Created by Bhanu on 09-08-2020
 */

data class ReminderReqModel(
    val firebaseId: String = "",
    val ids: List<String> = listOf()
)