package com.supersports.sportsflashes.common.utils

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.supersports.sportsflashes.R

/**
 *Created by Bhanu on 08-07-2020
 */
object AlertDialogUtility {
    fun appDialog(view: View, context: Context): Dialog {
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

    fun reminderAppDialog(
        view: Int,
        context: Context,
        titleValue: String,
        messageValue: String,
        subscribe: Boolean,
        yesRunnable: Runnable?
    ): Dialog {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(view)

        val yesBtn = dialog.findViewById(R.id.buttonYes) as Button
        val title = dialog.findViewById(R.id.title) as TextView
        val message = dialog.findViewById(R.id.message) as TextView
        val noBtn = dialog.findViewById(R.id.buttonNo) as Button
        val okBtn = dialog.findViewById(R.id.buttonOk) as Button
        if (!subscribe) {
            okBtn.visibility = View.VISIBLE
            yesBtn.visibility = View.GONE
            noBtn.visibility = View.GONE
        } else {
            okBtn.visibility = View.GONE
            yesBtn.visibility = View.VISIBLE
            noBtn.visibility = View.VISIBLE
        }
        title.text = titleValue
        message.text =
            messageValue
        yesBtn.setOnClickListener {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
            yesRunnable?.run()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        okBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
        dialog.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        return dialog
    }
}