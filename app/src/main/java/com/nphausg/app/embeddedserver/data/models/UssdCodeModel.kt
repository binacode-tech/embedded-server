/*
 * Created by simonpojok on 17/09/2023, 20:52
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 17/09/2023, 20:52
 */

package com.nphausg.app.embeddedserver.data.models

import kotlinx.serialization.Serializable

@Serializable
data class UssdCodeModel(
    val code: String,
)