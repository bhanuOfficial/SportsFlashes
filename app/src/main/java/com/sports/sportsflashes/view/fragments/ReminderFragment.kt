package com.sports.sportsflashes.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sports.sportsflashes.R
import com.sports.sportsflashes.common.application.SFApplication
import com.sports.sportsflashes.common.utils.AlertDialogUtility
import com.sports.sportsflashes.common.utils.AppConstant
import com.sports.sportsflashes.common.utils.DateTimeUtils
import com.sports.sportsflashes.model.FeaturedShows
import com.sports.sportsflashes.view.adapters.ReminderShowAdapter
import kotlinx.android.synthetic.main.dashboard_full_image_show.*
import kotlinx.android.synthetic.main.playable_item_layout.*
import kotlinx.android.synthetic.main.playable_item_layout.playCurrentShow
import kotlinx.android.synthetic.main.reminder_fragment.*
import kotlinx.android.synthetic.main.show_view_layout.*
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
    init {
        SFApplication.getAppComponent().inject(this)
    }

    @Inject
    lateinit var gson: Gson
    private var featuredShows = listOf<FeaturedShows>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            val featuredListType: Type = object : TypeToken<ArrayList<FeaturedShows?>?>() {}.type
            featuredShows =
                gson.fromJson(
                    it.getString(AppConstant.BundleExtras.FEATURED_SHOW_LIST),
                    featuredListType
                )
        }
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
        showDescription.text = featuredShows.description
        show_detail_layout.visibility = View.VISIBLE
        showDescriptionDetail.text = featuredShows.description
        showDescriptionDetail.tag = true
        creator.text = featuredShows.creator
        duration.text = DateTimeUtils.convertServerISOTime(
            AppConstant.DateTime.STD_DATE_FORMAT,
            featuredShows.releaseTime
        )

        if (showDescriptionDetail.lineCount > 3) {
            readMore.visibility = View.VISIBLE
        } else {
            readMore.visibility = View.GONE
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
        reminderView.setOnClickListener {
            AlertDialogUtility.reminderAppDialog(R.layout.reminder_dialog_layout, requireActivity())
        }

    }
}