/*
 * Created by nphau on 11/19/22, 4:16 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 11/19/22, 3:58 PM
 */

package com.nphausg.app.embeddedserver.activities

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.nphausg.app.embeddedserver.R
import com.nphausg.app.embeddedserver.data.models.UssdCodeModel
import com.nphausg.app.embeddedserver.extensions.animateFlash
import com.romellfudi.ussdlibrary.callPhoneNumber
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.request.receive
import io.ktor.server.response.respondText
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


private const val PERMISSION_REQUEST_CODE = 102


class MainActivity : AppCompatActivity() {
    private val logs = MutableLiveData<List<LogMessage>>(emptyList())

    private val hasPermissions: Boolean
        get() = listOf(
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            ACCESS_FINE_LOCATION
        )
            .all {
                ActivityCompat.checkSelfPermission(
                    this,
                    it,
                ) == PackageManager.PERMISSION_GRANTED
            }


    companion object {
        private const val PORT = 6868
    }

    private val server by lazy {
        embeddedServer(Netty, PORT, watchPaths = emptyList()) {
            // configures Cross-Origin Resource Sharing. CORS is needed to make calls from arbitrary
            // JavaScript clients, and helps us prevent issues down the line.
            install(CORS) {
                anyHost()
            }
            install(ContentNegotiation) {
                json()
            }
            routing {
                post("/ussd") {
                    runCatching {
                        val ussdCodeModel = call.receive<UssdCodeModel>()

                        val itemLogs = logs.value.orEmpty().toMutableList()
                            .apply { add(LogMessage.Normal("POST Request: Code: ${ussdCodeModel.code}")) }

                        logs.postValue(itemLogs)
                        callPhoneNumber(this@MainActivity, ussdCodeModel.code)
                    }
                        .onSuccess { message ->
                            val logMessage =
                                "POST Response: ${message.split("\n").joinToString(" ")}"
                            val itemLogs = logs.value.orEmpty().toMutableList()
                                .apply {
                                    add(LogMessage.Success(logMessage))
                                }
                            logs.postValue(itemLogs)
                            call.respondText(message)
                        }
                        .onFailure { error ->
                            val itemLogs = logs.value.orEmpty().toMutableList()
                                .apply {
                                    add(LogMessage.Error(error.localizedMessage ?: ""))
                                }
                            logs.postValue(itemLogs)
                            call.respondText(error.localizedMessage)
                        }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<AppCompatImageView>(R.id.image_logo).animateFlash()

        if (!hasPermissions) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    ACCESS_FINE_LOCATION,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                ),
                PERMISSION_REQUEST_CODE
            )
        }

        // Start server
        CoroutineScope(Dispatchers.IO).launch {
            server.start(wait = true)
        }

        val adapter = LogAdapter()
        findViewById<TextView>(R.id.text_status).text =
            "Server Running: ${getIPAddress()}:$PORT\nMake Post Request to ${getIPAddress()}:$PORT/ussd\n Body: {\n" +
                    "    \"code\": \"*100#\"\n" +
                    "}"
        findViewById<RecyclerView>(R.id.logs_list).adapter = adapter

        logs.observe(this) { itemLogs ->
            adapter.submitLogs(itemLogs)
        }
    }

    override fun onDestroy() {
        server.stop(1_000, 2_000)
        super.onDestroy()
    }

    private fun getIPAddress(): String {
        val wifiInf = (getSystemService(Context.WIFI_SERVICE) as WifiManager).connectionInfo
        val ipAddress = wifiInf.ipAddress
        return String.format(
            "%d.%d.%d.%d",
            ipAddress and 0xff,
            ipAddress shr 8 and 0xff,
            ipAddress shr 16 and 0xff,
            ipAddress shr 24 and 0xff
        )
    }

}