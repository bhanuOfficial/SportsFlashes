package com.sports.sportsflashes.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.sports.sportsflashes.R
import com.sports.sportsflashes.common.helper.EventItemSelection
import com.sports.sportsflashes.model.MonthEventModel
import com.sports.sportsflashes.repository.api.STATUS
import com.sports.sportsflashes.view.adapters.EventsAdapter
import com.sports.sportsflashes.view.adapters.EventsAdapter.Companion.selected
import com.sports.sportsflashes.viewmodel.EventFragmentViewModel
import kotlinx.android.synthetic.main.events_fragment.*
import kotlinx.android.synthetic.main.schedule_fragment.swipeRefresh
import java.text.DateFormat
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 *Created by Bhanu on 07-07-2020
 */
class EventsFragment : Fragment(), EventItemSelection {
    private lateinit var eventsFragmentViewModel: EventFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        eventsFragmentViewModel = ViewModelProvider(this).get(EventFragmentViewModel::class.java)
        val callback = object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (selectView.visibility == View.VISIBLE) {
                    setSelectionVisible(false)
                } else {
                    findNavController().popBackStack()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback);
        return layoutInflater.inflate(R.layout.events_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEventRecycler()
        refreshViewInit()
        getMonths()
    }

    private fun initEventRecycler() {
        eventRecycler.setHasFixedSize(true)
        eventRecycler.layoutManager = LinearLayoutManager(activity).apply {
            this.orientation = LinearLayoutManager.VERTICAL
            this.reverseLayout = false
        }
    }

    private fun refreshViewInit() {
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = true
            swipeRefresh.postDelayed({
                swipeRefresh.isRefreshing = false
            }, 2000)
        }
    }

    private fun getMonths() {
        val dateFormat: DateFormat = SimpleDateFormat("MMMM")
        val date = Date()
        var currentMonthIndex = -1
        val months: Array<String> = DateFormatSymbols().months
        for (i in months.indices) {
            val month = months[i]
            monthTabs.addTab(
                monthTabs.newTab()
                    .setText(month)
                    .also { tab -> tab.tag = month }
            )
            if (dateFormat.format(date) == month) {
                currentMonthIndex = i
            }
        }
        monthTabs.postDelayed({
            monthTabs.setScrollPosition(currentMonthIndex, 0f, true)
        }, 100)
        getEventsByMonth(currentMonthIndex + 1)
        monthTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                getEventsByMonth(tab!!.position + 1)
            }

        })
    }

    private fun getEventsByMonth(month: Int) {
        activity?.let {
            eventsFragmentViewModel.getEventsByMonth(month).observe(
                it,
                androidx.lifecycle.Observer {
                    if (it.status == STATUS.SUCCESS) {
                        val eventList = it.data as List<MonthEventModel>
                        if (eventList.isEmpty()) {
                            eventRecycler.visibility = View.GONE
                            noData.visibility = View.VISIBLE
                        } else {
                            eventRecycler.adapter = EventsAdapter(eventList, this@EventsFragment)
                            eventRecycler.visibility = View.VISIBLE
                            noData.visibility = View.GONE
                        }

                    }
                })
        }
    }

    override fun onEventSelected(position: Int, eventModel: MonthEventModel) {

    }

    override fun setSelectionVisible(select: Boolean) {
        if (select) {
            selectView.visibility = View.VISIBLE
            monthTabs.visibility = View.GONE
            submitContainer.visibility = View.VISIBLE
        } else {
            selectView.visibility = View.GONE
            monthTabs.visibility = View.VISIBLE
            submitContainer.visibility = View.GONE
            selected = false
            EventsAdapter.listOfSelectedIndex = ArrayList<Int>()
            if (eventRecycler.adapter != null) {
                eventRecycler.adapter!!.notifyDataSetChanged()
            }
        }
    }

}