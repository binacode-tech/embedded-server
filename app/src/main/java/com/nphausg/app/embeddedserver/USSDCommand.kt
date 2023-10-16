/*
 * Created by simonpojok on 16/10/2023, 12:46
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 16/10/2023, 12:46
 */

package com.nphausg.app.embeddedserver

sealed interface USSDCommand {
    data class Call(val message: String) : USSDCommand
    data class Response(val message: String) : USSDCommand
    object None : USSDCommand
}