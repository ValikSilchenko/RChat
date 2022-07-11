package com.example.rchat.windows

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.rchat.R
import com.example.rchat.utils.ChatFunctions
import com.example.rchat.utils.ChatSingleton
import com.example.rchat.utils.JasonSTATHAM
import com.example.rchat.utils.Requests
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ChatsWindow : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

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
        setContentView(R.layout.chats_window)

//        val networkConnection = NetworkConnectionLiveData(applicationContext)   //!

        val userLogin: TextView = findViewById(R.id.CW_AppNameTV)
        val chatArray: RecyclerView = findViewById(R.id.CW_ChatsRV)
        val moreBtn: ImageButton = findViewById(R.id.CW_MoreBtn)
        val user = ChatFunctions().getSavedLogin(this)
        userLogin.text = user

        ChatSingleton.setChatsWindow(chatArray, user, this)

//        networkConnection.observe(this) { isConnected ->
//            if (isConnected)
//                Toast.makeText(applicationContext, "Internet connected", Toast.LENGTH_SHORT).show()
//            else
//                Toast.makeText(applicationContext, "Internet disconnected", Toast.LENGTH_SHORT).show()
//        }

        try {
            val response: List<JSONObject> = JasonSTATHAM().stringToListOfJSONObj(
                Requests().get(
                    mapOf("username" to user),
                    "${ChatSingleton.serverUrl}/chats"
                )
            )
            var username: String
            var youTxt: String
            var time: String
            var id: Int
            var unreadMsg: Int
            for (el in response) {
                if ((el["sender"] as JSONObject)["username"].toString() == user) {
                    username = (el["recipient"] as JSONObject)["username"].toString()
                    youTxt = "Вы:"
                    id = (el["recipient"] as JSONObject)["id"] as Int
                    unreadMsg = 0
                } else {
                    username = (el["sender"] as JSONObject)["username"].toString()
                    youTxt = ""
                    id = (el["sender"] as JSONObject)["id"] as Int
                    unreadMsg = Requests().get(
                        mapOf("sender" to username, "recipient" to user),
                        "${ChatSingleton.serverUrl}/count"
                    ).toInt()
                }

                val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
                time = if (el["date"] == sdf)
                    el["time"].toString()
                else
                    el["date"].toString()
                ChatSingleton.updateChatList(
                    username,
                    time,
                    el["messageText"].toString(),
                    youTxt,
                    id,
                    unreadMsg
                )
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
                    R.id.new_chat_item -> {
                        startActivity(Intent(this, FindUsersWindow::class.java))
                        overridePendingTransition(
                            android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right
                        )
                        true
                    }
                    R.id.new_group_chat_item -> {
                        startActivity(Intent(this, CreateGroupChatWindow::class.java))
                        true
                    }
                    R.id.settings_item -> {
                        val intent = Intent(this, SettingsWindow::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        startActivity(intent)
                        overridePendingTransition(
                            android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right
                        )
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
            } finally {
                popupMenu.show()
            }
        }
    }

    @Override
    override fun onBackPressed() {
        val message: AlertDialog.Builder = AlertDialog.Builder(this)
        message
            .setTitle(getString(R.string.attention_title))
            .setMessage(getString(R.string.really_wanna_exit_app_title))
            .setCancelable(true)
            .setPositiveButton(
                getString(R.string.yes_title)
            ) { _, _ -> finish() }
            .setNegativeButton(getString(R.string.no_title)) { dialog, _ -> dialog.cancel() }
        val messageWindow = message.create()
        messageWindow.show()
    }
}
