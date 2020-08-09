package com.sports.sportsflashes.model

/**
 *Created by Bhanu on 09-08-2020
 */
data class ReminderRespose(
    val __v: Int = 0,
    val _id: String = "",
    val createdAt: String = "",
    val firebaseId: String = "",
    val reminders: List<String> = listOf(),
    val updatedAt: String = ""
)