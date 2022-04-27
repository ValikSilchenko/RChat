package com.example.rchat

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class AvailableContacts : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        when(resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK)
        {
            Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.Theme_Dark)
            Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.Theme_Light)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.available_contacts)
    }

    @Override
    override fun onBackPressed() {
        startActivity(Intent(this, ChatList::class.java))
    }
}