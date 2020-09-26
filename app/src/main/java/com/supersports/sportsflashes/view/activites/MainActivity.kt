package com.supersports.sportsflashes.view.activites

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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ActivityNavigator
import androidx.navigation.Navigation
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
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.supersports.sportsflashes.R
import com.supersports.sportsflashes.common.application.SFApplication
import com.supersports.sportsflashes.common.helper.CurrentShowClickListener
import com.supersports.sportsflashes.common.helper.FeaturedShowsListImpl
import com.supersports.sportsflashes.common.utils.AppConstant
import com.supersports.sportsflashes.common.utils.AppUtility
import com.supersports.sportsflashes.common.utils.DateTimeUtils
import com.supersports.sportsflashes.common.utils.DateTimeUtils.toHourMinuteSeconds
import com.supersports.sportsflashes.model.*
import com.supersports.sportsflashes.repository.api.NetworkResponse
import com.supersports.sportsflashes.repository.api.STATUS
import com.supersports.sportsflashes.view.adapters.CategoryAdapter
import com.supersports.sportsflashes.view.adapters.CircularShowAdapter
import com.supersports.sportsflashes.viewmodel.MainActivityViewModel
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.menu_view.*
import kotlinx.android.synthetic.main.podcast_play_view.*
import kotlinx.android.synthetic.main.podcast_play_view.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.reflect.Type
import javax.inject.Inject


class MainActivity : AppCompatActivity(), FeaturedShowsListImpl, CurrentShowClickListener,
    CategoryAdapter.CategoryClickedListener {

    private var radioStatus: Boolean = false
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
    lateinit var appLogo: TextView
    lateinit var toolbar: Toolbar

    @Inject
    lateinit var gson: Gson


    override fun onCreate(savedInstanceState: Bundle?) {
        window.navigationBarColor = resources.getColor(R.color.black, null)
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        SFApplication.getAppComponent().inject(this)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        setContentView(R.layout.activity_main)
        appLogo = findViewById(R.id.appLogo)
        toolbar = findViewById(R.id.toolbar)
        initPodcastBottomSheet()
        setSupportActionBar(toolbar)
        initMenuOptions()
        getLiveRadio()
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

                val categoriesData = it.data as SportCategories
                val categories = categoriesData.categories
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
            if (show.radio) {
                backwardRadio.visibility = View.GONE
                forwardRadio.visibility = View.GONE
                showTimeRadio.visibility = View.VISIBLE
                linearLayout.setBackgroundResource(android.R.color.transparent)
            } else {
                backwardRadio.visibility = View.VISIBLE
                forwardRadio.visibility = View.VISIBLE
                if (!show.radio)
                    showTimeRadio.visibility = View.VISIBLE
                linearLayout.setBackgroundResource(android.R.color.transparent)
            }
            /* Glide.with(this)
                 .load(show.thumbnail)
                 .apply(RequestOptions.bitmapTransform(BlurTransformation(20, 2)))
                 .into(podcast_thumb)*/
            Glide.with(applicationContext)
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
                bottomSheet.textView.visibility = View.GONE
                radioStatus = false
            } else {
                radioStatus = true
                bottomSheet.textView.visibility = View.VISIBLE
                bottomSheet.textView.text = "Listen Live"
                if (show.thumbnailData is List<*>) {
                    val thumb = show.thumbnailData as ArrayList<ThumbnailData>

                    val userListType: Type = object : TypeToken<ArrayList<ThumbnailData>?>() {}.type
                    val userArray: ArrayList<ThumbnailData> =
                        gson.fromJson(gson.toJson(thumb), userListType)

                    bottomSheet.showTimeRadio.text = DateTimeUtils.calculateTimeBetweenTwoDates(
                        AppConstant.DateTime.DATE_TIME_FORMAT_ISO,
                        userArray[0].endTime,
                        userArray[0].startTime
                    )
                } else {
                    val thumbData: Type = object : TypeToken<ThumbnailData?>() {}.type
                    val thumb: ThumbnailData =
                        gson.fromJson(gson.toJson(show.thumbnailData), thumbData)
                    bottomSheet.showTimeRadio.text =
                        DateTimeUtils.calculateTimeBetweenTwoDates(
                            AppConstant.DateTime.DATE_TIME_FORMAT_ISO,
                            thumb.endTime,
                            thumb.startTime
                        )
                }

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
                exoPlayerInit(show.link, 0)
            } else
                if (show.seasonsEpisodes.isNotEmpty())
                    exoPlayerInit(show.seasonsEpisodes[seasonIndex].link.trim(), show.duration)
            sharePodcastAction.setOnClickListener {
                if (show.radio) {
                    AppUtility.shareAppContent(
                        this,
                        "Listen to live commentary/discussion for ${show.title} on Sports Flashes ${resources.getString(R.string.app_url)}"
                    )
                } else if (show.seasonsEpisodes.isNotEmpty() && show.type.equals(
                        "podacast",
                        true
                    )
                ) {
                    AppUtility.shareAppContent(
                        this,
                        "Listen to podcast ${show.title} on Sports Flashes ${resources.getString(R.string.app_url)}"
                    )
                } else if (show.seasonsEpisodes.isNotEmpty() && show.seasonsEpisodes[seasonIndex].live) {
                    if (show.type.equals("podcast", true)) {
                        AppUtility.shareAppContent(
                            this,
                            "Listen to podcast ${show.title} on Sports Flashes ${resources.getString(R.string.app_url)}"
                        )
                    } else {
                        AppUtility.shareAppContent(
                            this,
                            "Watch ${show.title} on Sports Flashes ${resources.getString(R.string.app_url)}"
                        )
                    }
                } else {
                    AppUtility.shareAppContent(
                        this,
                        "Watch ${show.title} on Sports Flashes ${resources.getString(R.string.app_url)}"
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

    private fun formatHoursAndMinutes(totalMinutes: Int): String? {
        var minutes = (totalMinutes % 60).toString()
        minutes = if (minutes.length == 1) "0$minutes" else minutes
        return if ((totalMinutes / 60).toString() == "0") {
            if (minutes.length == 1) "$minutes min" else "$minutes mins"
        } else
            ((totalMinutes / 60).toString()) + "h " + minutes + "m"
    }


    private fun initMenuOptions() {
        searchView.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        category_recyclerView.setHasFixedSize(true)
        category_recyclerView.layoutManager = LinearLayoutManager(this).also {
            it.orientation = LinearLayoutManager.HORIZONTAL
            it.reverseLayout = false
        }
        menu_drawer.setOnClickListener {
            if (menu_item_.visibility == View.GONE) {
                menu_item_.visibility = View.VISIBLE
                settings.visibility = View.VISIBLE
                menu_icon.setBackgroundResource(R.drawable.ic_baseline_arrow_back_ios_24)
                val animSlideDown: Animation =
                    AnimationUtils.loadAnimation(this, R.anim.slide_down)
                menu_item_.startAnimation(animSlideDown)
            } else {
                settings.visibility = View.GONE
                menu_icon.setBackgroundResource(R.drawable.menu_icon)
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


    private fun exoPlayerInit(streamUrl: String, duration: Int) {
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
                    if (this@MainActivity::bottomSheet.isInitialized && bottomSheet.showTimeRadio != null && !radioStatus)
                        bottomSheet.showTimeRadio.text = toHourMinuteSeconds(mediaPlayer.duration)
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
        Handler().postDelayed({
            mediaPlayer.prepare(mediaSource)
            mediaPlayer.playWhenReady = true
        }, 1000)

    }

    private fun extractMediaSourceFromUri(uri: Uri): MediaSource {
        val userAgent = Util.getUserAgent(this, "SportsFlashes Radio")
        val httpDataSourceFactory = DefaultHttpDataSourceFactory(
            userAgent,
            null,
            DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
            DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
            true
        )

        return ExtractorMediaSource.Factory(
            DefaultDataSourceFactory(
                this,
                null,
                httpDataSourceFactory
            )
        ).setExtractorsFactory(DefaultExtractorsFactory()).createMediaSource(uri)

        /*   val dataSourceFactory =
               DefaultDataSourceFactory(this, Util.getUserAgent(this, "SportsFlashes Radio"), null)
           val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()
           val audioSource: MediaSource =
               ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null)
           return audioSource*/
    }

    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }

    private fun initSeek() {
        var duration: Long = mediaPlayer.duration
        var amountToUpdate = duration / 100
        if (show.radio) {
//            smallSeekBar.progress = 100F
            largeSeekBar.curProcess = 100
//            smallSeekBar.isEnabled = false
            largeSeekBar.isEnabled = false
        }


//            smallSeekBar.max = amountToUpdate.toFloat()
        largeSeekBar.maxProcess = amountToUpdate.toInt()
        updateSongTime = object : Runnable {
            override fun run() {
                val startTime = mediaPlayer.currentPosition
//                    smallSeekBar.progress = startTime.toFloat() / 100
                largeSeekBar.curProcess = startTime.toInt() / 100
                seekBarHandler.postDelayed(this, 1000)
            }
        }
        seekBarHandler.post(updateSongTime)

    }

    private fun onItemClickOptionMenu() {
        schedualView.setOnClickListener { v -> changeViewOfOptionMenuClick(v) }
        fansCornerView.setOnClickListener { v -> changeViewOfOptionMenuClick(v) }
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
                        if (this@MainActivity::featuredShows.isInitialized)
                            this.putString(
                                AppConstant.BundleExtras.FEATURED_SHOW_LIST,
                                gson.toJson(featuredShows)
                            )
                    })
                arrayOf(fansCornerView, eventView, homeView, reminderView).forEach {
                    it.setBackgroundResource(R.drawable.circle_white)
                }
                fansCornerIcon.background =
                    resources.getDrawable(R.drawable.fans_corner_black, null)
                eventIcon.background = resources.getDrawable(R.drawable.highlights, null)
                homeIcon.background = resources.getDrawable(R.drawable.home_black, null)
                reminderIcon.background = resources.getDrawable(R.drawable.reminder, null)
                schedualIcon.background = resources.getDrawable(R.drawable.schedule_white, null)
            }
            fansCornerView -> {
                arrayOf(schedualView, eventView, homeView, reminderView).forEach {
                    it.setBackgroundResource(R.drawable.circle_white)
                }
                schedualIcon.background = resources.getDrawable(R.drawable.schedule, null)
                eventIcon.background = resources.getDrawable(R.drawable.highlights, null)
                homeIcon.background = resources.getDrawable(R.drawable.home_black, null)
                reminderIcon.background = resources.getDrawable(R.drawable.reminder, null)
                fansCornerIcon.background =
                    resources.getDrawable(R.drawable.fans_corner_white, null)
            }
            eventView -> {
                findNavController(R.id.app_host_fragment).navigateUp()
                findNavController(R.id.app_host_fragment).navigate(
                    R.id.eventsFragment, null
                )
                arrayOf(schedualView, fansCornerView, homeView, reminderView).forEach {
                    it.setBackgroundResource(R.drawable.circle_white)
                }
                schedualIcon.background = resources.getDrawable(R.drawable.schedule, null)
                eventIcon.background = resources.getDrawable(R.drawable.highlights_white, null)
                homeIcon.background = resources.getDrawable(R.drawable.home_black, null)
                reminderIcon.background = resources.getDrawable(R.drawable.reminder, null)
                fansCornerIcon.background =
                    resources.getDrawable(R.drawable.fans_corner_black, null)
            }
            reminderView -> {
                findNavController(R.id.app_host_fragment).navigateUp()
                findNavController(R.id.app_host_fragment).navigate(
                    R.id.reminderFragment,
                    Bundle().apply {
                        this.putString(
                            AppConstant.BundleExtras.FEATURED_SHOW_LIST,
                            gson.toJson(featuredShows)
                        )
                    }
                )
                arrayOf(schedualView, fansCornerView, homeView, eventView).forEach {
                    it.setBackgroundResource(R.drawable.circle_white)
                }
                schedualIcon.background = resources.getDrawable(R.drawable.schedule, null)
                eventIcon.background = resources.getDrawable(R.drawable.highlights, null)
                homeIcon.background = resources.getDrawable(R.drawable.home_black, null)
                reminderIcon.background = resources.getDrawable(R.drawable.reminder_white, null)
                fansCornerIcon.background =
                    resources.getDrawable(R.drawable.fans_corner_black, null)
            }
            homeView -> {
                findNavController(R.id.app_host_fragment).popBackStack()
                arrayOf(schedualView, fansCornerView, eventView, reminderView).forEach {
                    it.setBackgroundResource(R.drawable.circle_white)
                }
                schedualIcon.background = resources.getDrawable(R.drawable.schedule, null)
                eventIcon.background = resources.getDrawable(R.drawable.highlights, null)
                homeIcon.background = resources.getDrawable(R.drawable.home_white, null)
                reminderIcon.background = resources.getDrawable(R.drawable.reminder, null)
                fansCornerIcon.background =
                    resources.getDrawable(R.drawable.fans_corner_black, null)
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
        seasonIndex = 0
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
                arrayOf(schedualView, fansCornerView, eventView, reminderView).forEach {
                    it.setBackgroundResource(R.drawable.circle_white)
                }
                schedualIcon.background = resources.getDrawable(R.drawable.schedule, null)
                eventIcon.background = resources.getDrawable(R.drawable.highlights, null)
                homeIcon.background = resources.getDrawable(R.drawable.home_white, null)
                reminderIcon.background = resources.getDrawable(R.drawable.reminder, null)
                fansCornerIcon.background =
                    resources.getDrawable(R.drawable.fans_corner_black, null)
            }
            MessageEvent.PLAY_PODCAST_SOURCE -> {
                val show = messageEvent.data as FeaturedShows
                initPodcastViewer(show)
            }
            MessageEvent.PLAY_PODCAST_SOURCE_MORE -> {
                seasonIndex = messageEvent.data as Int
            }
            MessageEvent.SEARCH_RESULT -> {
                val searchResult = messageEvent.data as String
                Navigation.findNavController(this, R.id.app_host_fragment)
                    .navigate(R.id.playableShowFragment, Bundle().apply {
                        this.putString(
                            AppConstant.BundleExtras.FEATURED_SHOW,
                            searchResult
                        )
                        this.putBoolean(AppConstant.BundleExtras.REMINDER, false)
                    })
            }
        }
    }

    override fun categoryClicked(categoryId: String) {
        findNavController(R.id.app_host_fragment).navigateUp()
        findNavController(R.id.app_host_fragment).navigate(
            R.id.categoryShowFragment,
            Bundle().apply {
                this.putString(
                    AppConstant.BundleExtras.CATEGORY_ID,
                    categoryId
                )
            })
        menu_drawer.performClick()
    }

    private fun getLiveRadio() {
        viewModel.getLiveRadio().observe(this, Observer<NetworkResponse> {
            if (it.status == STATUS.SUCCESS) {
                val radioData = it.data as LiveRadioModel
                val radioModel = radioData.radio
                if (radioModel.isNotEmpty()) {
                    val featuredShows =
                        gson.fromJson(gson.toJson(radioModel[0]), FeaturedShows::class.java)
//                    backwardRadio.visibility = View.GONE
//                    forwardRadio.visibility = View.GONE
//                    if (!show.radio)
//                        showTimeRadio.visibility = View.GONE
//                    linearLayout.setBackgroundResource(android.R.color.transparent)
                    for (i in radioModel) {
                        for (thumb in i.thumbnailData) {
                            val timeMilis = DateTimeUtils.parseTimeInMillis(
                                AppConstant.DateTime.DATE_TIME_FORMAT_ISO,
                                thumb.startTime
                            )
                            if (timeMilis == DateTimeUtils.getCurrentTimeInMillis()) {
                                featuredShows.thumbnail = thumb.thumbnail
                                break
                            }
                        }
                        break
                    }
                    showName.text = radioModel[0].title
                    showDisc.text = radioModel[0].description
                    mediaActionButton.setOnClickListener {
                        initPodcastViewer(featuredShows)
                    }
                }
            }
        })
    }
}