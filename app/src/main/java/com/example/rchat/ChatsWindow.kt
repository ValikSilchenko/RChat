package com.example.rchat

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.ImageButton
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
        setContentView(R.layout.chats_window)

        val newChatBtn: ImageButton = findViewById(R.id.NewChat_Btn)
        val userLogin: TextView = findViewById(R.id.AppName)
        val chatArray: ListView = findViewById(R.id.ChatListView)
        val settingsBtn: ImageButton = findViewById(R.id.Settings_Btn)

        val user = getSharedPreferences("Authorization", Context.MODE_PRIVATE).getString("LOGIN_KEY", "NaN").toString()
        userLogin.text = user

        ChatSingleton.setChatsWindow(chatArray, user, this)
        ChatSingleton.clearChatList()

        try {
            val response: List<JSONObject> = JasonSTATHAM().zapretParsinga(
                Requests().get(
                    mapOf("username" to user),
                    "${ChatSingleton.serverUrl}/chats"
                )
            )
            var username: String
            var youTxt: String
            for (el in response) {
                    if ((el["sender"] as JSONObject)["username"].toString() == user) {
                        username = (el["recipient"] as JSONObject)["username"].toString()
                        youTxt = "You:"
                    }
                    else {
                        username = (el["sender"] as JSONObject)["username"].toString()
                        youTxt = ""
                    }
                ChatSingleton.updateChatList(username, el["time"].toString(), el["messageText"].toString(), youTxt)
            }
        } catch (error: Exception) {
            ChatFunctions().showMessage("Ошибка", "Окно чатов: ${error.message}", this)
        }

        newChatBtn.setOnClickListener {
            startActivity(Intent(this, FindUsersWindow::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        settingsBtn.setOnClickListener {
            val intent = Intent(this, SettingsWindow::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(intent)
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }

    @Override
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
}
