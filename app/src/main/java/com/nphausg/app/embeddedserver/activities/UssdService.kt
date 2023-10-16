/*
 * Created by simonpojok on 17/09/2023, 22:59
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 17/09/2023, 22:59
 */

package com.nphausg.app.embeddedserver.activities

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast

class UssdService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.d(TAG, "onAccessibilityEvent")
        try {
            //Get the source
            val nodeInfo = event.source
            val text = event.text.toString()
            if (event.className == "android.app.AlertDialog") {
                Log.d(TAG, text)
                Toast.makeText(this, text, Toast.LENGTH_LONG).show()
                val nodeInput = nodeInfo!!.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
                val bundle = Bundle()
                bundle.putCharSequence(
                    AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                    "0"
                )
                nodeInput.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle)
                nodeInput.refresh()
                val list = nodeInfo.findAccessibilityNodeInfosByText("Send")
                for (node in list) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onInterrupt() {}
    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "onServiceConnected")
        try {
            val info = AccessibilityServiceInfo()
            info.flags = AccessibilityServiceInfo.DEFAULT
            info.packageNames = arrayOf("com.android.phone")
            info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            serviceInfo = info
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        var TAG = "USSD"
    }
}
