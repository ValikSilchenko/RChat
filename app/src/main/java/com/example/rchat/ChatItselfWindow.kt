package com.example.rchat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.rchat.utils.ChatSingleton
import org.json.JSONObject

class ChatItselfWindow : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_itself)

        val backToMainMenuBtn: Button = findViewById(R.id.MainMenu_Btn)
        val sendMessageBtn: Button = findViewById(R.id.SendMessage_Btn)
        val chatName: TextView = findViewById(R.id.UserName_ChatText)
        val messagesListView: ListView = findViewById(R.id.Messages_List)
        val messageInput: TextView = findViewById(R.id.Message_Input)
        var response: List<JSONObject>

        ChatSingleton.setChatItselfWindow(
            messagesListView,
            intent.getStringExtra("Chat Name").toString(),
            this
        )
        ChatSingleton.clearMessageList()
        chatName.text = intent.getStringExtra("Chat Name")

        // Вывод сообщений на экран
        ChatSingleton.sendMessagesRequest()
        // Вывод сообщений на экран
//        try {
//            response = JasonSTATHAM().zapretParsinga(
//                Requests().get(
//                    mapOf(
//                        "sender" to ChatSingleton.getLogin(),
//                        "recipient" to chatName.text.toString()
//                    ),
//                    "http://192.168.1.107:8080/personal"
//                )
//            )
//
//            for (el in response)
//                ChatSingleton.updateMessageList(
//                    (el["sender"] as JSONObject)["username"].toString(),
//                    el["messageText"].toString()
//                )
//
//        } catch (exception: Exception) {
//            ChatFunctions().showMessage(
//                "Ошибка",
//                "Ошибка отправки данных: ${exception.message}",
//                this
//            )
//        }
        backToMainMenuBtn.setOnClickListener {
            startIntent()
        }

        sendMessageBtn.setOnClickListener {
            if (messageInput.text.isNotEmpty()) {
                ChatSingleton.sendMessage(
                    intent.getStringExtra("Chat Name").toString(),
                    messageInput.text.toString()
                )
                messageInput.text = null
            }
        }
    }

    @Override
    override fun onBackPressed() {
        startIntent()
    }

    fun startIntent() {
        val intent = Intent(this, ChatsWindow::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
    }
}