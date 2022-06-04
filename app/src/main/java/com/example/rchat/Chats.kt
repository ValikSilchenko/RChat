package com.example.rchat

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Chats : AppCompatActivity() {

    private var previewChatLogins = mutableListOf<String>()
    private var previewChatReceivingTimes = mutableListOf<String>()
    private var previewChatMessages = mutableListOf<String>()
    private var previewChatIDs = mutableListOf<Int>()
    private var userID = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.Theme_Dark)
            Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.Theme_Light)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.chats)

        val newChatBtn: Button = findViewById(R.id.NewChat_Btn)
        val mainMenuBtn: Button = findViewById(R.id.MainMenu_Btn)
        val chatArray: RecyclerView = findViewById(R.id.Chat_Array)
        val chatItself: LinearLayout = findViewById(R.id.Chat_Itself)
        val chatsList:LinearLayout = findViewById(R.id.Chats_List)
        chatItself.isVisible = false

        postToList()
        chatArray.layoutManager = LinearLayoutManager(this)
        chatArray.adapter =
            PreviewChatRvAdapter(
                previewChatLogins,
                previewChatReceivingTimes,
                previewChatMessages,
                previewChatIDs,
                this,
                ChatItself::class.java
            )

        newChatBtn.setOnClickListener {
            //startIntent(AvailableContacts::class.java)
            chatsList.isVisible = false
            chatItself.isVisible = true
        }

        mainMenuBtn.setOnClickListener {
            chatItself.isVisible = false
            chatsList.isVisible = true
        }
    }

    @Override
    override fun onBackPressed() {
    }

    private fun startIntent(window: Class<*>?) {
        startActivity(Intent(this, window))
    }

    private fun addToList(
        previewLogin: String,
        previewReceivingTime: String,
        previewMessage: String,
        chatID: Int
    ) {
        previewChatLogins.add(previewLogin)
        previewChatReceivingTimes.add(previewReceivingTime)
        previewChatMessages.add(previewMessage)
        previewChatIDs.add(chatID)
    }

    // ДЕБАГ
    private fun postToList() {
        for (i in 1..25) {
            addToList(
                "Login #${i}",
                "19:57",
                "Some message text",
                (0..100).random()
            )
        }
    }

    public fun testFunc(context: Context){
        Toast.makeText(context, "Message was successfully sent", Toast.LENGTH_SHORT).show()
    }
}