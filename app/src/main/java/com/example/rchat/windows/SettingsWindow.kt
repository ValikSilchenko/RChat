package com.example.rchat.windows

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.rchat.R
import com.example.rchat.utils.BackgroundService
import com.example.rchat.utils.ChatFunctions

class SettingsWindow : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.Theme_Dark)
            Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.Theme_Light)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_window)

        val backBtn: ImageButton = findViewById(R.id.SW_ToChatsBtn)
        val exitAccountBtn: Button = findViewById(R.id.SW_ExitAccountBtn)

        backBtn.setOnClickListener {
            startIntent()
        }

        exitAccountBtn.setOnClickListener {
            if (ChatFunctions().isServiceRunning(BackgroundService::class.java, applicationContext))
                stopService(Intent(applicationContext, BackgroundService::class.java))
            ChatFunctions().deleteData(this)
            val intent = Intent(this, AuthorizationWindow::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }

    @Override
    override fun onBackPressed() {
        startIntent()
    }

    private fun startIntent() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
}
