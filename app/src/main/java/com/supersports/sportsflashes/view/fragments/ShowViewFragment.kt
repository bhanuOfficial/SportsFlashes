package com.supersports.sportsflashes.view.fragments

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment.newInstance
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.supersports.sportsflashes.R
import com.supersports.sportsflashes.common.application.SFApplication
import com.supersports.sportsflashes.common.utils.AlertDialogUtility
import com.supersports.sportsflashes.common.utils.AppConstant
import com.supersports.sportsflashes.common.utils.AppUtility
import com.supersports.sportsflashes.common.utils.DateTimeUtils
import com.supersports.sportsflashes.model.*
import com.supersports.sportsflashes.repository.api.STATUS
import com.supersports.sportsflashes.view.activites.MainActivity
import com.supersports.sportsflashes.view.adapters.MoreEpisodeAdapter
import com.supersports.sportsflashes.viewmodel.ShowFragmentViewModel
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.dashboard_full_image_show.*
import kotlinx.android.synthetic.main.playable_item_layout.playCurrentShow
import kotlinx.android.synthetic.main.playable_item_layout.readMore
import kotlinx.android.synthetic.main.playable_item_layout.showDescriptionDetail
import kotlinx.android.synthetic.main.playable_item_layout.showTittle
import kotlinx.android.synthetic.main.playable_item_layout.show_detail_layout
import kotlinx.android.synthetic.main.show_view_layout.*
import kotlinx.android.synthetic.main.show_view_layout.playLayout
import org.greenrobot.eventbus.EventBus
import java.lang.reflect.Type
import javax.inject.Inject


/**
 *Created by Bhanu on 04-07-2020
 */
class ShowViewFragment : Fragment(), MoreEpisodeAdapter.OnSeasonItemClickedListener {

    private var isFromSeasonClick: Boolean = false
    private var index: Int = 0
    private lateinit var featuredShows: FeaturedShows
    private lateinit var eventModel: MonthEventModel
    private lateinit var youTubePlayer: YouTubePlayer
    private lateinit var liveSeason: LiveSeasonModel.Live
    private var reminder: Boolean = false
    private lateinit var activity: MainActivity
    private var selectedSeason = -1

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var mediaPlayer: ExoPlayer
    private var isVisibleToUser = false
    private var fromLiveSchedule = false
    private lateinit var showFragmentViewModel: ShowFragmentViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var moreEpidsodeAdapter: MoreEpisodeAdapter
    private var moreEpisodesList = arrayListOf<SeasonsEpisode>()
    private lateinit var seasons: Seasons

    init {
        SFApplication.getAppComponent().inject(this)
//        EventBus.getDefault().register(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity

    }

    private fun getSeasonById(sessionId: String) {
        showFragmentViewModel.getSeasonById(sessionId).observe(requireActivity(), Observer {
            if (it.status == STATUS.SUCCESS) {
                hideProgress()
                moreEpisodesList.clear()
                val data = it.data as LiveSeasonModel
                val userListType: Type = object : TypeToken<ArrayList<SeasonsEpisode>?>() {}.type
                val userArray: ArrayList<SeasonsEpisode> =
                    gson.fromJson(gson.toJson(data.episodes), userListType)
                if (userArray.isEmpty()){
                    moreEpisodesContainer.visibility = View.INVISIBLE
                }
                moreEpisodesList.addAll(userArray)
                if (this::moreEpidsodeAdapter.isInitialized)
                    moreEpidsodeAdapter.notifyDataSetChanged()
//                /moreEpisodesList.addAll()
            } else if (it.status == STATUS.ERROR) {
                hideProgress()
                moreEpisodesContainer.visibility = View.INVISIBLE
            }
        })
    }

    private fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        if (progressBar != null)
            progressBar.visibility = View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showFragmentViewModel = ViewModelProvider(this).get(ShowFragmentViewModel::class.java)
        sharedPreferences = requireActivity().getSharedPreferences(
            getString(R.string.pref_key),
            Context.MODE_PRIVATE
        )
        arguments?.let {
            if (it.getString(AppConstant.BundleExtras.FEATURED_SHOW) != null) {
                featuredShows =
                    gson.fromJson(
                        it.getString(AppConstant.BundleExtras.FEATURED_SHOW),
                        FeaturedShows::class.java
                    )
            }

            fromLiveSchedule = it.getBoolean(AppConstant.BundleExtras.FROM_SCHEDULE_LIVE)
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


        mediaPlayer.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playWhenReady && this@ShowViewFragment::youTubePlayer.isInitialized && isVisibleToUser) {
                    if (youTubePlayer.isPlaying)
                        youTubePlayer.pause()
                }
            }
        })
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
                    if (playerContainer.visibility == View.VISIBLE && this@ShowViewFragment::youTubePlayer.isInitialized) {
                        youTubePlayer.setFullscreen(false)
                    }
                } else {
                    findNavController().popBackStack()
                }
            }
        }
        userVisibleHint = true
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        activity.appLogo.setBackgroundResource(
            R.drawable.in_app_logo
        )
        activity.toolbar.setBackgroundColor(resources.getColor(R.color.black, null))
        activity.appLogo.text = ""
        return inflater.inflate(R.layout.show_view_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val seasonArray = ArrayList<String>()
        for (i in 1..featuredShows.seasons.size) {
            seasonArray.add("Season $i")
        }
        val seasonDropDown =
            ArrayAdapter(activity, android.R.layout.simple_spinner_item, seasonArray)
        seasonDropDown.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //Setting the ArrayAdapter data on the Spinner
        //Setting the ArrayAdapter data on the Spinner
        seasonSpinner.adapter = seasonDropDown
        seasonSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                p1: View?,
                position: Int,
                p3: Long
            ) {
                (parent?.getChildAt(0) as TextView).setTextColor(
                    resources.getColor(
                        R.color.white,
                        null
                    )
                )
                showProgress()
                selectedSeason = position

                if ((featuredShows.seasons[selectedSeason]) is String) {
                    getSeasonById((featuredShows.seasons[selectedSeason]) as String)
                } else {
                    val seasonType: Type = object : TypeToken<Seasons?>() {}.type
                    seasons =
                        gson.fromJson(
                            gson.toJson(featuredShows.seasons[selectedSeason]),
                            seasonType
                        )
                    getSeasonById(seasons._id)
                }

            }

        }
        seasonSpinner.setSelection(seasonArray.size - 1, true)
        if (arguments?.getString(AppConstant.BundleExtras.FROM_HOME) != null) {
            selectedSeason = 0
            seasonSpinner.setSelection(0, true)
            if (featuredShows.seasonsEpisodes.isNotEmpty() && featuredShows.seasonsEpisodes[selectedSeason].link.contains(
                    "youtube"
                )
            )
                initYoutubePlayerView(featuredShows.seasonsEpisodes[selectedSeason].link.split("v=")[1])
            else {
                activity.let { AppUtility.showToast(it, "Source Error") }
            }
        } else {
            selectedSeason = seasonArray.size - 1
        }
        initViewForShow()
    }

    private fun moreEpisodesInit() {
        moreEpisodesRecycler.setHasFixedSize(true)
        moreEpisodesRecycler.isNestedScrollingEnabled = false
        moreEpisodesRecycler.layoutManager = LinearLayoutManager(activity).also {
            it.reverseLayout = false
            it.orientation = RecyclerView.VERTICAL
        }
        /* moreEpisodesList =
             featuredShows.seasonsEpisodes.subList(1, featuredShows.seasonsEpisodes.size)*/

        moreEpidsodeAdapter = MoreEpisodeAdapter(
            moreEpisodesList, this
        )
        moreEpisodesRecycler.adapter = moreEpidsodeAdapter
    }

    private fun initYoutubePlayerView(videoCode: String) {
        if (isFromSeasonClick) {
            if (this@ShowViewFragment::youTubePlayer.isInitialized) {
                youTubePlayer.loadVideo(videoCode)
                youTubePlayer.play()
            }
            return
        }
        if (this@ShowViewFragment::activity.isInitialized) {
            val a: Activity? = activity
            if (a != null) a.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        }
        if (mediaPlayer.playWhenReady)
            mediaPlayer.playWhenReady = false
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
                    youTubePlayer = player
//                    YPlayer.setFullscreen(true)
                    youTubePlayer.loadVideo(videoCode)
                    youTubePlayer.play()

                    youTubePlayer.setPlaybackEventListener(object :
                        YouTubePlayer.PlaybackEventListener {
                        override fun onSeekTo(p0: Int) {

                        }

                        override fun onBuffering(p0: Boolean) {
                        }

                        override fun onPlaying() {
                            if (mediaPlayer.playWhenReady)
                                mediaPlayer.playWhenReady = false
                        }

                        override fun onStopped() {
                        }

                        override fun onPaused() {
                        }

                    })

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
            if (this@ShowViewFragment::youTubePlayer.isInitialized) {
                youTubePlayer.setFullscreen(false)
            }
        } else {
            if (this@ShowViewFragment::youTubePlayer.isInitialized) {
                youTubePlayer.setFullscreen(true)
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
            if ((this::eventModel.isInitialized && eventModel.subscribed) || (reminder && featuredShows.subscribed)) {
                reminderView.text = "Remove reminder"
            } else
                reminderView.text = "Set Reminder"
        } else {
            playCurrentShow.visibility = View.VISIBLE
            share.visibility = View.VISIBLE
            reminderView.visibility = View.GONE
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
                            showFragmentViewModel.removeReminder(request)
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
                    showFragmentViewModel.setReminder(request).observe(it1, Observer {
                        if (it.status == STATUS.SUCCESS) {
                            reminderView.text = "Remove Reminder"
                        } else if (it.status == STATUS.ERROR) {

                        }
                    })
                    AlertDialogUtility.reminderAppDialog(
                        R.layout.reminder_dialog_layout, requireActivity(),
                        featuredShows.title, "Your reminder has been set", false, null
                    )
                }
            }
            featuredShows.subscribed = !featuredShows.subscribed
        }

        if (this::featuredShows.isInitialized && !this::eventModel.isInitialized && !featuredShows.radio) {
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

            when {
                featuredShows.radio -> {
                    showType.text = "Listen Live"
                }
                featuredShows.type.equals(
                    "podcast",
                    true
                ) -> {
                    showType.text = "Listen"
                }
                featuredShows.type.equals(
                    "video",
                    true
                ) -> {
                    showType.text = "Watch"
                }
            }

            showDuration.text = formatHoursAndMinutes(featuredShows.duration)
            show_detail_layout.visibility = View.VISIBLE
            showDescriptionDetail.text = featuredShows.description
            showDescriptionDetail.tag = true


            playCurrentShow.setOnClickListener {
                if (!isFromSeasonClick) {
                    index = 0
                }

                if (fromLiveSchedule) {
                    if (featuredShows.link.contains(
                            "youtube"
                        )
                    ) {
                        if (mediaPlayer.playWhenReady)
                            mediaPlayer.playWhenReady = false

                        if (featuredShows.link.isNotEmpty() && !featuredShows.link.contains(
                                "youtube"
                            )
                        ) {
                            activity?.let { AppUtility.showToast(it, "Source Error") }
                            return@setOnClickListener
                        }
                        val videoCode = featuredShows.link.split("v=")[1]
                        initYoutubePlayerView(videoCode)
                    } else {
                        if (this@ShowViewFragment::youTubePlayer.isInitialized && youTubePlayer.isPlaying) {
                            youTubePlayer.pause()
                        }
                        EventBus.getDefault().post(
                            MessageEvent(
                                MessageEvent.PLAY_PODCAST_SOURCE,
                                featuredShows
                            )
                        )
                    }
                } else {
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
                            activity.let { AppUtility.showToast(it, "Source Error") }
                            return@setOnClickListener
                        }
                        val videoCode = featuredShows.seasonsEpisodes[index].link.split("v=")[1]
                        initYoutubePlayerView(videoCode)
                    } else {
                        if (this@ShowViewFragment::youTubePlayer.isInitialized && youTubePlayer.isPlaying) {
                            youTubePlayer.pause()
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

            }

            share.setOnClickListener {

                if (featuredShows.radio) {
                    activity?.let { it1 ->
                        AppUtility.shareAppContent(
                            it1,
                            "Listen to live commentary/discussion for ${featuredShows.title} on Sports Flashes ${resources.getString(R.string.app_url)}"
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
                            "Listen to podcast ${featuredShows.title} on Sports Flashes ${resources.getString(R.string.app_url)}"
                        )
                    }
                } else if (featuredShows.seasonsEpisodes.isNotEmpty() && featuredShows.seasonsEpisodes[selectedSeason].live) {
                    if (featuredShows.type.equals("podcast", true)) {
                        activity?.let { it1 ->
                            AppUtility.shareAppContent(
                                it1,
                                "Listen to podcast ${featuredShows.title} on Sports Flashes ${resources.getString(R.string.app_url)}"
                            )
                        }
                    } else {
                        activity?.let { it1 ->
                            AppUtility.shareAppContent(
                                it1,
                                "Watch ${featuredShows.title} on Sports Flashes ${resources.getString(R.string.app_url)}"
                            )
                        }
                    }
                } else {
                    activity?.let { it1 ->
                        AppUtility.shareAppContent(
                            it1,
                            "Watch ${featuredShows.title} on Sports Flashes ${resources.getString(R.string.app_url)}"
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
        index = position
        playCurrentShow.performClick()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        this.isVisibleToUser = true
    }

    override fun onDestroy() {
        super.onDestroy()
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        isVisibleToUser = false
    }

}