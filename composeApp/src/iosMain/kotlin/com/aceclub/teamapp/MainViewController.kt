package com.aceclub.teamapp

import androidx.compose.ui.window.ComposeUIViewController
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

fun MainViewController() = ComposeUIViewController {
    App(
        onCall = { phone ->
            val sanitized = phone.filter { it.isDigit() || it == '+' }
            NSURL.URLWithString("tel:$sanitized")?.let { UIApplication.sharedApplication.openURL(it) }
        },
        onEmail = { email ->
            NSURL.URLWithString("mailto:$email")?.let { UIApplication.sharedApplication.openURL(it) }
        }
    )
}
