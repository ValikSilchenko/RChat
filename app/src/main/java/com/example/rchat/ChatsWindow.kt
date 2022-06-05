package com.example.rchat

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.rchat.utils.ChatSingleton
import com.example.rchat.utils.Functions

class ChatsWindow : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.Theme_Dark)
            Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.Theme_Light)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.chats)

        val chatArray: RecyclerView = findViewById(R.id.Chat_Array)
        val newChatBtn: Button = findViewById(R.id.NewChat_Btn)

        ChatSingleton.setChatsWindow(
            chatArray,
            intent.getStringExtra("User Login").toString(),
            this
        )
        try {
            ChatSingleton.openConnection(intent.getStringExtra("User Login").toString())
        } catch (exception: Exception) {
            Functions().showMessage("Ошибка", "Ошибка установки соединения", this)
            //TODO("Обработка ошибки при отсутствии интернетов")
        }

        newChatBtn.setOnClickListener {
            startActivity(Intent(this, FindUsersWindow::class.java))
        }
    }

    @Override
    override fun onBackPressed() {
    }

    private fun getStrings(username: String, message: String) {

    }
}
