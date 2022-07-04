package com.example.rchat.windows

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.rchat.R
import com.example.rchat.utils.ChatSingleton

class CreateGroupChatWindow : AppCompatActivity() {
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
        setContentView(R.layout.create_gc_window)

        val backBtn: ImageButton = findViewById(R.id.CGC_BackBtn)
        val createBtn: Button = findViewById(R.id.CGC_CreateChatBtn)
        val usersArray: ListView = findViewById(R.id.CGC_UsersArray)
        val groupName: EditText = findViewById(R.id.CGC_GroupName)

        ChatSingleton.setCGCWindow(this, usersArray)

        createBtn.setOnClickListener {

        }

        backBtn.setOnClickListener {
            val intent = Intent(this, ChatsWindow::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }
    }
}