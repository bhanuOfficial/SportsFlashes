package com.sports.sportsflashes.common.helper

import com.sports.sportsflashes.model.MonthEventModel

/**
 *Created by Bhanu on 07-07-2020
 */
interface EventItemSelection {
    fun onEventSelected(position: Int, eventModel: MonthEventModel,listOfSelectedEvent: ArrayList<Int>)
    fun setSelectionVisible(select: Boolean)
}