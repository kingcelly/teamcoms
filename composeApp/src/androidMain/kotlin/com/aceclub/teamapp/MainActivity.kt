package com.aceclub.teamapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App(
                onCall = { phone ->
                    startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
                },
                onEmail = { email ->
                    startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email")))
                }
            )
        }
    }
}
