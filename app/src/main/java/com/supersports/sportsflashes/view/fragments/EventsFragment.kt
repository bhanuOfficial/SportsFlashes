package com.supersports.sportsflashes.view.fragments

import android.content.Context
import android.content.SharedPreferences
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
import com.supersports.sportsflashes.R
import com.supersports.sportsflashes.common.helper.EventItemSelection
import com.supersports.sportsflashes.common.utils.AlertDialogUtility
import com.supersports.sportsflashes.common.utils.AppConstant
import com.supersports.sportsflashes.model.MonthEventModel
import com.supersports.sportsflashes.model.ReminderReqModel
import com.supersports.sportsflashes.repository.api.STATUS
import com.supersports.sportsflashes.view.activites.MainActivity
import com.supersports.sportsflashes.view.adapters.EventsAdapter
import com.supersports.sportsflashes.view.adapters.EventsAdapter.Companion.selected
import com.supersports.sportsflashes.viewmodel.EventFragmentViewModel
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
    private lateinit var activity: MainActivity
    private var selectedShows = listOf<String>()
    private lateinit var sharedPreferences: SharedPreferences
    private var currentMonthIndex = -1
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        eventsFragmentViewModel = ViewModelProvider(this).get(EventFragmentViewModel::class.java)
        sharedPreferences = requireActivity().getSharedPreferences(
            getString(R.string.pref_key),
            Context.MODE_PRIVATE
        )
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
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        activity.appLogo.setBackgroundResource(android.R.color.transparent)
        activity.toolbar.setBackgroundColor(resources.getColor(R.color.black, null))
        activity.appLogo.text = "Event"
        activity.appLogo.setTextColor(resources.getColor(R.color.white, null))
        return layoutInflater.inflate(R.layout.events_fragment, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initEventRecycler()
        refreshViewInit()
        getMonths()
    }

    private fun initView() {
        submitReminder.setOnClickListener {
            val request = ReminderReqModel(
                sharedPreferences.getString(
                    AppConstant.FIREBASE_INSTANCE,
                    ""
                )!!, selectedShows
            )

            activity.let { it1 ->
                eventsFragmentViewModel.setReminder(request)
                    .observe(it1, androidx.lifecycle.Observer {
                        if (it.status == STATUS.SUCCESS) {
                            setSelectionVisible(false)
                            getEventsByMonth(currentMonthIndex + 1)
                        } else if (it.status == STATUS.ERROR) {

                        }
                    })
                AlertDialogUtility.reminderAppDialog(
                    R.layout.reminder_dialog_layout, requireActivity(),
                    "", "Your reminder has been set"
                    , false, null
                )

            }
        }
    }

    private fun initEventRecycler() {
        eventRecycler.setHasFixedSize(true)
        eventRecycler.layoutManager = LinearLayoutManager(activity).apply {
            this.orientation = LinearLayoutManager.VERTICAL
            this.reverseLayout = false
        }
    }

    private fun refreshViewInit() {
        swipeRefresh.isRefreshing = true
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = true
            getEventsByMonth(monthTabs.selectedTabPosition + 1)
        }
    }

    private fun getMonths() {
        val dateFormat: DateFormat = SimpleDateFormat("MMMM")
        val date = Date()

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
                    if (swipeRefresh != null)
                        swipeRefresh.isRefreshing = false
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

    override fun onEventSelected(
        position: Int,
        eventModel: MonthEventModel,
        listOfSelectedEvent: ArrayList<String>
    ) {
        selectedShows = listOfSelectedEvent
        if (listOfSelectedEvent.size == 0) {
            selectView.text = resources.getText(R.string.select)
            selectView.setTextColor(resources.getColor(R.color.white, null))
        } else {
            selectView.text = resources.getText(R.string.cancel)
            selectView.setTextColor(resources.getColor(R.color.red, null))
        }

        if (selectView != null)
            selectView.setOnClickListener {
                if (selectView.text.toString().equals("cancel",true)) {
                    setSelectionVisible(false)
                }
            }
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
            EventsAdapter.listOfSelectedIndex = ArrayList<String>()
            if (eventRecycler.adapter != null) {
                eventRecycler.adapter!!.notifyDataSetChanged()
            }
        }
    }

}