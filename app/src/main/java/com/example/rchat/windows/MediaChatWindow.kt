package com.example.rchat.windows

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.rchat.R
import com.example.rchat.utils.ChatFunctions
import com.example.rchat.utils.ChatSingleton

/* Оконный класс медиавложений в чате
*/
class MediaChatWindow : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        /* Установка темы приложения
        */
        ChatFunctions().setAppTheme(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.media_chat_window)

        val backBtn: ImageButton = findViewById(R.id.MCW_BackBtn)
        val testTxt: TextView = findViewById(R.id.MCW_ChatNameTV)

        testTxt.text = ChatSingleton.chatName

        /* Нажатие кнопки возврата
        */
        backBtn.setOnClickListener {
            super.onBackPressed()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }

    @Override
    override fun onBackPressed() {
        super.onBackPressed()
    }
}