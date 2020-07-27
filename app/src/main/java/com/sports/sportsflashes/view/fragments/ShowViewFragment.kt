package com.sports.sportsflashes.view.fragments

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

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var mediaPlayer: ExoPlayer

    init {
        SFApplication.getAppComponent().inject(this)
//        EventBus.getDefault().register(this)
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
                if (activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    if (playerContainer.visibility == View.VISIBLE && this@ShowViewFragment::YPlayer.isInitialized) {
                        YPlayer.setFullscreen(false)
                    }
                } else {
                    findNavController().popBackStack()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback);
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
            if (this@ShowViewFragment::YPlayer.isInitialized) {
                YPlayer.setFullscreen(true)
            }
        }
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

        reminderView.setOnClickListener {
            AlertDialogUtility.reminderAppDialog(R.layout.reminder_dialog_layout, requireActivity())
        }

        if (featuredShows.seasonsEpisodes.size > 1 && !this::eventModel.isInitialized) {
            moreEpisodesContainer.visibility = View.VISIBLE
            moreEpisodesInit()
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
        playCurrentShow.setOnClickListener {
            if (!isFromSeasonClick) {
                index = 0
            }
            isFromSeasonClick = false
            if (featuredShows.type == "Video") {
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

    override fun onSeasonClick(position: Int) {
        isFromSeasonClick = true
        index = position + 1
        playCurrentShow.performClick()
    }
}