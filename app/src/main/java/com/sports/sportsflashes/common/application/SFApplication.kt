package com.sports.sportsflashes.common.application

import android.app.Application
import com.sports.sportsflashes.R
import com.sports.sportsflashes.di.AppComponent
import com.sports.sportsflashes.di.AppModule
import com.sports.sportsflashes.di.DaggerAppComponent
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump


class SFApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createAppComponent()
//        initCalligraphy()

    }

    companion object {
        private lateinit var appComponent: AppComponent
        fun getAppComponent(): AppComponent {
            return appComponent
        }
    }

    private fun initCalligraphy() {
        ViewPump.init(
            ViewPump.builder().addInterceptor(
                CalligraphyInterceptor(
                    CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/medium.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
                )
            ).build()
        )
    }

    private fun createAppComponent() {
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

}