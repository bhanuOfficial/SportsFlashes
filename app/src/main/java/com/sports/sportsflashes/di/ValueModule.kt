package com.sports.sportsflashes.di

import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.sports.sportsflashes.R
import com.sports.sportsflashes.common.utils.AppConstant
import dagger.Module
import dagger.Provides
import okhttp3.*
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
    fun getOkHttpClient(context: Context): OkHttpClient {

        val clientBuilder = OkHttpClient.Builder()
        val headerAuthorizationInterceptor: Interceptor = Interceptor { chain ->
            var request: Request = chain.request()
            val headers: Headers =
                request.headers().newBuilder().add(
                    "Authorization", context.getSharedPreferences(
                        context.getString(R.string.pref_key),
                        Context.MODE_PRIVATE
                    ).getString(AppConstant.FIREBASE_INSTANCE, "")
                ).build()
            request = request.newBuilder().headers(headers).build()
            chain.proceed(request)
        }
        clientBuilder.addNetworkInterceptor(StethoInterceptor())
        clientBuilder.addInterceptor(headerAuthorizationInterceptor)
        return clientBuilder.build()
//        return OkHttpClient().newBuilder().addNetworkInterceptor(StethoInterceptor()).build()
    }

}