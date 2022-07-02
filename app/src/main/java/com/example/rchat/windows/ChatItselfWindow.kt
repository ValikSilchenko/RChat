package com.example.rchat.windows

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.rchat.R
import com.example.rchat.utils.ChatSingleton
import com.example.rchat.utils.JasonSTATHAM
import com.example.rchat.utils.Requests
import org.json.JSONObject

class ChatItselfWindow : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_itself_window)

        val backToMainMenuBtn: ImageButton = findViewById(R.id.CIW_BackBtn)
        val sendMessageBtn: ImageButton = findViewById(R.id.CIW_SendMessageBtn)
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
        if (ChatSingleton.messagesArrayList.isNotEmpty())
            ChatSingleton.messagesArrayList.clear()
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
            startIntent()
        }

        sendMessageBtn.setOnClickListener {
            if (messageInput.text.isNotEmpty()) {
                ChatSingleton.sendMessage(
                    chatLogin,
                    messageInput.text.toString()
                )
                messageInput.text = null
                ChatSingleton.setSelection()
            }
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
    }
}