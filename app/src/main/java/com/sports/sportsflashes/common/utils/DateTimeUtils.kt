package com.sports.sportsflashes.common.utils

import android.util.Log
import com.sports.sportsflashes.common.utils.AppConstant.DateTime.DATE_TIME_FORMAT_LOCAL
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/**
 *Created by Bhanu on 20-07-2020
 */
object DateTimeUtils {
    val TAG = DateTimeUtils::class.java.name

    const val MINUTE: Long = 60
    const val HOUR = MINUTE * 60
    const val DAY = HOUR * 24
    const val MONTH = DAY * 30
    const val YEAR = MONTH * 12

    fun calculateDateDifference(
        dateFormat: String?,
        fromDate: String?,
        toDate: String?
    ): Long {
        return try {
            val simpleDateFormat =
                SimpleDateFormat(dateFormat, Locale.getDefault())
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
            val fromTimeD: Date = simpleDateFormat.parse(fromDate)
            val toTimeD: Date = simpleDateFormat.parse(toDate)
            fromTimeD.getTime() - toTimeD.getTime()
        } catch (e: ParseException) {
            e.printStackTrace()
            0
        }
    }


    fun now(format: String?): String? {
        val dateFormat: DateFormat = SimpleDateFormat(format, Locale.getDefault())
        val date = Date()
        val timeStamp: String = dateFormat.format(date)
        Log.w("Application", "Current TimeStamp $timeStamp")
        return timeStamp
    }

    fun getTimeStamp(format: String?, millis: Long): String? {
        val date = Date(millis)
        val dateFormat: DateFormat = SimpleDateFormat(format, Locale.getDefault())
        val timeStamp: String = dateFormat.format(date)
        Log.w("Application", "TimeStamp $timeStamp")
        return timeStamp
    }

    fun convertServerTime(format: String?, time: String?): String? {
        if (time == null || time == "") return ""
        val date = Date(parseTimeInMillis(AppConstant.DateTime.DONATION_SERVER_FORMAT, time))
        val dateFormat: DateFormat = SimpleDateFormat(format, Locale.getDefault())
        val timeStamp: String = dateFormat.format(date)
        Log.w("Application", "TimeStamp $timeStamp")
        return timeStamp
    }

    fun convertServerISOTime(format: String?, time: String?): String? {
        if (time == null || time == "") return ""
        val date = Date(parseTimeInMillis(AppConstant.DateTime.DATE_TIME_FORMAT_ISO, time))
        val dateFormat: DateFormat = SimpleDateFormat(format, Locale.getDefault())
        val timeStamp: String = dateFormat.format(date)
        Log.w("Application", "TimeStamp $timeStamp")
        return timeStamp
    }

    fun convertToServerTime(calendar: Calendar): String? {
        //calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        val date: Date = calendar.getTime()
        val dateFormat: DateFormat = SimpleDateFormat(AppConstant.DateTime.DATE_TIME_FORMAT_ISO)
        val tz: TimeZone = TimeZone.getTimeZone("UTC")
        dateFormat.setTimeZone(tz)
        return dateFormat.format(date)
    }

    fun parseTimeInMillis(format: String?, timeStamp: String?): Long {
        return try {
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            if (timeStamp != null) {
                val date: Date = sdf.parse(timeStamp)
                date.getTime()
            } else 0
        } catch (pe: ParseException) {
            0
        }
    }

    fun convertUTCTimeToLocal(dateStr: String?): String? {
        val df = SimpleDateFormat(DATE_TIME_FORMAT_LOCAL, Locale.ENGLISH)
        df.setTimeZone(TimeZone.getTimeZone("UTC"))
        var date: Date? = null
        try {
            date = df.parse(dateStr)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        df.setTimeZone(TimeZone.getDefault())
        return df.format(date)
    }

    fun getCurrentTimeInMillis(): Long {
        val date = Date()
        return date.getTime()
    }

    fun toMinuteSeconds(millis: Long): String? {
        if (millis == 0L) {
            return "00:00"
        }
        val duration = millis / 1000
        val minute = duration / 60
        val seconds = duration % 60
        return java.lang.String.format(Locale.getDefault(), "%1$02d:%2$02d", minute, seconds)
    }

    fun toHourMinuteSeconds(millis: Long): String? {
        if (millis == 0L) {
            return "00:00:00"
        }
        val duration = millis / 1000
        var hour: Long = 0
        var minute = duration / 60
        if (minute >= 60) {
            hour = minute / 60
            minute = minute % 60
        }
        val seconds = duration % 60
        return if (hour == 0L) {
            java.lang.String.format(Locale.getDefault(), "%1$02d:%2$02d", minute, seconds)
        } else java.lang.String.format(
            Locale.getDefault(),
            "%1$02d:%2$02d:%3$02d",
            hour,
            minute,
            seconds
        )
    }

    fun getTimeSlots(
        format: String?,
        startTime: String?,
        endTime: String?,
        slot: Long
    ): List<Long?>? {
        return try {
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            val dateObj1: Date = sdf.parse(startTime)
            val dateObj2: Date = sdf.parse(endTime)
            getTimeSlots(dateObj1.getTime(), dateObj2.getTime(), slot)
        } catch (pe: ParseException) {
            pe.printStackTrace()
            null
        }
    }

    fun getTimeSlots(
        startTime: Long,
        endTime: Long,
        slot: Long
    ): List<Long?>? {
        val mTimeSlots: MutableList<Long?> = ArrayList()
        return try {
            val dateObj1 = Date(startTime)
            val dateObj2 = Date(endTime)
            var dif: Long = dateObj1.getTime()
            while (dif < dateObj2.getTime()) {
                mTimeSlots.add(Date(dif).getTime())
                dif += slot
            }
            mTimeSlots
        } catch (pe: Exception) {
            pe.printStackTrace()
            null
        }
    }

    /**
     * Get day from date
     *
     * @return day from date
     */
    fun getDayFromDate(timeInMillis: Long): String? {
        return try {
            val newDate = Date(timeInMillis)
            SimpleDateFormat("EEEE", Locale.getDefault()).format(newDate)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * get Month from date
     *
     * @return Month of this date
     */
    fun getMonthFromDate(timeInMillis: Long): String? {
        return try {
            val newDate = Date(timeInMillis)
            SimpleDateFormat("MMM", Locale.getDefault()).format(newDate)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getIntMonthFromDate(): Int {
        return getIntMonthFromDate(System.currentTimeMillis())
    }

    fun getIntMonthFromDate(timeInMillis: Long): Int {
        return try {
            val c: Calendar = Calendar.getInstance()
            c.setTime(Date(timeInMillis))
            c.get(Calendar.MONTH)
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    fun getYearFromDate(): Int {
        return getYearFromDate(System.currentTimeMillis())
    }

    fun getYearFromDate(timeInMillis: Long): Int {
        return try {
            val c: Calendar = Calendar.getInstance()
            c.setTime(Date(timeInMillis))
            c.get(Calendar.YEAR)
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }


    /**
     * Get relative date time format as '2 hourse ago , 2 days ago , 2 minutes ago'
     *
     * @param timeInMillis time in millis seconds
     * @return relative date time
     */
    fun getRelativeTimeDifference(timeInMillis: Long): String? {
        var difference: Long = 0
        val mCurrentDate = System.currentTimeMillis()
        difference = mCurrentDate - timeInMillis
        var seconds = difference / 1000
        var minutes = seconds / 60
        var hours = minutes / 60
        var days = hours / 24
        var weeks = days / 7
        var months = days / 31
        var years = days / 365
        return if (difference > 0) {
            if (seconds == 0L) {
                return "Just now"
            }
            if (seconds < MINUTE) {
                if (seconds == 1L) "Just now" else "$seconds seconds ago"
            } else if (seconds < MINUTE * 2) {
                "a minute ago"
            } else if (seconds < HOUR) {
                "$minutes minutes ago"
            } else if (seconds < HOUR * 2) {
                "an hour ago"
            } else if (seconds < DAY) {
                "$hours hours ago"
            } else if (seconds < DAY * 2) {
                "yesterday"
            } else if (seconds < MONTH) {
                "$days days ago"
            } else if (seconds < YEAR) // 12 * 30 * 24 * 60 * 60
            {
                if (months <= 1) "one month ago" else "$months months ago"
            } else {
                if (years <= 1) "one year ago" else "$years years ago"
            }
        } else {
            seconds = -seconds
            minutes = -minutes
            hours = -hours
            days = -days
            weeks = -weeks
            months = -months
            years = -years
            if (seconds < MINUTE) {
                if (seconds == 1L) "Just now" else "$seconds seconds ago"
            } else if (seconds < MINUTE * 2) {
                "in a minute"
            } else if (seconds < HOUR) {
                "in $minutes minutes"
            } else if (seconds < HOUR * 2) {
                "in an hour"
            } else if (seconds < DAY) {
                "in $hours hours"
            } else if (seconds < DAY * 2) {
                "tomorrow"
            } else if (seconds < DAY * 30) {
                "in $days days"
            } else if (seconds < YEAR) // 12 * 30 * 24 * 60 * 60
            {
                if (months <= 1) "in one month" else "in $months months"
            } else {
                if (years <= 1) "in one year" else "in $years years"
            }
        }
    }


    /*   fun getMonthRange(): Pair<Long?, Long?>? {
           var begining: Date
           var end: Date
           run {
               val calendar: Calendar = getCalendarForNow()
               calendar.set(
                   Calendar.DAY_OF_MONTH,
                   calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
               )
               setTimeToBeginningOfDay(calendar)
               begining = calendar.getTime()
           }
           run {
               val calendar: Calendar = getCalendarForNow()
               calendar.set(
                   Calendar.DAY_OF_MONTH,
                   calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
               )
               setTimeToEndofDay(calendar)
               end = calendar.getTime()
           }
           return Pair.create(begining.getTime(), end.getTime())
       }
   */
    private fun getCalendarForNow(): Calendar {
        val calendar: Calendar = GregorianCalendar.getInstance()
        calendar.setTime(Date())
        return calendar
    }

    private fun setTimeToBeginningOfDay(calendar: Calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }

    private fun setTimeToEndofDay(calendar: Calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
    }

    fun inLocalTimeZone(dateStr: String): Long {
        return try {
            Log.d(TAG, "In UTC : $dateStr")
            val df =
                SimpleDateFormat(AppConstant.DateTime.DATE_TIME_FORMAT_ISO, Locale.ENGLISH)
            df.setTimeZone(TimeZone.getTimeZone("UTC"))
            val date: Date = df.parse(dateStr)
            df.setTimeZone(TimeZone.getDefault())
            val formattedDate: String = df.format(date)
            Log.d(TAG, "In UTC : $formattedDate")
            parseTimeInMillis(AppConstant.DateTime.DATE_TIME_FORMAT_ISO, formattedDate)
        } catch (pe: ParseException) {
            pe.printStackTrace()
            0
        }
    }


    @Throws(ParseException::class)
    fun getSeparateDate(
        date: String?,
        format: String?
    ): HashMap<String, Int>? {
        val fromDateFormat: DateFormat = SimpleDateFormat(format, Locale.getDefault())
        val dateInDateObj: Date = fromDateFormat.parse(date)
        /* Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateInDateObj);*/
        val day: Int = dateInDateObj.getDate()
        val month: Int = dateInDateObj.getMonth()
        val year: Int = dateInDateObj.getYear() + 1900
        val hour: Int = dateInDateObj.getHours()
        val minutes: Int = dateInDateObj.getMinutes()
        val second: Int = dateInDateObj.getSeconds()
        val hashMap = HashMap<String, Int>()
        hashMap["day"] = day
        hashMap["month"] = month
        hashMap["year"] = year
        hashMap["hour"] = hour
        hashMap["minutes"] = minutes
        hashMap["second"] = second
        return hashMap
    }

    fun onTimeSet(hour: Int, minute: Int): DateTime? {
        val dateTime = DateTime()
        val mCalen: Calendar = Calendar.getInstance()
        mCalen.set(Calendar.HOUR_OF_DAY, hour)
        mCalen.set(Calendar.MINUTE, minute)
        var hour12format_local: Int = mCalen.get(Calendar.HOUR)
        val hourOfDay_local: Int = mCalen.get(Calendar.HOUR_OF_DAY)
        val minute_local: Int = mCalen.get(Calendar.MINUTE)
        val ampm: Int = mCalen.get(Calendar.AM_PM)
        val minute1: String
        minute1 = if (minute_local < 10) {
            "0$minute_local"
        } else "" + minute_local
        val ampmStr = if (ampm == 0) "AM" else "PM"
        // Set the Time String in Button
        if (hour12format_local == 0) hour12format_local = 12
        dateTime.hour = hour12format_local
        dateTime.min = minute
        dateTime.ampm = ampmStr

//        String selecteTime=hour12format_local+":"+ minute1+" "+ampmStr;
        return dateTime
    }

    fun monthNameFromNumber(month: Int): String? {
        val str = arrayOf(
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"
        )
        return str[month - 1]
    }

    class DateTime {
        var day = 0
        var month = 0
        var year = 0
        var hour = 0
        var min = 0
        var ampm: String? = null

    }

    fun getAdditionalTimeWithDuration(time: String, format: String, duration: Int): String {
        val df = SimpleDateFormat(format)
        val d: Date = df.parse(time)
        val cal: Calendar = Calendar.getInstance()
        cal.time = d
        cal.add(Calendar.MINUTE, duration)
        return df.format(cal.time)
    }
}