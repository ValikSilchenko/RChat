package com.example.rchat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class ChatItself : AppCompatActivity() {

    private var incomingLoginsList = mutableListOf<String>()
    private var incomingMessagesTexts = mutableListOf<String>()
    private var outgoingLoginsList = mutableListOf<String>()
    private var outgoingMessagesText = mutableListOf<String>()
    private var messagesRecView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_itself)


    }

}