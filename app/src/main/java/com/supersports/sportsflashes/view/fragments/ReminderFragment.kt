package com.supersports.sportsflashes.view.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.supersports.sportsflashes.R
import com.supersports.sportsflashes.common.application.SFApplication
import com.supersports.sportsflashes.common.utils.AlertDialogUtility
import com.supersports.sportsflashes.common.utils.AppConstant
import com.supersports.sportsflashes.common.utils.DateTimeUtils
import com.supersports.sportsflashes.model.FeaturedShows
import com.supersports.sportsflashes.model.ReminderReqModel
import com.supersports.sportsflashes.repository.api.STATUS
import com.supersports.sportsflashes.view.activites.MainActivity
import com.supersports.sportsflashes.view.adapters.ReminderShowAdapter
import com.supersports.sportsflashes.viewmodel.ReminderFragmentViewModel
import kotlinx.android.synthetic.main.dashboard_full_image_show.*
import kotlinx.android.synthetic.main.playable_item_layout.playCurrentShow
import kotlinx.android.synthetic.main.reminder_fragment.*
import kotlinx.android.synthetic.main.show_view_layout.*
import kotlinx.android.synthetic.main.show_view_layout.playLayout
import kotlinx.android.synthetic.main.show_view_layout.readMore
import kotlinx.android.synthetic.main.show_view_layout.showDescriptionDetail
import kotlinx.android.synthetic.main.show_view_layout.showTittle
import kotlinx.android.synthetic.main.show_view_layout.show_detail_layout
import java.lang.reflect.Type
import javax.inject.Inject

/**
 *Created by Bhanu on 27-07-2020
 */
class ReminderFragment : Fragment(), ReminderShowAdapter.OnReminderItemClickListner {
    private lateinit var reminderFragmentViewModel: ReminderFragmentViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var activity: MainActivity

    init {
        SFApplication.getAppComponent().inject(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }

    @Inject
    lateinit var gson: Gson
    private var featuredShows = listOf<FeaturedShows>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        reminderFragmentViewModel =
            ViewModelProvider(this).get(ReminderFragmentViewModel::class.java)
        sharedPreferences = requireActivity().getSharedPreferences(
            getString(R.string.pref_key),
            Context.MODE_PRIVATE
        )
        arguments?.let {
            val featuredListType: Type = object : TypeToken<ArrayList<FeaturedShows?>?>() {}.type
            featuredShows =
                gson.fromJson(
                    it.getString(AppConstant.BundleExtras.FEATURED_SHOW_LIST),
                    featuredListType
                )
        }
        activity.appLogo.setBackgroundResource(android.R.color.transparent)
        activity.toolbar.setBackgroundColor(resources.getColor(R.color.black, null))
        activity.appLogo.text = "Reminder"
        activity.appLogo.setTextColor(resources.getColor(R.color.red, null))
        val rootView = inflater.inflate(R.layout.reminder_fragment, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initScheduleShowRecycler()
        onItemClicked(featuredShows[0])
    }

    private fun initScheduleShowRecycler() {
        reminder_shows_recycler.setHasFixedSize(true)
        reminder_shows_recycler.layoutManager = LinearLayoutManager(activity).apply {
            this.orientation = LinearLayoutManager.HORIZONTAL
            this.reverseLayout = false
        }
        reminder_shows_recycler.adapter =
            activity?.let {
                ReminderShowAdapter(featuredShows, this)
            }
    }

    override fun onItemClicked(featuredShows: FeaturedShows) {
        playCurrentShow.visibility = View.GONE
        reminderView.visibility = View.VISIBLE
        share.visibility = View.GONE
        playLayout.visibility = View.GONE
        showTitleContainer.setPadding(0, 0, 0, 0)
        moreEpisodesContainer.visibility = View.INVISIBLE
        activity?.let {
            Glide.with(this).load(featuredShows.thumbnail)
                .placeholder(
                    it.resources.getDrawable(
                        R.drawable.default_thumbnail,
                        null
                    )
                )
                .into(showImage)
        }

        showTittle.text = featuredShows.title
        showTittle.textSize = 25f
        showDate.textSize = 15f
        showDate.text = DateTimeUtils.convertServerISOTime(
            AppConstant.DateTime.REMINDER_FORMAT,
            featuredShows.releaseTime
        )
        show_detail_layout.visibility = View.VISIBLE
        showDescriptionDetail.text = featuredShows.description
        showDescriptionDetail.tag = true
        showType.text = featuredShows.type

        showDescriptionDetail.viewTreeObserver
            .addOnPreDrawListener {
                if (showDescriptionDetail != null) {
                    val count = showDescriptionDetail.layout.lineCount
                    if (count > 3) {
                        readMore.visibility = View.VISIBLE
                    } else {
                        readMore.visibility = View.GONE
                    }
                }


                true
            }
        readMore.setOnClickListener {
            if (showDescriptionDetail.tag as Boolean) {
                readMore.setText(R.string.hide_more)
                showDescriptionDetail.maxLines = Int.MAX_VALUE
                showDescriptionDetail.tag = false
            } else {
                readMore.setText(R.string.show_more)
                showDescriptionDetail.maxLines = 2
                showDescriptionDetail.tag = true
            }
        }
        if (featuredShows.subscribed) {
            reminderView.text = "Remove Reminder"
        } else {
            reminderView.text = "Set Reminder"
        }

        reminderView.setOnClickListener {
            if (featuredShows.subscribed) {
                AlertDialogUtility.reminderAppDialog(
                    R.layout.reminder_dialog_layout, requireActivity(),
                    "You have already set a reminder for this event", "Do you wish to cancel? ",
                    true,
                    Runnable {
                        val showIds = ArrayList<String>()
                        showIds.add(featuredShows._id)

                        val request = ReminderReqModel(
                            sharedPreferences.getString(
                                AppConstant.FIREBASE_INSTANCE,
                                ""
                            )!!, showIds
                        )
                        activity?.let { it1 ->
                            reminderFragmentViewModel.removeReminder(request)
                                .observe(it1, Observer {
                                    if (it.status == STATUS.SUCCESS) {
                                        reminderView.text = "Set Reminder"
                                    } else if (it.status == STATUS.ERROR) {

                                    }
                                })
                        }
                    })
            } else {
                val showIds = ArrayList<String>()
                showIds.add(featuredShows._id)

                val request = ReminderReqModel(
                    sharedPreferences.getString(
                        AppConstant.FIREBASE_INSTANCE,
                        ""
                    )!!, showIds
                )

                activity?.let { it1 ->
                    reminderFragmentViewModel.setReminder(request).observe(it1, Observer {
                        if (it.status == STATUS.SUCCESS) {
                            reminderView.text = "Remove Reminder"
                        } else if (it.status == STATUS.ERROR) {

                        }
                    })
                    AlertDialogUtility.reminderAppDialog(
                        R.layout.reminder_dialog_layout, requireActivity(),
                        featuredShows.title, "Your reminder has been set"
                        , false, null
                    )
                }
            }
            featuredShows.subscribed = !featuredShows.subscribed
        }

    }
}