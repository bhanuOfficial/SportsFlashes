package com.sports.sportsflashes.di

import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.sports.sportsflashes.R
import dagger.Module
import dagger.Provides
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
class ValueModule {

    @Singleton
    @Provides
    fun getEndPoint(context: Context): HttpUrl {
        return HttpUrl.parse(context.resources.getString(R.string.baseUrl))!!
    }

    @Singleton
    @Provides
    fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient().newBuilder().addNetworkInterceptor(StethoInterceptor()).build()
    }

}