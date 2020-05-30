package com.sports.sportsflashes.repository

data class NetworkResponse(val status: STATUS, val data: Any? = null)

enum class STATUS {
    NOT_REQUESTED,
    IN_PROGRESS,
    SUCCESS,
    ERROR
}

