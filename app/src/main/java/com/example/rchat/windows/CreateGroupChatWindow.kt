package com.example.rchat.windows

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
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
import com.example.rchat.utils.ChatSingleton

/* Оконный класс создания бесед - групповых чатов
*/
class CreateGroupChatWindow : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        /* Установка темы приложения
        */
        val prefs = getSharedPreferences("Night Mode", Context.MODE_PRIVATE)
        when (prefs.getString("NightMode", "Day")) {
            "Day" -> setTheme(R.style.Theme_Light)
            "Night" -> setTheme(R.style.Theme_Dark)
            "System" -> {
                when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.Theme_Dark)
                    Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.Theme_Light)
                }
            }
        }

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