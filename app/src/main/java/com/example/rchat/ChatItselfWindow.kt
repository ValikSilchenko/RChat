package com.example.rchat

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.rchat.utils.ChatSingleton

class ChatItselfWindow : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_itself)

        val backToMainMenuBtn: Button = findViewById(R.id.MainMenu_Btn)
        val sendMessageBtn: Button = findViewById(R.id.SendMessage_Btn)
        val chatName: TextView = findViewById(R.id.UserName_ChatText)
        val messagesRecV: RecyclerView = findViewById(R.id.Messages_List)
        val messageInput: TextView = findViewById(R.id.Message_Input)

        ChatSingleton.clearLists(messagesRecV)
        ChatSingleton.setChatItselfWindow(
            messagesRecV,
            intent.getStringExtra("Chat Name").toString(),
            this
        )
        chatName.text = intent.getStringExtra("Chat Name")

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
        super.onBackPressed()
    }
}