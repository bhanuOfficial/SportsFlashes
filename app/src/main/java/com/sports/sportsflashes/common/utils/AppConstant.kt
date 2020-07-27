package com.sports.sportsflashes.common.utils

object AppConstant {
    const val MIN_BUFFER=  5 * 60 * 1000
    const val MAX_BUFFER= 10 * 60 * 1000
    const val YOUTUBE_API_KEY= "AIzaSyBOFy9h9Qoix4NzBTKUYnj4WMCE1j8H_lY"


    object BundleExtras{
        const val FEATURED_SHOW= "featured_show"
        const val EVENT_ITEM= "event_item"
        const val FEATURED_SHOW_LIST= "featured_show_list"
        const val YOUTUBE_VIDEO_CODE= "youtube_video_code"
        const val SCHEDULE_MODEL= "schedule_model"
        const val SCHEDULE_POSITION= "schedule_position"
        const val WEEKDAY_LIST= "weekday_list"
        const val FROM_HOME = "from_home"
        const val LIVE_SHOW_ID = "live_Show_id"
        const val LIVE_SHOW_UPCOMING_LIST = "live_Show_upcoming"
        const val CATEGORY_ID = "category_id"
    }

    object DateTime {
        const val BDAY_FORMAT = "EEE, dd MMM yyyy"
        const val BDAY_SERVER_FORMAT = "yyyy-MM-dd"
        const val DONATION_SERVER_FORMAT = "yyyy-MM-dd HH:mm:ss"
        const val ADD_DONATION_SERVER_FORMAT = "MM/dd/yyyy"
        const val STD_DATE_FORMAT = "dd MMM, yyyy @ hh:mm aa"
        const val TIME_FORMAT = "hh:mm aa"
        const val DATE_FORMAT = "MMM. dd"
        const val MINUTE_SECOND_FORMAT = "mm:ss"
        const val DATE_TIME_FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        const val DATE_TIME_FORMAT_LOCAL = "yyyy-MM-dd'T'HH:mm:ss"
    }
}