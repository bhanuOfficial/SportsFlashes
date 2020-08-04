package com.sports.sportsflashes.common.application

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sports.sportsflashes.R
import com.sports.sportsflashes.common.utils.AppConstant

/**
 *Created by Bhanu on 02-08-2020
 */
class AppFirebaseMessagingService : FirebaseMessagingService() {
    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("BHANU", "From: $token")
        preferences =
            this.getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE)
        editor = preferences.edit()
        editor.putString(AppConstant.FIREBASE_INSTANCE, token)
        editor.apply()
        editor.commit()

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("BHANU", "From: ${remoteMessage?.from}")
        // Check if message contains a notification payload.
        Log.d("BHANU", "Message Notification Body: ${remoteMessage.data}")
        //Message Services handle notification
        val notification = NotificationCompat.Builder(this)
            .setContentTitle(remoteMessage.from)
            .setContentText(remoteMessage.messageId)
            .setChannelId("Sports-Flashes-123123")
            .setSmallIcon(R.drawable.menu_icon)
            .build()
        val manager = NotificationManagerCompat.from(applicationContext)
        manager.notify(/*notification id*/0, notification)
        remoteMessage.notification?.let {
        }

    }
}