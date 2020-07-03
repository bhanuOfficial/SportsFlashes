package com.sports.sportsflashes.view.fragments

import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sports.sportsflashes.R
import com.sports.sportsflashes.common.application.SFApplication
import com.sports.sportsflashes.model.FeaturedShows
import com.sports.sportsflashes.view.adapters.CircularShowAdapter
import kotlinx.android.synthetic.main.schedule_fragment.*
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ScheduleFragment : Fragment() {
    private var featuredShows = listOf<FeaturedShows>()
    private var weekdayList = arrayListOf<String>()

    @Inject
    lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SFApplication.getAppComponent().inject(this)
        arguments?.let {
            val featuredListType: Type = object : TypeToken<ArrayList<FeaturedShows?>?>() {}.type
            featuredShows =
                gson.fromJson(it.getString("CHECK"), featuredListType)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.schedule_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initScheduleShowRecycler()
        initWeeViewRecycler()
        initSchedulerRecycler()
        getWeekList()

    }

    fun initScheduleShowRecycler() {
        schedule_shows_recycler.setHasFixedSize(true)
        schedule_shows_recycler.layoutManager = LinearLayoutManager(activity).apply {
            this.orientation = LinearLayoutManager.HORIZONTAL
            this.reverseLayout = false
        }
        schedule_shows_recycler.adapter =
            activity?.let {
                CircularShowAdapter(featuredShows, {
                    var smallItemWidth = it
                }, it, true)
            }
    }

    fun initWeeViewRecycler() {
        weekView_recycler.setHasFixedSize(true)
        weekView_recycler.layoutManager = GridLayoutManager(activity, 1).apply {
            this.orientation = GridLayoutManager.HORIZONTAL
            this.reverseLayout = false
        }
    }

    fun initSchedulerRecycler() {
        scheduleRecycler.setHasFixedSize(true)
        scheduleRecycler.layoutManager = LinearLayoutManager(activity).apply {
            this.orientation = LinearLayoutManager.HORIZONTAL
            this.reverseLayout = false
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ScheduleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun newInstance(): ScheduleFragment {
            return ScheduleFragment()
        }
    }

    private fun getWeekList() {
        var cal: Calendar = Calendar.getInstance()
        cal.time = cal.time
        val df: DateFormat = SimpleDateFormat("dd/MM/yyyy")
        for (i in 1..3) {
            cal.add(Calendar.DATE, -1)
            weekdayList.add(df.format(cal.time))
        }
        cal = Calendar.getInstance()
        cal.time = (cal.time)
        weekdayList.add(df.format(cal.time))
        for (i in 1..3) {
            cal.add(Calendar.DATE, 1)
            weekdayList.add(df.format(cal.time))
        }

        val byDate: Comparator<String?> =
            object : Comparator<String?> {
                var sdf =
                    SimpleDateFormat("dd/MM/yyyy")

                override fun compare(p0: String?, p1: String?): Int {
                    var d1: Date? = null
                    var d2: Date? = null
                    try {
                        d1 = sdf.parse(p0)
                        d2 = sdf.parse(p1)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
//                    return if (d1!!.time > d2!!.time) -1 else 1 //descending
                    return if (d1!!.time > d2!!.time) 1 else -1    //ascending
                }
            }

        Collections.sort(weekdayList, byDate)
        for (i in weekdayList) {
            Log.d("BHANU", " DATE --> $i")
            val date1 = SimpleDateFormat("dd/MM/yyyy").parse(i)
            when {
                isYesterday(date1!!) -> {
                    weekTabs.addTab(weekTabs.newTab().setText("Yesterday $i"))
                }
                isTomorrow(date1) -> {
                    weekTabs.addTab(weekTabs.newTab().setText("Tomorrow $i"))
                }
                isToday(date1) -> {
                    weekTabs.addTab(weekTabs.newTab().setText("Today $i"))
                }
                else -> {
                    weekTabs.addTab(weekTabs.newTab().setText(i))
                }
            }

        }
//        weekTabs.scrollX = weekTabs.width
        weekTabs.postDelayed({
            weekTabs.setScrollPosition(3, 0f, true)
        }, 100)
//        weekTabs.selectTab(weekTabs.getTabAt(),true)
//        weekView_recycler.adapter = ScheduleTimeAdapter(weekdayList)
    }

    fun isYesterday(d: Date): Boolean {
        return DateUtils.isToday(d.time + DateUtils.DAY_IN_MILLIS)
    }

    fun isTomorrow(d: Date): Boolean {
        return DateUtils.isToday(d.time - DateUtils.DAY_IN_MILLIS)
    }

    fun isToday(d: Date): Boolean {
        return DateUtils.isToday(d.time)
    }

}