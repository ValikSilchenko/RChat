package com.example.rchat

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SettingsWindow : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.Theme_Dark)
            Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.Theme_Light)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        val backBtn: Button = findViewById(R.id.SettingsChat_Btn)

        backBtn.setOnClickListener {
            startActivity(Intent(this, ChatsWindow::class.java))
        }
    }

    @Override
    override fun onBackPressed() {
        startActivity(Intent(this, ChatsWindow::class.java))
    }
}