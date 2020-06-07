package com.sports.sportsflashes.view.activites

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bekawestberg.loopinglayout.library.LoopingLayoutManager
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
import com.sports.sportsflashes.R
import com.sports.sportsflashes.common.application.SFApplication
import com.sports.sportsflashes.model.FeaturedShows
import com.sports.sportsflashes.model.SportCategories
import com.sports.sportsflashes.repository.NetworkResponse
import com.sports.sportsflashes.repository.STATUS
import com.sports.sportsflashes.view.adapters.CategoryAdapter
import com.sports.sportsflashes.view.adapters.CircularShowAdapter
import com.sports.sportsflashes.view.adapters.ImageShowAdapter
import com.sports.sportsflashes.view.customviewimpl.CircularHorizontalMode
import com.sports.sportsflashes.viewmodel.MainActivityViewModel
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.menu_view.*
import kotlinx.android.synthetic.main.podcast_play_view.*
import java.util.*


class MainActivity : AppCompatActivity(), FeaturedShowsImpl {
    var smallItemWidth: Int = 0
    var mainItemWidth: Int = 0
    private var draggingView = -1
    private lateinit var viewModel: MainActivityViewModel
    private var featuredShowslist = listOf<FeaturedShows>()
    private lateinit var animation1: AlphaAnimation
    private lateinit var podcastPlayerView: BottomSheetBehavior<RelativeLayout>
    private lateinit var mediaPlayer: ExoPlayer
    private var mTimer = Timer()
    private var seekBarHandler = Handler()

    companion object {
        val TAG = MainActivity::class.java.simpleName
        var instance = MainActivity()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SFApplication.getAppComponent().inject(this)
        setContentView(R.layout.activity_main)
        initDashboard()
        initMenuOptions()
        initPodcastViewer()
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        setFeaturedShows()
        setCategories()
        setMenuShows()
//        https://pwdown.com/14448/Dj%20Waley%20Babu%20-%20Badshah.mp3
//        https://file-examples.com/wp-content/uploads/2017/11/file_example_MP3_5MG.mp3
        exoPlayerInit("https://file-examples.com/wp-content/uploads/2017/11/file_example_MP3_5MG.mp3")
    }

    interface ItemPosition {
        fun getItemPosition(): Int
    }

    private fun setFeaturedShows() {
        viewModel.getFeaturedShows().observe(this,
            Observer<NetworkResponse> { t ->
                if (t!!.status == STATUS.SUCCESS) {
                    featuredShowslist = t.data as List<FeaturedShows>
                    circularRecycler.adapter =
                        CircularShowAdapter(featuredShowslist, {
                            smallItemWidth = it
                        }, this@MainActivity, false)

                    menu_show_recyclerView.adapter =
                        CircularShowAdapter(featuredShowslist, {
                            smallItemWidth = it
                        }, this@MainActivity, true)

                    imageCategory.adapter =
                        ImageShowAdapter(featuredShowslist, {
                            mainItemWidth = it
                        }, this@MainActivity)

                    imageCategory.postDelayed(Runnable {
                        imageCategory.scrollToPosition(
                            circularRecycler.getChildAdapterPosition(
                                circularRecycler.findViewAtCenter()!!
                            )
                        )
                        setFeaturedDetail(
                            featuredShowslist[circularRecycler.getChildAdapterPosition(
                                circularRecycler.findViewAtCenter()!!
                            )]
                        )
                    }, 200)
                }
            })
    }

    private fun setCategories() {
        viewModel.getCategories().observe(this, Observer<NetworkResponse> {
            if (it.status == STATUS.SUCCESS) {
                val categories = it.data as ArrayList<SportCategories>
                categories.addAll(categories)
                categories.addAll(categories)
                categories.addAll(categories)
                categories.addAll(categories)
                categories.addAll(categories)
                Log.d(TAG, "getCategories: List ${categories.toString()}")
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

    private fun initDashboard() {
        circularRecycler.setHasFixedSize(true)
        circularRecycler.layoutManager = LoopingLayoutManager(this).also {
            it.reverseLayout = false
            it.orientation = LinearLayoutManager.HORIZONTAL
        }
        circularRecycler.mViewMode = CircularHorizontalMode()
        circularRecycler.mNeedCenterForce = true


        imageCategory.setHasFixedSize(true)
        imageCategory.layoutManager = LoopingLayoutManager(this).also {
            it.reverseLayout = false
            it.orientation = LinearLayoutManager.HORIZONTAL
        }
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(imageCategory)


        val scrollListner: RecyclerView.OnScrollListener =
            object : RecyclerView.OnScrollListener() {
                var state: Int = -1
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (draggingView == 1 && recyclerView == imageCategory) {
                        circularRecycler.scrollBy(dx / (mainItemWidth / smallItemWidth), 0)
                        if (state == RecyclerView.SCROLL_STATE_SETTLING) {
                            circularRecycler.smoothScrollToView(circularRecycler.findViewAtCenter()!!)
                            circularRecycler.smoothScrollToPosition(
                                (imageCategory.adapter as ImageShowAdapter).getItemPosition()
                            )
                        }
                    } else if (draggingView == 2 && recyclerView == circularRecycler) {
                        imageCategory.scrollBy(dx * (mainItemWidth / smallItemWidth), 0)
                        if (state == RecyclerView.SCROLL_STATE_SETTLING) {
                            circularRecycler.post {
                                imageCategory.smoothScrollToPosition(
                                    circularRecycler.getChildAdapterPosition(circularRecycler.findViewAtCenter()!!)
                                )
                            }
                        }
                    }
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (imageCategory == recyclerView && newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        draggingView = 1
                    } else if (circularRecycler == recyclerView && newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        draggingView = 2
                    }
                    this.state = newState
                }
            }
        imageCategory.addOnScrollListener(scrollListner)
        circularRecycler.addOnScrollListener(scrollListner)


        setSupportActionBar(toolbar)
        setAlphaForFeaturedChanged()

        menu_drawer.setOnClickListener {
            if (menu_item_.visibility == View.GONE) {
                menu_item_.visibility = View.VISIBLE
                val animSlideDown: Animation =
                    AnimationUtils.loadAnimation(applicationContext, R.anim.slide_down)
                menu_item_.startAnimation(animSlideDown)
            } else {
                val animSlideUp: Animation =
                    AnimationUtils.loadAnimation(applicationContext, R.anim.slide_up)
                menu_item_.startAnimation(animSlideUp)
                menu_item_.postDelayed({ menu_item_.visibility = View.GONE }, 200)
            }
        }
    }

    private fun setAlphaForFeaturedChanged() {
        animation1 = AlphaAnimation(0.1f, 1.0f)
        animation1.duration = 700
        animation1.fillAfter = true
        showTittle.startAnimation(animation1)
        showDescription.startAnimation(animation1)
    }

    private fun initMenuOptions() {
        category_recyclerView.setHasFixedSize(true)
        category_recyclerView.layoutManager = LinearLayoutManager(this).also {
            it.orientation = LinearLayoutManager.HORIZONTAL
            it.reverseLayout = false
        }
    }


    override fun setFeaturedDetail(featuredShows: FeaturedShows) {
        animation1.startNow()
        showTittle.text = featuredShows.title
        showDescription.text = featuredShows.description
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase!!))
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


    private fun exoPlayerInit(streamUrl: String) {
        val renderersFactory = DefaultRenderersFactory(applicationContext)
        val bandwidthMeter = DefaultBandwidthMeter()

        val trackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(trackSelectionFactory)
        val loadControl = DefaultLoadControl.Builder()
            .setAllocator(DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE))
            .setBufferDurationsMs(
                5 * 60 * 1000, // this is it!
                10 * 60 * 1000,
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
                    /*   val duration: Long = mediaPlayer.duration
                       val amountToUpdate = duration / 100
                       mTimer.schedule(object : TimerTask() {
                           override fun run() {
                               if (amountToUpdate * smallSeekBar.progress < duration) {
                                   var p: Float = smallSeekBar.progress
                                   p += 1
                                   smallSeekBar.progress = p.roundToInt().toFloat()
                                   largeSeekBar.progress = p.roundToInt().toFloat()
                               }
                           }
                       }, 1000, amountToUpdate)*/
                } else {
                    mTimer.cancel()
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
        val UpdateSongTime: Runnable = object : Runnable {
            override fun run() {
                val duration: Long = mediaPlayer.duration
                val amountToUpdate = duration / 100
                smallSeekBar.setMax(amountToUpdate.toFloat())
                val startTime = mediaPlayer.currentPosition
                smallSeekBar.progress = startTime.toFloat()
                seekBarHandler.postDelayed(this, 100)
            }
        }
    }
}


