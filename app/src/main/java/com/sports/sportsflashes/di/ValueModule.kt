package com.sports.sportsflashes.di

import android.content.Context
import com.sports.sportsflashes.R
import dagger.Module
import dagger.Provides
import okhttp3.HttpUrl
import javax.inject.Singleton

@Module
class ValueModule {

    @Singleton
    @Provides
    fun getEndPoint(context: Context): HttpUrl {
        return HttpUrl.parse(context.resources.getString(R.string.baseUrl))!!
    }

}