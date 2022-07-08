package com.example.rchat.windows

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.rchat.R
import com.example.rchat.utils.ChatSingleton

class MediaChatWindow : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val prefs = getSharedPreferences("Night Mode", Context.MODE_PRIVATE)
        when (prefs.getString("NightMode", "Day")) {
            "Day" -> setTheme(R.style.Theme_Light)
            "Night" -> setTheme(R.style.Theme_Dark)
            "System" -> {
                when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.Theme_Dark)
                    Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.Theme_Light)
                }
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.media_chat_window)

        val backBtn: ImageButton = findViewById(R.id.MCW_BackBtn)
        val testTxt: TextView = findViewById(R.id.MCW_ChatNameTV)

        testTxt.text = ChatSingleton.chatName

        backBtn.setOnClickListener {
            super.onBackPressed()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }

    @Override
    override fun onBackPressed() {
        super.onBackPressed()
    }
}