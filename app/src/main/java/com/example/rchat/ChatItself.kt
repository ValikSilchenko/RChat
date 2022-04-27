package com.example.rchat

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

class ChatItself : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        when(resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK)
        {
            Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.Theme_Dark)
            Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.Theme_Light)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_itself)

        val userName: TextView= findViewById(R.id.UserName_ChatText)
        val sendBtn: Button = findViewById(R.id.Send_Btn)
        val messageInput: EditText = findViewById(R.id.Message_Input)
        val mainMenuBtn: Button = findViewById(R.id.MainMenu_Btn)
        val messages: ListView = findViewById(R.id.Messages_List)

        // ДЕБАГОВОЕ ЗАПОЛЕНИЕ СПИСКА, ПОТОМ УДАЛИТЬ
        val names: Array<String> = arrayOf("Вася", "Петя")
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names)
        messages.adapter = adapter

        // Нажатие кнопки отправки сообщения
        sendBtn.setOnClickListener{
            if (messageInput.text.toString() != "")
            {
                userName.text = messageInput.text.toString()
                messageInput.text = null
            }
        }

        // Возврат в главное меню
        mainMenuBtn.setOnClickListener {
            backToChatList()
        }
    }

    // Возврат в меню чатов
    @Override
    override fun onBackPressed() {
        backToChatList()
    }

    private fun backToChatList()
    {
        startActivity(Intent(this, ChatList::class.java))
    }
}