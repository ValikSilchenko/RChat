package com.example.rchat

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.rchat.utils.ChatFunctions
import com.example.rchat.utils.ChatSingleton
import com.example.rchat.utils.JasonSTATHAM
import com.example.rchat.utils.Requests
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ChatsWindow : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.Theme_Dark)
            Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.Theme_Light)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.chats_window)

        val userLogin: TextView = findViewById(R.id.CW_AppName)
        val chatArray: ListView = findViewById(R.id.CW_ChatsArray)
        val moreBtn: ImageButton = findViewById(R.id.CW_MoreBtn)

        val user = getSharedPreferences("Authorization", Context.MODE_PRIVATE).getString(
            "LOGIN_KEY",
            "NaN"
        ).toString()
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
            var time: String
            for (el in response) {
                if ((el["sender"] as JSONObject)["username"].toString() == user) {
                    username = (el["recipient"] as JSONObject)["username"].toString()
                    youTxt = "Вы:"
                } else {
                    username = (el["sender"] as JSONObject)["username"].toString()
                    youTxt = ""
                }
                val sdf = SimpleDateFormat("dd.MM.yyyy")
                println("Test date: ${sdf.format(Calendar.getInstance().time)}")
                time = if (el["date"] == sdf.format(Calendar.getInstance().time))
                    el["time"].toString()
                else
                    el["date"].toString()

                ChatSingleton.updateChatList(username, time, el["messageText"].toString(), youTxt)
            }
        } catch (error: Exception) {
            ChatFunctions().showMessage(
                "Ошибка",
                "Окно чатов: ${error.message}",
                this
            )
        }

        moreBtn.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.newchat_item -> {
                        startActivity(Intent(this, FindUsersWindow::class.java))
                        overridePendingTransition(
                            android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right
                        )
                        true
                    }
                    R.id.settings_item -> {
                        val intent = Intent(this, SettingsWindow::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.inflate(R.menu.more_cw_menu)
            try {
                val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                fieldMPopup.isAccessible = true
                val mPopup = fieldMPopup.get(popupMenu)
                mPopup.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(mPopup, true)
            } catch (e: Exception) {
                Log.e("Main", "Error of showing popup menu icons")
            }
            finally {
                popupMenu.show()
            }
        }
    }

    @Override
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
}
