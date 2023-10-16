/*
 * Created by nphau on 11/19/22, 4:16 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 11/19/22, 3:58 PM
 */

package com.nphausg.app.embeddedserver.activities

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.telephony.TelephonyManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import com.nphausg.app.embeddedserver.R
import com.nphausg.app.embeddedserver.data.models.UssdCodeModel
import com.nphausg.app.embeddedserver.extensions.animateFlash
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private const val PERMISSION_REQUEST_CODE = 102


class MainActivity : AppCompatActivity() {
    private val telephonyManager by lazy { getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager }

    private val hasPermissions: Boolean
        get() = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED

    private lateinit var handler: Handler

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
                    kotlin.runCatching {
                        val ussdCode = call.receive<UssdCodeModel>()

                        //String ussdCode = "*123#";
                        val intent = Intent(Intent.ACTION_CALL)
                        intent.data =  Uri.parse("tel:*185#") // ussdCode.code.ussdToCallableUri()
                        startActivity(intent)

                        println(ussdCode)
                        val callback = object : TelephonyManager.UssdResponseCallback() {
                            override fun onReceiveUssdResponse(
                                telephonyManager: TelephonyManager?,
                                request: String?,
                                response: CharSequence?
                            ) {
                                super.onReceiveUssdResponse(telephonyManager, request, response)
                                runBlocking(coroutineContext) {
                                    call.respondText(response.toString())
                                }
                            }

                            override fun onReceiveUssdResponseFailed(
                                telephonyManager: TelephonyManager?,
                                request: String?,
                                failureCode: Int
                            ) {
                                super.onReceiveUssdResponseFailed(
                                    telephonyManager,
                                    request,
                                    failureCode
                                )
                                runBlocking {
                                    call.respondText("USSD ($request) Failed with code $failureCode ")
                                }
                            }
                        }

                        if (hasPermissions) {
                            dialUssdCode(ussdCode.code, callback)
                            delay(120000)
                        } else {
                            call.respondText("No Permission Granted")
                        }
                    }.onFailure {
                        call.respondText("${it.localizedMessage} ${it.stackTrace}")
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<AppCompatImageView>(R.id.image_logo).animateFlash()
        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                Log.e("Message", msg.data.toString())
            }
        }

        if (!hasPermissions) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE),
                PERMISSION_REQUEST_CODE
            )
        }

        // Start server
        CoroutineScope(Dispatchers.IO).launch {
            server.start(wait = true)
        }
    }

    override fun onDestroy() {
        server.stop(1_000, 2_000)
        super.onDestroy()
    }


    private fun dialUssdCode(ussdCode: String, callback: TelephonyManager.UssdResponseCallback) {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        telephonyManager.sendUssdRequest(ussdCode, callback, handler)
    }

}

private fun String.ussdToCallableUri(): Uri? {
    var uriString: String? = this
    if (!this.startsWith("tel:"))  uriString = "tel:$uriString"
    for (c in this.toCharArray()) {
        if (c == '#') uriString += Uri.encode("#") else uriString += c
    }
    return Uri.parse(uriString)
}