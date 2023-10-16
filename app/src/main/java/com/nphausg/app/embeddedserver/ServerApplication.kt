/*
 * Created by simonpojok on 16/10/2023, 12:43
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 16/10/2023, 12:43
 */

package com.nphausg.app.embeddedserver

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ServerApplication : Application() {
    private val liveData: MutableLiveData<USSDCommand> = MutableLiveData(USSDCommand.None)
    val command: LiveData<USSDCommand> = liveData

    fun onResponse(response: USSDCommand.Response) {
        liveData.postValue(response)
    }

    fun onCommand(command: USSDCommand.Call) {
        liveData.postValue(command)
    }

}