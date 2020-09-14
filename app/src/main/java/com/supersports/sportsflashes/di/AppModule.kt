package com.supersports.sportsflashes.di

import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.supersports.sportsflashes.common.application.SFApplication
import com.supersports.sportsflashes.common.utils.AppConstant
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

@Module
class AppModule(application: SFApplication) {
    private val application: Application

    init {
        this.application = application
    }

    @Singleton
    @Provides
    fun getAppContext(): Context {
        return application
    }

    @Provides
    @Singleton
    fun getGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    @Singleton
    fun getAppResources(): Resources {
        return getAppContext().resources
    }

    @Provides
    @Singleton
    fun getCompositeDispose(): CompositeDisposable {
        return CompositeDisposable()
    }

    @Singleton
    @Provides
    fun getMediaPlayer(): ExoPlayer {
        val renderersFactory = DefaultRenderersFactory(getAppContext())
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
        return ExoPlayerFactory.newSimpleInstance(
            getAppContext(),
            renderersFactory,
            trackSelector,
            loadControl,
            null,
            bandwidthMeter
        )
    }
}