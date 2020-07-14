package com.sports.sportsflashes.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.gson.Gson
import com.sports.sportsflashes.R
import com.sports.sportsflashes.common.application.SFApplication
import com.sports.sportsflashes.common.utils.AppConstant
import com.sports.sportsflashes.model.FeaturedShows
import com.sports.sportsflashes.model.MessageEvent
import com.sports.sportsflashes.model.MonthEventModel
import com.sports.sportsflashes.view.activites.YoutubePlayerActivity
import kotlinx.android.synthetic.main.dashboard_full_image_show.*
import kotlinx.android.synthetic.main.playable_item_layout.playCurrentShow
import kotlinx.android.synthetic.main.playable_item_layout.readMore
import kotlinx.android.synthetic.main.playable_item_layout.showDescription
import kotlinx.android.synthetic.main.playable_item_layout.showDescriptionDetail
import kotlinx.android.synthetic.main.playable_item_layout.showTittle
import kotlinx.android.synthetic.main.playable_item_layout.show_detail_layout
import kotlinx.android.synthetic.main.show_view_layout.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

/**
 *Created by Bhanu on 04-07-2020
 */
class ShowViewFragment : Fragment() {

    private lateinit var featuredShows: FeaturedShows
    private lateinit var eventModel: MonthEventModel

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var mediaPlayer: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SFApplication.getAppComponent().inject(this)
        arguments?.let {
            if (it.getString(AppConstant.BundleExtras.FEATURED_SHOW) != null)
                featuredShows =
                    gson.fromJson(
                        it.getString(AppConstant.BundleExtras.FEATURED_SHOW),
                        FeaturedShows::class.java
                    )
            if (it.getString(AppConstant.BundleExtras.EVENT_ITEM) != null) {
                eventModel = gson.fromJson(
                    it.getString(AppConstant.BundleExtras.EVENT_ITEM),
                    MonthEventModel::class.java
                )
                featuredShows = gson.fromJson(gson.toJson(eventModel), FeaturedShows::class.java)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.show_view_layout, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewForShow()
    }

    private fun initViewForShow() {
        if (this::eventModel.isInitialized) {
            playCurrentShow.visibility = View.GONE
            reminderView.visibility = View.VISIBLE
            share.visibility = View.GONE
            showTitleContainer.setPadding(0, 0, 0, 0)
        } else {
            playCurrentShow.visibility = View.VISIBLE
            share.visibility = View.VISIBLE
            reminderView.visibility = View.GONE
        }

        if (featuredShows.seasonsEpisodes.size > 1 && !this::eventModel.isInitialized) {
            moreEpisodesContainer.visibility = View.VISIBLE
        } else {
            moreEpisodesContainer.visibility = View.INVISIBLE
        }

        if (this::eventModel.isInitialized) {
            activity?.let {
                Glide.with(this).load(eventModel.thumbnail)
                    .placeholder(
                        it.resources.getDrawable(
                            R.drawable.default_thumbnail,
                            null
                        )
                    )
                    .into(showImage)
            }

        }
        if (this::featuredShows.isInitialized)
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

        showDescriptionDetail.tag = true

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
        playCurrentShow.setOnClickListener {
            if (featuredShows.type == "Video") {
                if (mediaPlayer.playWhenReady)
                    mediaPlayer.playWhenReady = false
                activity?.let {
                    it.startActivity(
                        Intent(context, YoutubePlayerActivity::class.java)
                            .putExtra(
                                AppConstant.BundleExtras.YOUTUBE_VIDEO_CODE,
                                AppConstant.YOUTUBE_VIDEO_CODE
                            )
                    )
                }
            } else
                EventBus.getDefault().post(
                    MessageEvent(
                        MessageEvent.PLAY_PODCAST_SOURCE,
                        featuredShows.playing.link
                    )
                )
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    private fun setShowDetails(show: Any) {

    }
}