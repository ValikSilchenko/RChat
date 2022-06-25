package com.example.rchat

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.rchat.utils.ChatSingleton

class ChatItselfWindow : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_itself_window)

        val backToMainMenuBtn: ImageButton = findViewById(R.id.MainMenu_Btn)
        val sendMessageBtn: ImageButton = findViewById(R.id.SendMessage_Btn)
        val chatName: TextView = findViewById(R.id.UserName_ChatText)
        val messagesListView: ListView = findViewById(R.id.Messages_List)
        val messageInput: TextView = findViewById(R.id.Message_Input)

        val chatLogin = ChatSingleton.chatName
        chatName.text = chatLogin

        ChatSingleton.setChatItselfWindow(
            messagesListView,
            chatLogin,
            this
        )
        ChatSingleton.clearMessageList()

        // Вывод сообщений на экран
        ChatSingleton.sendMessagesRequest()

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
//        val intent = Intent(this, ChatsWindow::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
//        startActivity(intent)
    }
}