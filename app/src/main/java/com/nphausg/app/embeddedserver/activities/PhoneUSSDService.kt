/*
 * Created by simonpojok on 16/10/2023, 11:41
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 16/10/2023, 11:41
 */
package com.nphausg.app.embeddedserver.activities

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.nphausg.app.embeddedserver.ServerApplication
import com.nphausg.app.embeddedserver.USSDCommand

class PhoneUSSDService : AccessibilityService() {
    override fun onAccessibilityEvent(accessibilityEvent: AccessibilityEvent) {
        Log.d(TAG, "onAccessibilityEvent")
        val text = accessibilityEvent.text.toString()
        if (accessibilityEvent.className == "android.app.AlertDialog") {
            performGlobalAction(GLOBAL_ACTION_BACK)
            val ussdIntent = Intent("com.times.ussd.action.REFRESH")
            ussdIntent.putExtra("message", text)
            sendBroadcast(ussdIntent)
            (applicationContext as ServerApplication).onResponse(USSDCommand.Response(text))
        }

        Thread.sleep(2000)
        val commandResponse = (applicationContext as ServerApplication).command.value
        if (commandResponse is USSDCommand.Call) {

        }

    }

    override fun onInterrupt() {}
    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "onServiceConnected")
        val info = AccessibilityServiceInfo()
        info.flags = AccessibilityServiceInfo.DEFAULT
        info.packageNames = arrayOf("com.android.phone")
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        serviceInfo = info
    }

    companion object {
        var TAG = "Accessibility Service"
    }
}