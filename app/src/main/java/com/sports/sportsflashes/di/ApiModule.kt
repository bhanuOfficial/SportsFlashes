package com.sports.sportsflashes.di

import dagger.Module
import dagger.Provides
import okhttp3.HttpUrl
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class ApiModule {

    @Singleton
    @Provides
    fun getRetrofitProvide(
        baseUrl: HttpUrl,
        converterFactory: Converter.Factory,
        callAdapterFactory: CallAdapter.Factory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(callAdapterFactory)
            .build()
    }

    @Singleton
    @Provides
    fun getGsonConvertor(): Converter.Factory {
        return GsonConverterFactory.create()
    }

    @Singleton
    @Provides
    fun getCallAdaptorFactory(): CallAdapter.Factory {
        return RxJava2CallAdapterFactory.create()
    }


}