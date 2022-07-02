package com.example.rchat.windows

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.rchat.R
import com.example.rchat.utils.ChatSingleton

class CreateGroupChatWindow : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_gc_window)

        val backBtn: ImageButton = findViewById(R.id.CGC_BackBtn)
        val createBtn: Button = findViewById(R.id.CGC_CreateChatBtn)
        val usersArray: ListView = findViewById(R.id.CGC_UsersArray)
        val groupName: EditText = findViewById(R.id.CGC_GroupName)

        ChatSingleton.setCGCWindow(this, usersArray)

        createBtn.setOnClickListener {

        }

        backBtn.setOnClickListener {
            val intent = Intent(this, ChatsWindow::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }
    }
}