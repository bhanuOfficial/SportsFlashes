package com.sports.sportsflashes.common.utils

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.widget.Button
import com.sports.sportsflashes.R

/**
 *Created by Bhanu on 08-07-2020
 */
object AlertDialogUtility {
    fun appDialog(view: View, context: Context): Dialog{
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        val yesBtn = dialog.findViewById(R.id.buttonYes) as Button
        val noBtn = dialog.findViewById(R.id.buttonNo) as Button
        val okBtn = dialog.findViewById(R.id.buttonOk) as Button
        yesBtn.setOnClickListener {
            dialog.dismiss()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
        return dialog
    }
}