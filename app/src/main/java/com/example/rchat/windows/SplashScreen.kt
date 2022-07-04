package com.example.rchat.windows

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.rchat.R
import com.example.rchat.utils.BackgroundService
import com.example.rchat.utils.ChatFunctions

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val prefs = getSharedPreferences("Night Mode", Context.MODE_PRIVATE)
        when {
            prefs.getString("NightMode", "Day") == "Day" -> setTheme(R.style.Theme_Light)
            prefs.getString("NightMode", "Day") == "Night" -> setTheme(R.style.Theme_Dark)
            prefs.getString("NightMode", "Day") == "System" -> {
                when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.Theme_Dark)
                    Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.Theme_Light)
                }
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen_window)

        val handler = Handler()
        handler.postDelayed({
            if (ChatFunctions().isAuthorized(this)) {
                if (!ChatFunctions().isServiceRunning(BackgroundService::class.java, applicationContext))
                    startService(Intent(applicationContext, BackgroundService::class.java))
                newWindow(ChatsWindow::class.java)
            }
            else {
                newWindow(AuthorizationWindow::class.java)
            }
        }, 4000)
    }

    private fun newWindow(window: Class<*>) {
        val mIntent = Intent(this, window)
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(mIntent)
    }
}