package com.sports.sportsflashes.common.applicationlevel

import android.app.Application
import com.sports.sportsflashes.di.AppComponent
import com.sports.sportsflashes.di.AppModule
import com.sports.sportsflashes.di.DaggerAppComponent

class SFApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createAppComponent()
    }

    companion object {
        private lateinit var appComponent: AppComponent
        fun getAppComponent(): AppComponent {
            return appComponent
        }
    }

    private fun createAppComponent() {
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

}