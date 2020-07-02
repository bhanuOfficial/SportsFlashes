package com.sports.sportsflashes.common.helper

import com.sports.sportsflashes.model.FeaturedShows

/**
 *Created by Bhanu on 02-07-2020
 */
interface CurrentShowClickListener {
    fun onCurrentShowClicked(featuredShows: FeaturedShows)
}