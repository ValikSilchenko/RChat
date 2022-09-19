package com.example.rchat.windows

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rchat.R
import com.example.rchat.adapters.CreateGroupChatRVAdapter
import com.example.rchat.utils.ChatFunctions
import com.example.rchat.utils.ChatSingleton

/* Оконный класс создания бесед - групповых чатов
*/
class CreateGroupChatWindow : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        /* Установка темы приложения
        */
        ChatFunctions().setAppTheme(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_gc_window)

        val backBtn: ImageButton = findViewById(R.id.CGC_BackBtn)
        val createBtn: Button = findViewById(R.id.CGC_CreateChatBtn)
        val usersArray: RecyclerView = findViewById(R.id.CGC_UsersRV)
        val groupName: EditText = findViewById(R.id.CGC_GroupNameET)

        val usersArrayList = ChatSingleton.chatsArrayList
        val recViewAdapter = CreateGroupChatRVAdapter(usersArrayList)
        usersArray.layoutManager = LinearLayoutManager(this)
        usersArray.adapter = recViewAdapter

        /* Нажатие кнопки возврата
        */
        backBtn.setOnClickListener {
            val intent = Intent(this, ChatsWindow::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
            finish()
        }

        /* Нажатие кнопки создания беседы
        */
        createBtn.setOnClickListener {
            Toast.makeText(this, getString(R.string.wip_title), Toast.LENGTH_SHORT).show()
        }
    }
}