package com.sports.sportsflashes.di

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
import com.sports.sportsflashes.common.application.SFApplication
import com.sports.sportsflashes.common.utils.AppConstant
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
}