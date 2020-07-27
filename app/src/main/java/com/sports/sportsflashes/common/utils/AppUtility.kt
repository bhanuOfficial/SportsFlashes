package com.sports.sportsflashes.common.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast


/**
 *Created by Bhanu on 06-07-2020
 */
object AppUtility {

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun clearCache(context: Context) {
        context.cacheDir.deleteRecursively()
    }

    fun shareAppContent(context: Context, message: String) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "SportsFlashes")
        sharingIntent.putExtra(Intent.EXTRA_TEXT, message)
        context.startActivity(Intent.createChooser(sharingIntent, "Share via"))
    }
}