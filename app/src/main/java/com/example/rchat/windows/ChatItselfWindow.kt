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

        val idArray: ArrayList<Int> = ArrayList()
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
        setContentView(R.layout.chat_itself_window)

        val backToMainMenuBtn: ImageButton = findViewById(R.id.CIW_BackBtn)
        val sendMessageBtn: ImageButton = findViewById(R.id.CIW_SendMessageBtn)
        val attachBtn: ImageButton = findViewById(R.id.CIW_AttachBtn)
        val chatNameTV: TextView = findViewById(R.id.CIW_ChatNameTV)
        val messagesLV: ListView = findViewById(R.id.CIW_MessagesRV)
        val messageInputET: EditText = findViewById(R.id.CIW_MessageInputET)

        val chatLogin = ChatSingleton.chatName
        chatNameTV.text = chatLogin

        ChatSingleton.setChatItselfWindow(
            messagesLV,
            chatLogin,
            this,
            messageInputET
        )

        var unreadCount = Requests().get(
            mapOf("sender" to chatLogin, "recipient" to ChatSingleton.Van),
            "${ChatSingleton.serverUrl}/count"
        ).toInt()
        val response: List<JSONObject> = JasonSTATHAM().stringToListOfJSONObj(
            Requests().get(
                mapOf(
                    "sender" to ChatSingleton.Van,
                    "recipient" to ChatSingleton.Billy
                ),
                "${ChatSingleton.serverUrl}/personal"
            )
        )
        for (el in response) {
            if (!(el["read"] as Boolean))
                idArray.add(el["id"] as Int)
            ChatSingleton.updateMessageList(
                (el["sender"] as JSONObject)["username"].toString(),
                el["messageText"].toString(),
                "${el["date"]} ${el["time"]}",
                el["id"] as Int
            )
        }
        ChatSingleton.focusOnLastItem(unreadCount)

        // Отправка запроса на прочтение сообщений
        idArray.forEach {
            ChatSingleton.sendRequestForReading(chatLogin, it)
        }

        backToMainMenuBtn.setOnClickListener {
            ChatSingleton.clearMessagesList()
            startIntent()
        }

        chatNameTV.setOnClickListener {
            val mIntent = Intent(this, MediaChatWindow::class.java)
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(mIntent)
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        attachBtn.setOnClickListener {
            Toast.makeText(applicationContext, "В разработке...", Toast.LENGTH_SHORT).show()
        }

        sendMessageBtn.setOnClickListener {
            if (messageInputET.text.isNotEmpty())
                ChatSingleton.sendMessage(
                    chatLogin,
                    messageInputET.text.toString(),
                    messageInputET
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