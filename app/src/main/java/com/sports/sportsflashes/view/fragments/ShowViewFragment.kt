package com.sports.sportsflashes.view.fragments

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment.newInstance
import com.google.gson.Gson
import com.sports.sportsflashes.R
import com.sports.sportsflashes.common.application.SFApplication
import com.sports.sportsflashes.common.utils.AlertDialogUtility
import com.sports.sportsflashes.common.utils.AppConstant
import com.sports.sportsflashes.common.utils.AppUtility
import com.sports.sportsflashes.common.utils.DateTimeUtils
import com.sports.sportsflashes.model.FeaturedShows
import com.sports.sportsflashes.model.LiveSeasonModel
import com.sports.sportsflashes.model.MessageEvent
import com.sports.sportsflashes.model.MonthEventModel
import com.sports.sportsflashes.view.activites.MainActivity
import com.sports.sportsflashes.view.adapters.MoreEpisodeAdapter
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.dashboard_full_image_show.*
import kotlinx.android.synthetic.main.playable_item_layout.playCurrentShow
import kotlinx.android.synthetic.main.playable_item_layout.readMore
import kotlinx.android.synthetic.main.playable_item_layout.showDescriptionDetail
import kotlinx.android.synthetic.main.playable_item_layout.showTittle
import kotlinx.android.synthetic.main.playable_item_layout.show_detail_layout
import kotlinx.android.synthetic.main.show_view_layout.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject


/**
 *Created by Bhanu on 04-07-2020
 */
class ShowViewFragment : Fragment(), MoreEpisodeAdapter.OnSeasonItemClickedListener {

    private var isFromSeasonClick: Boolean = false
    private var index: Int = 0
    private lateinit var featuredShows: FeaturedShows
    private lateinit var eventModel: MonthEventModel
    private lateinit var YPlayer: YouTubePlayer
    private lateinit var liveSeason: LiveSeasonModel.Live
    private var reminder: Boolean = false
    private lateinit var activity: MainActivity

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var mediaPlayer: ExoPlayer

    init {
        SFApplication.getAppComponent().inject(this)
//        EventBus.getDefault().register(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            reminder =
                it.getBoolean(AppConstant.BundleExtras.REMINDER)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val callback = object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (activity.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    if (playerContainer.visibility == View.VISIBLE && this@ShowViewFragment::YPlayer.isInitialized) {
                        YPlayer.setFullscreen(false)
                    }
                } else {
                    findNavController().popBackStack()
                }
            }
        }
        userVisibleHint = true
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        activity.appLogo.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.in_app_logo,
            0,
            0,
            0
        )
        activity.toolbar.setBackgroundColor(resources.getColor(R.color.black, null))
        activity.appLogo.text = ""
        return inflater.inflate(R.layout.show_view_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewForShow()
        if (arguments?.getString(AppConstant.BundleExtras.FROM_HOME) != null) {
            if (featuredShows.seasonsEpisodes.isNotEmpty() && featuredShows.seasonsEpisodes[0].link.contains(
                    "youtube"
                )
            )
                initYoutubePlayerView(featuredShows.seasonsEpisodes[0].link.split("v=")[1])
            else {
                activity?.let { AppUtility.showToast(it, "Source Error") }
            }
        }
    }

    private fun moreEpisodesInit() {
        moreEpisodesRecycler.setHasFixedSize(true)
        moreEpisodesRecycler.isNestedScrollingEnabled = false
        moreEpisodesRecycler.layoutManager = LinearLayoutManager(activity).also {
            it.reverseLayout = false
            it.orientation = RecyclerView.VERTICAL
        }
        moreEpisodesRecycler.adapter =
            MoreEpisodeAdapter(
                featuredShows.seasonsEpisodes.subList(
                    1,
                    featuredShows.seasonsEpisodes.size
                ), this
            )
    }

    private fun initYoutubePlayerView(videoCode: String) {
        activity?.let {
            Glide.with(this).load(featuredShows.thumbnail)
                .placeholder(
                    it.resources.getDrawable(
                        R.drawable.default_thumbnail,
                        null
                    )
                ).apply(RequestOptions.bitmapTransform(BlurTransformation(20, 2)))
                .into(showImage)
        }
        playerContainer.visibility = View.VISIBLE
        val youTubePlayerFragment = newInstance()
        youTubePlayerFragment.initialize(AppConstant.YOUTUBE_API_KEY, object :
            YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(
                provider: YouTubePlayer.Provider?,
                player: YouTubePlayer,
                wasRestored: Boolean
            ) {
                if (!wasRestored) {
                    YPlayer = player
//                    YPlayer.setFullscreen(true)
                    YPlayer.loadVideo(videoCode)
                    YPlayer.play()
                }
            }

            override fun onInitializationFailure(
                arg0: YouTubePlayer.Provider?,
                arg1: YouTubeInitializationResult?
            ) {
                // TODO Auto-generated method stub
            }
        })
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.youtube_playerFragment, youTubePlayerFragment).commit()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (this@ShowViewFragment::YPlayer.isInitialized) {
                YPlayer.setFullscreen(false)
            }
        } else {
            if (this@ShowViewFragment::YPlayer.isInitialized && YPlayer.isPlaying) {
                YPlayer.setFullscreen(true)
            }
        }
    }

    private fun formatHoursAndMinutes(totalMinutes: Int): String? {
        var minutes = (totalMinutes % 60).toString()
        minutes = if (minutes.length == 1) "0$minutes" else minutes
        return if ((totalMinutes / 60).toString() == "0") {
            if (minutes.length == 1) "$minutes min" else "$minutes mins"
        } else
            ((totalMinutes / 60).toString()) + "h " + minutes + "m"
    }


    private fun initViewForShow() {
        if (this::eventModel.isInitialized || reminder) {
            playCurrentShow.visibility = View.GONE
            reminderView.visibility = View.VISIBLE
            share.visibility = View.GONE
            showTitleContainer.setPadding(0, 0, 0, 0)
        } else {
            playCurrentShow.visibility = View.VISIBLE
            share.visibility = View.VISIBLE
            reminderView.visibility = View.GONE
        }

        reminderView.setOnClickListener {
            AlertDialogUtility.reminderAppDialog(R.layout.reminder_dialog_layout, requireActivity())
        }

        if (this::featuredShows.isInitialized && featuredShows.seasonsEpisodes.size > 1 && !this::eventModel.isInitialized) {
            moreEpisodesContainer.visibility = View.VISIBLE
            moreEpisodesInit()
        } else {
            moreEpisodesContainer.visibility = View.INVISIBLE
        }

        if (this::eventModel.isInitialized) {
            activity.let {
                Glide.with(this).load(eventModel.thumbnail)
                    .placeholder(it.resources.getDrawable(R.drawable.default_thumbnail, null))
                    .into(showImage)
            }
        }
        if (this::featuredShows.isInitialized) {
            activity.let {
                Glide.with(this).load(featuredShows.thumbnail)
                    .placeholder(
                        it.resources.getDrawable(
                            R.drawable.default_thumbnail,
                            null
                        )
                    )
                    .into(showImage)
            }

            if (this::eventModel.isInitialized || reminder) {
                playLayout.visibility = View.GONE
                showTittle.textSize = 25f
                showDate.textSize = 15f
                showDate.text = DateTimeUtils.convertServerISOTime(
                    AppConstant.DateTime.REMINDER_FORMAT,
                    featuredShows.releaseTime
                )
            } else {
                playLayout.visibility = View.VISIBLE
                showDate.text = DateTimeUtils.convertServerISOTime(
                    AppConstant.DateTime.SIMPLE_DATE_FORMAT,
                    featuredShows.releaseTime
                )
            }
            showTittle.text = featuredShows.title
            /*val time = DateTimeUtils.convertServerISOTime(
                AppConstant.DateTime.TIME_FORMAT_HOURS,
                featuredShows.releaseTime
            )
            showTittle.text = "$time - ${DateTimeUtils.getAdditionalTimeWithDuration(
                time!!,
                AppConstant.DateTime.TIME_FORMAT_HOURS,
                featuredShows.duration
            )}"*/
            if (featuredShows.radio) {
                showType.text = "Listen Live"
            } else {
                showType.text = featuredShows.type
            }
            showDuration.text = formatHoursAndMinutes(featuredShows.duration)
            show_detail_layout.visibility = View.VISIBLE
            showDescriptionDetail.text = featuredShows.description
            showDescriptionDetail.tag = true


            playCurrentShow.setOnClickListener {
                if (!isFromSeasonClick) {
                    index = 0
                }
                if (featuredShows.type == "Video" || (featuredShows.seasonsEpisodes.isNotEmpty() && featuredShows.seasonsEpisodes[index].link.contains(
                        "youtube"
                    ))
                ) {
                    if (mediaPlayer.playWhenReady)
                        mediaPlayer.playWhenReady = false
                    if (featuredShows.seasonsEpisodes.isNotEmpty() && !featuredShows.seasonsEpisodes[index].link.contains(
                            "youtube"
                        )
                    ) {
                        activity?.let { AppUtility.showToast(it, "Source Error") }
                        return@setOnClickListener
                    }
                    val videoCode = featuredShows.seasonsEpisodes[index].link.split("v=")[1]
                    initYoutubePlayerView(videoCode)
                } else {
                    if (this@ShowViewFragment::YPlayer.isInitialized && YPlayer.isPlaying) {
                        YPlayer.pause()
                    }
                    if (isFromSeasonClick) {
                        EventBus.getDefault().post(
                            MessageEvent(
                                MessageEvent.PLAY_PODCAST_SOURCE_MORE,
                                index
                            )
                        )
                    }
                    EventBus.getDefault().post(
                        MessageEvent(
                            MessageEvent.PLAY_PODCAST_SOURCE,
                            featuredShows
                        )
                    )
                    isFromSeasonClick = false
                }
            }

            share.setOnClickListener {

                if (featuredShows.radio) {
                    activity?.let { it1 ->
                        AppUtility.shareAppContent(
                            it1,
                            "Listen to live commentary/discussion for ${featuredShows.title} on Sports Flashes ${"www.xyz.com"}"
                        )
                    }
                } else if (featuredShows.seasonsEpisodes.isNotEmpty() && featuredShows.type.equals(
                        "podacast",
                        true
                    )
                ) {
                    activity?.let { it1 ->
                        AppUtility.shareAppContent(
                            it1,
                            "Listen to podcast ${featuredShows.title} on Sports Flashes"
                        )
                    }
                } else if (featuredShows.seasonsEpisodes.isNotEmpty() && featuredShows.seasonsEpisodes[0].live) {
                    if (featuredShows.type.equals("podcast", true)) {
                        activity?.let { it1 ->
                            AppUtility.shareAppContent(
                                it1,
                                "Listen to podcast ${featuredShows.title} on Sports Flashes"
                            )
                        }
                    } else {
                        activity?.let { it1 ->
                            AppUtility.shareAppContent(
                                it1,
                                "Watch ${featuredShows.title} on Sports Flashes"
                            )
                        }
                    }
                } else {
                    activity?.let { it1 ->
                        AppUtility.shareAppContent(
                            it1,
                            "Watch ${featuredShows.title} on Sports Flashes"
                        )
                    }
                }
            }
        }
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
                showDescriptionDetail.maxLines = 4
                showDescriptionDetail.tag = true
            }
            readMore.visibility = View.VISIBLE
        }
    }

    override fun onSeasonClick(position: Int) {
        isFromSeasonClick = true
        index = position + 1
        playCurrentShow.performClick()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            val a: Activity? = activity
            if (a != null) a.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val a: Activity? = activity
        if (a != null) a.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}