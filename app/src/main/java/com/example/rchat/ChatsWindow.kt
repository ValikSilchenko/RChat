package com.example.rchat

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatsWindow : AppCompatActivity() {

    // Переменные для списка чатов
    private var previewChatLogins = mutableListOf<String>()
    private var previewChatReceivingTimes = mutableListOf<String>()
    private var previewChatMessages = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.Theme_Dark)
            Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.Theme_Light)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.chats)

        val chatArray: RecyclerView = findViewById(R.id.Chat_Array)
        val newChatBtn: Button = findViewById(R.id.NewChat_Btn)
        val appName: TextView = findViewById(R.id.AppName)

        appName.text = "Nigger"
        ChatSingleton.saveData(appName.text.toString())

        postToRv()
        chatArray.layoutManager = LinearLayoutManager(this)
        chatArray.adapter =
            PreviewChatRvAdapter(previewChatLogins, previewChatReceivingTimes, previewChatMessages, this, ChatItself::class.java)

        newChatBtn.setOnClickListener {
            startActivity(Intent(this, FindUsers::class.java))
        }
    }

    @Override
    override fun onBackPressed() {
    }

    // DEBUG
    private fun postToRv() {
        for (i in 1..25) {
            Functions().addToList(
                previewChatLogins,
                previewChatReceivingTimes,
                previewChatMessages,
                "Login #$i",
                "10.21",
                "Preview message of index $i"
            )
        }
    }
}
