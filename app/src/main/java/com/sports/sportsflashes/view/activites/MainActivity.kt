package com.sports.sportsflashes.view.activites

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.sports.sportsflashes.R
import com.sports.sportsflashes.common.application.SFApplication
import com.sports.sportsflashes.common.helper.CurrentShowClickListener
import com.sports.sportsflashes.common.helper.FeaturedShowsListImpl
import com.sports.sportsflashes.common.utils.AppConstant
import com.sports.sportsflashes.model.FeaturedShows
import com.sports.sportsflashes.model.SportCategories
import com.sports.sportsflashes.repository.STATUS
import com.sports.sportsflashes.view.adapters.CategoryAdapter
import com.sports.sportsflashes.view.adapters.CircularShowAdapter
import com.sports.sportsflashes.view.fragments.ScheduleFragment
import com.sports.sportsflashes.viewmodel.MainActivityViewModel
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.menu_view.*
import kotlinx.android.synthetic.main.podcast_play_view.*
import java.util.*
import javax.inject.Inject


class MainActivity : AppCompatActivity(), FeaturedShowsListImpl, CurrentShowClickListener {

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var mediaPlayer: ExoPlayer
    private var seekBarHandler = Handler()
    private lateinit var updateSongTime: Runnable
    private lateinit var podcastPlayerView: BottomSheetBehavior<RelativeLayout>
    private lateinit var featuredShows: List<FeaturedShows>

    @Inject
    lateinit var gson: Gson

    private fun getMediaPlayer() {
        val renderersFactory = DefaultRenderersFactory(this)
        val bandwidthMeter = DefaultBandwidthMeter()
        val trackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(trackSelectionFactory)
        val loadControl = DefaultLoadControl.Builder()
            .setAllocator(DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE))
            .setBufferDurationsMs(
                AppConstant.MIN_BUFFER,
                AppConstant.MAX_BUFFER,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
            )
            .setTargetBufferBytes(DefaultLoadControl.DEFAULT_TARGET_BUFFER_BYTES)
            .setPrioritizeTimeOverSizeThresholds(DefaultLoadControl.DEFAULT_PRIORITIZE_TIME_OVER_SIZE_THRESHOLDS)
            .createDefaultLoadControl()
        mediaPlayer = ExoPlayerFactory.newSimpleInstance(
            this,
            renderersFactory,
            trackSelector,
            loadControl,
            null,
            bandwidthMeter
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SFApplication.getAppComponent().inject(this)
        setContentView(R.layout.activity_main)
        initFragmentHost()
        setSupportActionBar(toolbar)
        initMenuOptions()
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        getMediaPlayer()
        setCategories()
        initPodcastViewer()
        onItemClickOptionMenu()
//        https://pwdown.com/14448/Dj%20Waley%20Babu%20-%20Badshah.mp3
//        https://file-examples.com/wp-content/uploads/2017/11/file_example_MP3_5MG.mp3
        exoPlayerInit("https://file-examples.com/wp-content/uploads/2017/11/file_example_MP3_5MG.mp3")
    }


    private fun setCategories() {
        viewModel.getCategories().observe(this, Observer {
            if (it.status == STATUS.SUCCESS) {
                val categories = it.data as ArrayList<SportCategories>
                category_recyclerView.adapter = CategoryAdapter(categories)
            }
        })
    }

    private fun setMenuShows() {
        menu_show_recyclerView.setHasFixedSize(true)
        menu_show_recyclerView.layoutManager = LinearLayoutManager(this).also {
            it.reverseLayout = false
            it.orientation = LinearLayoutManager.HORIZONTAL
        }

    }

    private fun initFragmentHost() {
        /*navController =
            Navigation.findNavController(R.id.nav_graph)*/

    }

    private fun initPodcastViewer() {
        val bottomSheet = findViewById<RelativeLayout>(R.id.radio)
        podcastPlayerView = BottomSheetBehavior.from(bottomSheet)
        Glide.with(this)
            .load("https://thumbnails-sportsflashes.s3.ap-south-1.amazonaws.com/bee19793-1e8b-4292-9bff-23cc99205803.jpg")
            .apply(RequestOptions.bitmapTransform(BlurTransformation(20, 2))).into(podcast_thumb)
        podcastPlayerView.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset > 0.1f && slideOffset <= 1.0f) {
                    podcast_actionView.alpha = 1.0f - slideOffset
                } else {
                    podcast_actionView.alpha = slideOffset + 1.0f
                }
                podcast_actionView.animate().scaleX(1 - slideOffset).scaleY(1 - slideOffset)
                    .setDuration(0).start();
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
        })

        play.setOnClickListener {
            if (mediaPlayer.playbackState == Player.STATE_ENDED) {
                mediaPlayer.seekTo(0)
                mediaPlayer.playWhenReady = true
            } else
                if (mediaPlayer.playWhenReady) {
                    mediaPlayer.playWhenReady = false
                } else if (!mediaPlayer.playWhenReady) {
                    mediaPlayer.playWhenReady = true
                }
        }


        drag_podcast_view.setOnClickListener {
            if (podcastPlayerView.state == BottomSheetBehavior.STATE_COLLAPSED) {
                podcastPlayerView.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                podcastPlayerView.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }


    private fun initMenuOptions() {
        category_recyclerView.setHasFixedSize(true)
        category_recyclerView.layoutManager = LinearLayoutManager(this).also {
            it.orientation = LinearLayoutManager.HORIZONTAL
            it.reverseLayout = false
        }
        menu_drawer.setOnClickListener {
            if (menu_item_.visibility == View.GONE) {
                menu_item_.visibility = View.VISIBLE
                val animSlideDown: Animation =
                    AnimationUtils.loadAnimation(this, R.anim.slide_down)
                menu_item_.startAnimation(animSlideDown)
            } else {
                val animSlideUp: Animation =
                    AnimationUtils.loadAnimation(this, R.anim.slide_up)
                menu_item_.startAnimation(animSlideUp)
                menu_item_.postDelayed({ menu_item_.visibility = View.GONE }, 200)
            }
        }

    }


    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase!!))
    }


    private fun exoPlayerInit(streamUrl: String) {
        mediaPlayer.addListener(object : Player.EventListener {

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {

            }

            override fun onSeekProcessed() {
            }

            override fun onTracksChanged(
                trackGroups: TrackGroupArray?,
                trackSelections: TrackSelectionArray?
            ) {

            }

            override fun onPlayerError(error: ExoPlaybackException?) {
            }

            override fun onLoadingChanged(isLoading: Boolean) {
            }

            override fun onPositionDiscontinuity(reason: Int) {
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            }

            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playWhenReady) {
                    initSeek()
                } else {
                    if (this@MainActivity::updateSongTime.isInitialized)
                        seekBarHandler.removeCallbacks(updateSongTime)
                }
            }
        })

        val dataSourceFactory = DefaultDataSourceFactory(applicationContext, "SportsFlashes Radio")
        val extractorsFactory = DefaultExtractorsFactory()
        val mediaSource = ExtractorMediaSource(
            Uri.parse(streamUrl),
            dataSourceFactory,
            extractorsFactory,
            null,
            null
        )
        mediaPlayer.prepare(mediaSource)

    }

    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }

    private fun initSeek() {
        val duration: Long = mediaPlayer.duration
        val amountToUpdate = duration / 100
        smallSeekBar.max = amountToUpdate.toFloat()
        largeSeekBar.max = amountToUpdate.toFloat()
        updateSongTime = object : Runnable {
            override fun run() {
                val startTime = mediaPlayer.currentPosition
                smallSeekBar.progress = startTime.toFloat() / 100
                largeSeekBar.progress = startTime.toFloat() / 100
                seekBarHandler.postDelayed(this, 1000)
            }
        }
        seekBarHandler.post(updateSongTime)
    }

    private fun onItemClickOptionMenu() {
        schedualView.setOnClickListener { v -> changeViewOfOptionMenuClick(v) }
        downloadView.setOnClickListener { v -> changeViewOfOptionMenuClick(v) }
        eventView.setOnClickListener { v -> changeViewOfOptionMenuClick(v) }
        reminderView.setOnClickListener { v -> changeViewOfOptionMenuClick(v) }
        homeView.setOnClickListener { v -> changeViewOfOptionMenuClick(v) }
    }


    private fun changeViewOfOptionMenuClick(clickedView: View) {
        clickedView.setBackgroundResource(R.drawable.circle_red)

        when (clickedView) {
            schedualView -> {
                findNavController(R.id.app_host_fragment).navigate(
                    R.id.action_homeFragment_to_scheduleFragment,
                    Bundle().apply {
                        this.putString("CHECK",gson.toJson(featuredShows))
                    })
                arrayOf(downloadView, eventView, homeView, reminderView).forEach {
                    it.setBackgroundResource(R.drawable.circle_white)
                }
                downloadIcon.background = resources.getDrawable(R.drawable.download, null)
                eventIcon.background = resources.getDrawable(R.drawable.highlights, null)
                homeIcon.background = resources.getDrawable(R.drawable.home_black, null)
                reminderIcon.background = resources.getDrawable(R.drawable.reminder, null)
                schedualIcon.background = resources.getDrawable(R.drawable.schedule_white, null)
            }
            downloadView -> {
                arrayOf(schedualView, eventView, homeView, reminderView).forEach {
                    it.setBackgroundResource(R.drawable.circle_white)
                }
                schedualIcon.background = resources.getDrawable(R.drawable.schedule, null)
                eventIcon.background = resources.getDrawable(R.drawable.highlights, null)
                homeIcon.background = resources.getDrawable(R.drawable.home_black, null)
                reminderIcon.background = resources.getDrawable(R.drawable.reminder, null)
                downloadIcon.background = resources.getDrawable(R.drawable.download_white, null)
            }
            eventView -> {
                arrayOf(schedualView, downloadView, homeView, reminderView).forEach {
                    it.setBackgroundResource(R.drawable.circle_white)
                }
                schedualIcon.background = resources.getDrawable(R.drawable.schedule, null)
                eventIcon.background = resources.getDrawable(R.drawable.highlights_white, null)
                homeIcon.background = resources.getDrawable(R.drawable.home_black, null)
                reminderIcon.background = resources.getDrawable(R.drawable.reminder, null)
                downloadIcon.background = resources.getDrawable(R.drawable.download, null)
            }
            reminderView -> {
                arrayOf(schedualView, downloadView, homeView, eventView).forEach {
                    it.setBackgroundResource(R.drawable.circle_white)
                }
                schedualIcon.background = resources.getDrawable(R.drawable.schedule, null)
                eventIcon.background = resources.getDrawable(R.drawable.highlights, null)
                homeIcon.background = resources.getDrawable(R.drawable.home_black, null)
                reminderIcon.background = resources.getDrawable(R.drawable.reminder_white, null)
                downloadIcon.background = resources.getDrawable(R.drawable.download, null)
            }
            homeView -> {
                findNavController(R.id.app_host_fragment).popBackStack()
                arrayOf(schedualView, downloadView, eventView, reminderView).forEach {
                    it.setBackgroundResource(R.drawable.circle_white)
                }
                schedualIcon.background = resources.getDrawable(R.drawable.schedule, null)
                eventIcon.background = resources.getDrawable(R.drawable.highlights, null)
                homeIcon.background = resources.getDrawable(R.drawable.home_white, null)
                reminderIcon.background = resources.getDrawable(R.drawable.reminder, null)
                downloadIcon.background = resources.getDrawable(R.drawable.download, null)
            }
        }
        menu_drawer.performClick()
    }

    override fun setShowsList(featuredShows: List<FeaturedShows>) {
        this.featuredShows = featuredShows
        setMenuShows()
        menu_show_recyclerView.adapter =
            CircularShowAdapter(featuredShows, {
                var smallItemWidth = it
            }, this, true)
    }

    override fun onCurrentShowClicked(featuredShows: FeaturedShows) {
        showName.text = featuredShows.title
        showDisc.text = featuredShows.description
        if (mediaPlayer.playbackState == Player.STATE_ENDED) {
            mediaPlayer.seekTo(0)
            mediaPlayer.playWhenReady = true
        } else
            if (mediaPlayer.playWhenReady) {
                mediaPlayer.playWhenReady = false
            } else if (!mediaPlayer.playWhenReady) {
                mediaPlayer.playWhenReady = true
            }
    }

}


