package com.supersports.sportsflashes.common.application

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.facebook.stetho.Stetho
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.supersports.sportsflashes.R
import com.supersports.sportsflashes.common.utils.AppConstant
import com.supersports.sportsflashes.di.AppComponent
import com.supersports.sportsflashes.di.AppModule
import com.supersports.sportsflashes.di.DaggerAppComponent


class SFApplication : Application() {
    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreate() {
        super.onCreate()
        createAppComponent()
        initFirebaseRegistration()
        subscribeStetho()
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

    private fun initFirebaseRegistration() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("BHANU", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
                val msg = getString(R.string.msg_token_fmt, token)
                Log.d("Bhanu", msg)
                preferences =
                    this.getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE)
                editor = preferences.edit()
                editor.putString(AppConstant.FIREBASE_INSTANCE, token)
                editor.apply()
                editor.commit()
            })
    }

    private fun subscribeStetho() {
        Stetho.initializeWithDefaults(this)
    }

}