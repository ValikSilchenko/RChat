package com.example.rchat.windows

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.rchat.R
import com.example.rchat.utils.ChatSingleton
import com.example.rchat.utils.JasonSTATHAM
import com.example.rchat.utils.Requests
import org.json.JSONObject

class ChatItselfWindow : AppCompatActivity() {

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
        setContentView(R.layout.chat_itself_window)

        val backToMainMenuBtn: ImageButton = findViewById(R.id.CIW_BackBtn)
        val sendMessageBtn: ImageButton = findViewById(R.id.CIW_SendMessageBtn)
        val attachBtn: ImageButton = findViewById(R.id.CIW_AttachBtn)
        val actionsBtn: ImageButton = findViewById(R.id.CIW_ActionsBtn)
        val chatName: TextView = findViewById(R.id.CIW_ChatName)
        val messagesListView: ListView = findViewById(R.id.CIW_MessagesArray)
        val messageInput: EditText = findViewById(R.id.CIW_MessageInput)

        val chatLogin = ChatSingleton.chatName
        chatName.text = chatLogin

        ChatSingleton.setChatItselfWindow(
            messagesListView,
            chatLogin,
            this,
            messageInput
        )

        // Receiving messages
        val response: List<JSONObject> = JasonSTATHAM().stringToJSONObj(
            Requests().get(
                mapOf(
                    "sender" to ChatSingleton.Van,
                    "recipient" to ChatSingleton.Billy
                ),
                "${ChatSingleton.serverUrl}/personal"
            )
        )
        for (el in response)
            ChatSingleton.updateMessageList(
                (el["sender"] as JSONObject)["username"].toString(),
                el["messageText"].toString(),
                "${el["date"]} ${el["time"]}"
            )
        // End of receiving messages

        backToMainMenuBtn.setOnClickListener {
            ChatSingleton.clearMessagesList()
            startIntent()
        }

        actionsBtn.setOnClickListener {
            val mIntent = Intent(this, MediaChatWindow::class.java)
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(mIntent)
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        attachBtn.setOnClickListener {
            Toast.makeText(applicationContext, "В разработке...", Toast.LENGTH_SHORT).show()
        }

        sendMessageBtn.setOnClickListener {
            if (messageInput.text.isNotEmpty())
                ChatSingleton.sendMessage(
                    chatLogin,
                    messageInput.text.toString(),
                    messageInput
                )
        }
    }

    @Override
    override fun onBackPressed() {
        startIntent()
    }

    private fun startIntent() {
        ChatSingleton.isInChat = false
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        finish()
    }
}