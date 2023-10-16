<h1 align="center"> :robot: Android Embedded Server </h1>

<div align="center">
    <img src="https://img.shields.io/badge/kotlin-v1.6.10-blue.svg">
    <img src="https://img.shields.io/badge/gradle-7.1.0-blueviolet.svg">
    <img src="https://img.shields.io/badge/API-21%2B-blue.svg?style=flat">
    <img src="https://img.shields.io/badge/License-Apache%202.0-success.svg">
    <img src="https://circleci.com/gh/twilio-labs/plugin-rtc.svg?style=svg">
    <a href="https://github.com/nphau/android.embeddedserver/actions/workflows/app-build.yml"><img alt="Build Status" src="https://github.com/nphau/android.embeddedserver/actions/workflows/app-build.yml/badge.svg"/></a>
</div>

![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/colored.png)

## 👇 Overview

A minimal way to create HTTP server in android with Kotlin. Create asynchronous client and server applications. Anything from microservices to multiplatform HTTP client apps in a simple way. Open Source, free, and fun!

```kotlin
embeddedServer(Netty, PORT, watchPaths = emptyList()) {
            install(WebSockets)
            install(CallLogging)
            routing {
                get("/") {
                    call.respondText(
                        text = "Hello!! You are here in ${Build.MODEL}",
                        contentType = ContentType.Text.Plain
                    )
                }
            }
        }
```
## 🚀 How to use

Cloning the repository into a local directory and checkout the desired branch:

```
git clone git@github.com:nphau/android.embeddedserver.git
cd android.embeddedserver
git checkout master
```
## 🍲 Screenshots

<h4 align="center">
<img src="docs/device.gif" width="20%" vspace="10" hspace="10">
<img src="docs/edge_get.gif" width="70%" vspace="10" hspace="10">

## ✨ Contributing
Please feel free to contact me or make a pull request.

## 👀 Author

``` Created by $username on $today
Copyright (c) $today.year . All rights reserved.
Last modified $file.lastModified
```
<p>
    <a href="https://nphausg.medium.com" target="_blank">
    <img src="https://avatars2.githubusercontent.com/u/13111806?s=400&u=f09b6160dbbe2b7eeae0aeb0ab4efac0caad57d7&v=4" width="96" height="96">
    </a>
    <a href="https://github.com/hieuwu" target="_blank">
    <img src="https://avatars.githubusercontent.com/u/43868345?v=4" width="96" height="96">
    </a>
</p>
