package com.example.rchat

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.rchat.utils.ChatFunctions
import com.example.rchat.utils.ChatSingleton
import com.example.rchat.utils.JasonSTATHAM
import com.example.rchat.utils.Requests
import org.json.JSONObject

class ChatItselfWindow : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_itself)

        val backToMainMenuBtn: Button = findViewById(R.id.MainMenu_Btn)
        val sendMessageBtn: Button = findViewById(R.id.SendMessage_Btn)
        val chatName: TextView = findViewById(R.id.UserName_ChatText)
        val messagesRecV: RecyclerView = findViewById(R.id.Messages_List)
        val messageInput: TextView = findViewById(R.id.Message_Input)
        var response: List<JSONObject>

        ChatSingleton.setChatItselfWindow(
            messagesRecV,
            intent.getStringExtra("Chat Name").toString(),
            this
        )
        ChatSingleton.clearLists(messagesRecV)
        chatName.text = intent.getStringExtra("Chat Name")

        // Вывод сообщений на экран
        try {
            response = JasonSTATHAM().zapretParsinga(
                Requests().get(
                    mapOf(
                        "sender" to ChatSingleton.getLogin(),
                        "recipient" to chatName.text.toString()
                    ),
                    "http://192.168.1.107:8080/personal"
                )
            )

            for (el in response)
                ChatSingleton.updateMessageList(
                    (el["sender"] as JSONObject)["username"].toString(),
                    el["messageText"].toString()
                )

        } catch (exception: Exception) {
            ChatFunctions().showMessage(
                "Ошибка",
                "Ошибка отправки данных: ${exception.message}",
                this
            )
        }

        backToMainMenuBtn.setOnClickListener {
            super.onBackPressed()
        }

        sendMessageBtn.setOnClickListener {
            if (messageInput.text.isNotEmpty()) {
                ChatSingleton.sendMessage(
                    intent.getStringExtra("Chat Name").toString(),
                    messageInput.text.toString()
                )
                focusLastItem(messagesRecV)
                messageInput.text = null
            }
        }
    }

    private fun focusLastItem(recView: RecyclerView) {
        if (ChatSingleton.incomingLoginsList.isNotEmpty())
            recView.smoothScrollToPosition(ChatSingleton.incomingLoginsList.size - 1)
        else if (ChatSingleton.outgoingLoginsList.isNotEmpty())
            recView.smoothScrollToPosition(ChatSingleton.outgoingLoginsList.size - 1)
    }

    @Override
    override fun onBackPressed() {
        //ChatSingleton.sendChatsRequest()
        super.onBackPressed()
    }
}