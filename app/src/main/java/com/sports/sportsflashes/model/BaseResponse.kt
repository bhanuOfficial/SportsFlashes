package com.sports.sportsflashes.model

data class BaseResponse<T>(val msg: String, val code: Int, val data: T)