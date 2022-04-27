package com.example.rchat

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ChatList : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        when(resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK)
        {
            Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.Theme_Dark)
            Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.Theme_Light)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_list)

        val newChatBtn: Button = findViewById(R.id.NewChat_Btn)
        val settingsBtn: Button = findViewById(R.id.Settings_Btn)

        // Переход в чат (ДЕБАГ)
        newChatBtn.setOnClickListener {
            startIntent(ChatItself::class.java)
        }

        settingsBtn.setOnClickListener {
            startIntent(Settings::class.java)
        }
    }

    @Override
    override fun onBackPressed() {
    }

    private fun startIntent(Window: Class<*>?)
    {
        startActivity(Intent(this, Window))
    }
}