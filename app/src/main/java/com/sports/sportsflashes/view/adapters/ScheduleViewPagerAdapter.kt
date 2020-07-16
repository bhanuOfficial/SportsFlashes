package com.sports.sportsflashes.view.adapters

import android.os.Bundle
import android.text.format.DateUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.sports.sportsflashes.common.application.SFApplication
import com.sports.sportsflashes.common.utils.AppConstant
import com.sports.sportsflashes.model.ScheduleModel
import com.sports.sportsflashes.view.fragments.ScheduleRecyclerFragment
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
    private val dateFormat = SimpleDateFormat("dd MMM")
    private val dateFormatWithDay = SimpleDateFormat("EEE, dd MMM")
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
                "Yesterday, ${dateFormat.format(df.parse(weekdayList[position])!!)}"
            }
            isTomorrow(date1) -> {
                "Tomorrow, ${dateFormat.format(df.parse(weekdayList[position])!!)}"
            }
            isToday(date1) -> {
                "Today, ${dateFormat.format(df.parse(weekdayList[position])!!)}"
            }
            else -> {
                dateFormatWithDay.format(df.parse(weekdayList[position])!!)
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

}