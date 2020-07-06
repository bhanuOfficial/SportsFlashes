package com.sports.sportsflashes.common.utils

import android.content.Context
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
}