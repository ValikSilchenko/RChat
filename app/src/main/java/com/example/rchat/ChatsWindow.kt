package com.example.rchat

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.rchat.utils.ChatFunctions
import com.example.rchat.utils.ChatSingleton
import com.example.rchat.utils.JasonSTATHAM
import com.example.rchat.utils.Requests
import org.json.JSONObject

class ChatsWindow : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.Theme_Dark)
            Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.Theme_Light)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.chats)

        //val chatArray: RecyclerView = findViewById(R.id.Chat_Array)
        val newChatBtn: Button = findViewById(R.id.NewChat_Btn)
        var response: List<JSONObject>
        val userLogin: TextView = findViewById(R.id.AppName)
        val chatArray: ListView = findViewById(R.id.ChatListView)
        userLogin.text = intent.getStringExtra("User Login").toString()

        val user = intent.getStringExtra("User Login").toString()

        ChatSingleton.setChatsWindow(chatArray, user, this)
        try {
            ChatSingleton.openConnection(user)
        } catch (exception: Exception) {
            ChatFunctions().showMessage("Ошибка", "Ошибка установки соединения", this)
            //TODO("Обработка ошибки при отсутствии интернетов")
        }

        response = JasonSTATHAM().zapretParsinga(
            Requests().get(
                mapOf(
                    "username" to intent.getStringExtra("User Login").toString()
                ), "http://192.168.1.107:8080/chats"
            )
        )
        var username: String
        for (el in response) {
            username =
                if ((el["sender"] as JSONObject)["username"].toString() == intent.getStringExtra("User Login")
                        .toString()
                )
                    (el["recipient"] as JSONObject)["username"].toString()
                else
                    (el["sender"] as JSONObject)["username"].toString()
            ChatSingleton.updateChatList(
                username,
                el["time"].toString(),
                el["messageText"].toString()
            )
        }

        newChatBtn.setOnClickListener {
            startActivity(Intent(this, FindUsersWindow::class.java))
        }
    }

    @Override
    override fun onBackPressed() {
    }
}