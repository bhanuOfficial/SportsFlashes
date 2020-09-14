package com.supersports.sportsflashes.view.adapters

import android.os.Bundle
import android.text.format.DateUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.bumptech.glide.util.Preconditions
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.supersports.sportsflashes.common.application.SFApplication
import com.supersports.sportsflashes.common.utils.AppConstant
import com.supersports.sportsflashes.model.ScheduleModel
import com.supersports.sportsflashes.view.fragments.ScheduleRecyclerFragment
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 *Created by Bhanu on 15-07-2020
 */
class ScheduleViewPagerAdapter(
    fm: FragmentManager,
    private val scheduleModel: ScheduleModel,
    private val weekTabs: TabLayout,
    private val weekdayList: ArrayList<String>
) : FragmentStatePagerAdapter(fm) {
    @Inject
    lateinit var gson: Gson
    private val dateFormat = SimpleDateFormat("d")
    private val dateFormatWithDay = SimpleDateFormat("EEEE,")
    private val df: DateFormat = SimpleDateFormat("dd/MM/yyyy")

    init {
        SFApplication.getAppComponent().inject(this)
    }

    override fun getItem(position: Int): Fragment {
        val fragment = ScheduleRecyclerFragment()
        fragment.weekTabs(weekTabs)
        fragment.arguments = Bundle().apply {
            // Our object is just an integer :-P
            putString(AppConstant.BundleExtras.SCHEDULE_MODEL, gson.toJson(scheduleModel))
            putInt(AppConstant.BundleExtras.SCHEDULE_POSITION, position)
            putStringArrayList(AppConstant.BundleExtras.WEEKDAY_LIST, weekdayList)
        }
        return fragment
    }

    override fun getCount(): Int {
        return weekdayList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val date1 = SimpleDateFormat("dd/MM/yyyy").parse(weekdayList[position])
        return when {
            isYesterday(date1!!) -> {
                "Yesterday, ${dateFormat.format(df.parse(weekdayList[position])!!)+getDayOfMonthSuffix(dateFormat.format(df.parse(weekdayList[position])!!).toInt())}"
            }
            isTomorrow(date1) -> {
                "Tomorrow, ${dateFormat.format(df.parse(weekdayList[position])!!)+getDayOfMonthSuffix(dateFormat.format(df.parse(weekdayList[position])!!).toInt())}"
            }
            isToday(date1) -> {
                "Today, ${dateFormat.format(df.parse(weekdayList[position])!!)+getDayOfMonthSuffix(dateFormat.format(df.parse(weekdayList[position])!!).toInt())}"
            }
            else -> {
                "${dateFormatWithDay.format(df.parse(weekdayList[position])!!)} " +dateFormat.format(df.parse(weekdayList[position])!!)+
                        "${getDayOfMonthSuffix(dateFormat.format(df.parse(weekdayList[position])!!).toInt())}"


            }
        }
    }

    private fun isYesterday(d: Date): Boolean {
        return DateUtils.isToday(d.time + DateUtils.DAY_IN_MILLIS)
    }

    private fun isTomorrow(d: Date): Boolean {
        return DateUtils.isToday(d.time - DateUtils.DAY_IN_MILLIS)
    }

    private fun isToday(d: Date): Boolean {
        return DateUtils.isToday(d.time)
    }

    private fun getDayOfMonthSuffix(n: Int): String? {
        Preconditions.checkArgument(n in 1..31, "illegal day of month: $n")
        return if (n in 11..13) {
            "th"
        } else when (n % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }

}