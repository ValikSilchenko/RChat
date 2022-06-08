package com.example.rchat

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.rchat.utils.ChatFunctions
import com.example.rchat.utils.ChatSingleton
import com.example.rchat.utils.JasonSTATHAM
import com.example.rchat.utils.Requests
import org.json.JSONObject
import java.lang.Exception

class ChatsWindow : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.Theme_Dark)
            Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.Theme_Light)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.chats)

        val newChatBtn: Button = findViewById(R.id.NewChat_Btn)
        val userLogin: TextView = findViewById(R.id.AppName)
        val chatArray: ListView = findViewById(R.id.ChatListView)
        userLogin.text = intent.getStringExtra("User Login").toString()

        val user = intent.getStringExtra("User Login").toString()

        ChatSingleton.setChatsWindow(chatArray, user, this)
        ChatSingleton.clearChatList()

        try {
            val response: List<JSONObject> = JasonSTATHAM().zapretParsinga(
                Requests().get(
                    mapOf("username" to user),
                    "http://192.168.1.107:8080/chats"
                )
            )
            var username: String
            for (el in response) {
                username =
                    if ((el["sender"] as JSONObject)["username"].toString() == user
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
        } catch (error: Exception) {  // TODO
            ChatFunctions().showMessage("Ошибка", "${error.message}", this)
        }


        newChatBtn.setOnClickListener {
            startActivity(Intent(this, FindUsersWindow::class.java))
        }
    }

    @Override
    override fun onBackPressed() {
        val exitMessage: AlertDialog.Builder = AlertDialog.Builder(this)
        exitMessage
            .setTitle("Предупреждение")
            .setMessage("Вы действительно хотите выйти?")
            .setCancelable(true)
            .setPositiveButton("Да") { _, _ -> finish() }
            .setNegativeButton(
                "Нет"
            ) { dialog, _ -> dialog.cancel() }
        val exitWindow = exitMessage.create()
        exitWindow.show()
    }
}
