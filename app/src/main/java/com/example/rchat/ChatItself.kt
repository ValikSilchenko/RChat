package com.example.rchat

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatItself : AppCompatActivity() {

    private var incomingLoginsList = mutableListOf<String>()
    private var incomingMessagesTexts = mutableListOf<String>()
    private var outgoingLoginsList = mutableListOf<String>()
    private var outgoingMessagesText = mutableListOf<String>()
    private var messagesRecView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.Theme_Dark)
            Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.Theme_Light)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_itself)

        val userName: TextView = findViewById(R.id.UserName_ChatText)
        val sendBtn: Button = findViewById(R.id.Send_Btn)
        val messageInput: EditText = findViewById(R.id.Message_Input)
        val mainMenuBtn: Button = findViewById(R.id.MainMenu_Btn)
        messagesRecView = findViewById(R.id.Messages_List)

        val chatList = ChatList()
        userName.text = intent.getStringExtra("Chat Name")

        // Нажатие кнопки отправки сообщения (ДЕБАГ)
        sendBtn.setOnClickListener {
            if (messageInput.text.isNotEmpty() && messageInput.text.length <= 1000) {
                // Добавить отправку данных на сервер
                postToList("", "", "Yuriy", messageInput.text.toString())
                messagesRecView?.layoutManager = LinearLayoutManager(this)
                messagesRecView?.adapter = MessageItemRvAdapter(
                    incomingLoginsList,
                    incomingMessagesTexts,
                    outgoingLoginsList,
                    outgoingMessagesText
                )
                messageInput.text = null
            }
        }

        // Возврат в главное меню
        mainMenuBtn.setOnClickListener {
            super.onBackPressed()
        }
    }

    @Override
    override fun onBackPressed() {
        super.onBackPressed()
    }

    // Добавление данных в message_item
    private fun addToList(incomingLogin: String, incomingMessage: String, outgoingLogin: String, outgoingMessage: String) {
        incomingLoginsList.add(incomingLogin)
        incomingMessagesTexts.add(incomingMessage)
        outgoingLoginsList.add(outgoingLogin)
        outgoingMessagesText.add(outgoingMessage)
    }

    private fun postToList(incomingLogin: String, incomingMessage: String, outgoingLogin: String, outgoingMessage: String) {
        addToList(incomingLogin, incomingMessage, outgoingLogin, outgoingMessage)
        messagesRecView?.layoutManager = LinearLayoutManager(this)
        messagesRecView?.adapter = MessageItemRvAdapter(
            incomingLoginsList,
            incomingMessagesTexts,
            outgoingLoginsList,
            outgoingMessagesText
        )
    }
}