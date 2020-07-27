package com.sports.sportsflashes.view.activites

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ActivityNavigator
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.sports.sportsflashes.R
import com.sports.sportsflashes.common.application.SFApplication
import com.sports.sportsflashes.common.helper.CurrentShowClickListener
import com.sports.sportsflashes.common.helper.FeaturedShowsListImpl
import com.sports.sportsflashes.common.utils.AppConstant
import com.sports.sportsflashes.common.utils.AppUtility
import com.sports.sportsflashes.model.FeaturedShows
import com.sports.sportsflashes.model.MessageEvent
import com.sports.sportsflashes.model.SportCategories
import com.sports.sportsflashes.repository.api.STATUS
import com.sports.sportsflashes.view.adapters.CategoryAdapter
import com.sports.sportsflashes.view.adapters.CircularShowAdapter
import com.sports.sportsflashes.viewmodel.MainActivityViewModel
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.menu_view.*
import kotlinx.android.synthetic.main.podcast_play_view.*
import kotlinx.android.synthetic.main.podcast_play_view.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject


class MainActivity : AppCompatActivity(), FeaturedShowsListImpl, CurrentShowClickListener,
    CategoryAdapter.CategoryClickedListener {

    private var seasonIndex: Int = 0
    private lateinit var viewModel: MainActivityViewModel

    @Inject
    lateinit var mediaPlayer: ExoPlayer
    private var seekBarHandler = Handler()
    private lateinit var updateSongTime: Runnable
    private lateinit var podcastPlayerView: BottomSheetBehavior<RelativeLayout>
    private lateinit var featuredShows: List<FeaturedShows>
    private lateinit var bottomSheet: RelativeLayout
    private lateinit var show: FeaturedShows

    @Inject
    lateinit var gson: Gson


    override fun onCreate(savedInstanceState: Bundle?) {
        window.navigationBarColor = resources.getColor(R.color.black, null)
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        SFApplication.getAppComponent().inject(this)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        setContentView(R.layout.activity_main)
        initPodcastBottomSheet()
        setSupportActionBar(toolbar)
        initMenuOptions()
        setCategories()
        onItemClickOptionMenu()

        settings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
//        https://pwdown.com/14448/Dj%20Waley%20Babu%20-%20Badshah.mp3
//        https://file-examples.com/wp-content/uploads/2017/11/file_example_MP3_5MG.mp3
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

    private fun initPodcastBottomSheet() {
        bottomSheet = findViewById(R.id.radio)
        podcastPlayerView = BottomSheetBehavior.from(bottomSheet)
        podcastPlayerView.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset > 0.1f && slideOffset <= 1.0f) {
                    podcast_actionView.alpha = 1.0f - slideOffset
                    podcast_actionView_opened.alpha = 1.0f + slideOffset
                } else {
                    podcast_actionView.alpha = slideOffset + 1.0f
                    podcast_actionView_opened.alpha = slideOffset
                }
                podcast_actionView.animate().scaleX(1 - slideOffset).scaleY(1 - slideOffset)
                    .setDuration(0).start()
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
        })

        drag_podcast_view.setOnClickListener {
            if (podcastPlayerView.state == BottomSheetBehavior.STATE_COLLAPSED) {
                podcastPlayerView.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                podcastPlayerView.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
        drag_podcast_down.setOnClickListener {
            if (podcastPlayerView.state == BottomSheetBehavior.STATE_COLLAPSED) {
                podcastPlayerView.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                podcastPlayerView.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
        bottomSheet.play_podcast.setOnClickListener {
            play.performClick()
        }
    }

    private fun initPodcastViewer(show: FeaturedShows) {
        if (show.type.equals("podcast", true) || show.radio) {
            if (show.radio || show.seasonsEpisodes[seasonIndex].live) {
                backwardRadio.visibility = View.GONE
                forwardRadio.visibility = View.GONE
                linearLayout.setBackgroundResource(android.R.color.transparent)
            }
            /* Glide.with(this)
                 .load(show.thumbnail)
                 .apply(RequestOptions.bitmapTransform(BlurTransformation(20, 2)))
                 .into(podcast_thumb)*/
            Glide.with(this)
                .load(show.thumbnail)
                .into(showIcon)
            showName.text = show.title
            showDisc.text = show.description
            play.setOnClickListener {
                if (mediaPlayer.playbackState == Player.STATE_ENDED) {
                    mediaPlayer.seekTo(0)
                    mediaPlayer.playWhenReady = true
                } else
                    if (mediaPlayer.playWhenReady) {
                        mediaPlayer.playWhenReady = false
                        mediaActionButton.setBackgroundResource(R.drawable.play_button)
                        play_podcast.setBackgroundResource(R.drawable.play_button)
                    } else if (!mediaPlayer.playWhenReady) {
                        mediaPlayer.playWhenReady = true
                        mediaActionButton.setBackgroundResource(R.drawable.stop_button)
                        play_podcast.setBackgroundResource(R.drawable.stop_button)
                    }
            }
            if (!show.radio) {
                bottomSheet.rjNameRadio.text = show.creator
                bottomSheet.showTimeRadio.text = show.releaseTime
            } else {
                bottomSheet.showTimeRadio.text = "Live Radio"
            }
            bottomSheet.showNameRadio.text = show.title

            try {
                volume.setOnClickListener {
                    if (volumeSeekBar.visibility == View.VISIBLE)
                        volumeSeekBar.visibility = View.GONE
                    else
                        volumeSeekBar.visibility = View.VISIBLE
                }
                val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                volumeSeekBar.max = audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                volumeSeekBar.progress = audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC)
                volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekbar: SeekBar?, progress: Int, p2: Boolean) {
                        audioManager.setStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            progress, 0
                        )
                        if (progress == 0) {
                            volume.setBackgroundResource(R.drawable.ic_baseline_volume_off_24)
                        } else {
                            volume.setBackgroundResource(R.drawable.ic_baseline_volume_up_white)
                        }
                    }

                    override fun onStartTrackingTouch(seekbar: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(seekbar: SeekBar?) {
                    }

                })

            } catch (e: Exception) {
                e.printStackTrace()
            }
            this.show = show
            if (show.radio) {
                exoPlayerInit(show.link)
            } else
                exoPlayerInit(show.seasonsEpisodes[seasonIndex].link)
            sharePodcastAction.setOnClickListener {
                if (show.radio) {
                    AppUtility.shareAppContent(
                        this,
                        "Listen to live commentary/discussion for ${show.title} on Sports Flashes ${"www.xyz.com"}"
                    )
                } else if (show.seasonsEpisodes.isNotEmpty() && show.type.equals(
                        "podacast",
                        true
                    )
                ) {
                    AppUtility.shareAppContent(
                        this,
                        "Listen to podcast ${show.title} on Sports Flashes  ${"www.xyz.com"}"
                    )
                } else if (show.seasonsEpisodes.isNotEmpty() && show.seasonsEpisodes[seasonIndex].live) {
                    if (show.type.equals("podcast", true)) {
                        AppUtility.shareAppContent(
                            this,
                            "Listen to podcast ${show.title} on Sports Flashes  ${"www.xyz.com"}"
                        )
                    } else {
                        AppUtility.shareAppContent(
                            this,
                            "Watch ${show.title} on Sports Flashes  ${"www.xyz.com"}"
                        )
                    }
                } else {
                    AppUtility.shareAppContent(
                        this,
                        "Watch ${show.title} on Sports Flashes  ${"www.xyz.com"}"
                    )
                }
            }
        } else {
//            val video_id = show.seasonsEpisodes[0].link.split("v=")[1]
            findNavController(R.id.app_host_fragment).navigate(
                R.id.playableShowFragment,
                Bundle().apply {
                    this.putString(
                        AppConstant.BundleExtras.FEATURED_SHOW,
                        gson.toJson(show)
                    )
                    this.putString(AppConstant.BundleExtras.FROM_HOME, "yes")
                })

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
                    mediaActionButton.setBackgroundResource(R.drawable.stop_button)
                    play_podcast.setBackgroundResource(R.drawable.stop_button)
                    initSeek()
                } else {
                    if (this@MainActivity::updateSongTime.isInitialized)
                        seekBarHandler.removeCallbacks(updateSongTime)
                }
            }
        })
        forwardRadio.setOnClickListener {
            mediaPlayer.seekTo(mediaPlayer.currentPosition + 20000)
        }
        backwardRadio.setOnClickListener {
            mediaPlayer.seekTo(mediaPlayer.currentPosition - 20000)
        }

        val mediaSource = extractMediaSourceFromUri(Uri.parse(streamUrl))
        mediaPlayer.prepare(mediaSource)
        mediaPlayer.playWhenReady = true
    }

    private fun extractMediaSourceFromUri(uri: Uri): MediaSource {
        val userAgent = Util.getUserAgent(this, "SportsFlashes Radio")
        return ExtractorMediaSource.Factory(DefaultDataSourceFactory(this, userAgent))
            .setExtractorsFactory(DefaultExtractorsFactory()).createMediaSource(uri)
    }

    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }

    private fun initSeek() {
        if (show.radio || this.show.seasonsEpisodes[0].live) {
//            smallSeekBar.progress = 100F
            largeSeekBar.progress = 100F
//            smallSeekBar.isEnabled = false
            largeSeekBar.isEnabled = false
        } else {
            val duration: Long = mediaPlayer.duration
            val amountToUpdate = duration / 100
//            smallSeekBar.max = amountToUpdate.toFloat()
            largeSeekBar.max = amountToUpdate.toFloat()
            updateSongTime = object : Runnable {
                override fun run() {
                    val startTime = mediaPlayer.currentPosition
//                    smallSeekBar.progress = startTime.toFloat() / 100
                    largeSeekBar.progress = startTime.toFloat() / 100
                    seekBarHandler.postDelayed(this, 1000)
                }
            }
            seekBarHandler.post(updateSongTime)
        }
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
                    R.id.scheduleFragment,
                    Bundle().apply {
                        this.putString(
                            AppConstant.BundleExtras.FEATURED_SHOW_LIST,
                            gson.toJson(featuredShows)
                        )
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
                findNavController(R.id.app_host_fragment).navigateUp()
                findNavController(R.id.app_host_fragment).navigate(
                    R.id.eventsFragment, null
                )
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
                findNavController(R.id.app_host_fragment).navigateUp()
                findNavController(R.id.app_host_fragment).navigate(
                    R.id.action_homeFragment_to_reminderFragment,
                    Bundle().apply {
                        this.putString(
                            AppConstant.BundleExtras.FEATURED_SHOW_LIST,
                            gson.toJson(featuredShows)
                        )
                    }
                )
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
        initPodcastViewer(featuredShows)
    }

    override fun finish() {
        super.finish()
        ActivityNavigator.applyPopAnimationsToPendingTransition(this)
    }

    override fun onBackPressed() {
        when {
            menu_item_.visibility == View.VISIBLE -> menu_drawer.performClick()
            this::podcastPlayerView.isInitialized && podcastPlayerView.state == BottomSheetBehavior.STATE_EXPANDED -> {
                podcastPlayerView.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            else -> super.onBackPressed()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(messageEvent: MessageEvent) {
        when (messageEvent.type) {
            MessageEvent.HOME_FRAGMENT -> {
                homeView.setBackgroundResource(R.drawable.circle_red)
                arrayOf(schedualView, downloadView, eventView, reminderView).forEach {
                    it.setBackgroundResource(R.drawable.circle_white)
                }
                schedualIcon.background = resources.getDrawable(R.drawable.schedule, null)
                eventIcon.background = resources.getDrawable(R.drawable.highlights, null)
                homeIcon.background = resources.getDrawable(R.drawable.home_white, null)
                reminderIcon.background = resources.getDrawable(R.drawable.reminder, null)
                downloadIcon.background = resources.getDrawable(R.drawable.download, null)
            }
            MessageEvent.PLAY_PODCAST_SOURCE -> {
                val show = messageEvent.data as FeaturedShows
                initPodcastViewer(show)
            }
            MessageEvent.PLAY_PODCAST_SOURCE_MORE -> {
                seasonIndex = messageEvent.data as Int
            }
        }
    }

    override fun categoryClicked(categoryId: String) {
        findNavController(R.id.app_host_fragment).navigateUp()
        findNavController(R.id.app_host_fragment).navigate(
            R.id.action_homeFragment_to_categoryShowFragment,
            Bundle().apply {
                this.putString(
                    AppConstant.BundleExtras.CATEGORY_ID,
                    categoryId
                )
            })
        menu_drawer.performClick()
    }

}