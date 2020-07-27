package com.sports.sportsflashes.model

/**
 *Created by Bhanu on 09-07-2020
 */
class MessageEvent() {
    var type = String()
     var data = Any()

    constructor(type: String) : this() {
        this.type = type
    }

    constructor(type: String, data: Any) : this() {
        this.type = type
        this.data = data
    }

    companion object {
        const val HOME_FRAGMENT = "HomeFragment"
        const val PLAY_PODCAST_SOURCE = "playing_podcast"
        const val PLAY_PODCAST_SOURCE_MORE = "playing_podcast_more"
        const val LIVE_SHOW = "live_show"
    }

}