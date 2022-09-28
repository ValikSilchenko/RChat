package com.example.rchat.windows

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
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

/* Оконный класс списка чатов
*/
class ChatsWindow : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        /* Установка темы приложения
        */
        ChatFunctions().setAppTheme(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.chats_window)

//        val networkConnection = NetworkConnectionLiveData(applicationContext)   //!
        val noChatsTxt: TextView = findViewById(R.id.CW_NoChatsTxt)
        val userLogin: TextView = findViewById(R.id.CW_AppNameTV)
        val chatArray: RecyclerView = findViewById(R.id.CW_ChatsRV)
        val moreBtn: ImageButton = findViewById(R.id.CW_MoreBtn)
        val user = ChatFunctions().getSavedLogin(this)
        userLogin.text = user

        /* Сеттер данного окна в синглтоне
        */
        ChatSingleton.setChatsWindow(chatArray, user, this, packageName, noChatsTxt)

//        networkConnection.observe(this) { isConnected ->
//            if (isConnected)
//                Toast.makeText(applicationContext, "Internet connected", Toast.LENGTH_SHORT).show()
//            else
//                Toast.makeText(applicationContext, "Internet disconnected", Toast.LENGTH_SHORT).show()
//        }

        /* Получение списка чатов
        */
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
                    youTxt = getString(R.string.you_title)
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
                getString(R.string.error_title),
                "${getString(R.string.error_of_receiving_data_title)} ${error.message}",
                this
            )
        }

        /* Скрытие текста, если чаты есть
        */
        if (ChatSingleton.chatsArrayList.size > 0)
            noChatsTxt.visibility = View.GONE

        /* Нажатие кнопки опций
        */
        moreBtn.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.new_chat_item -> {     /* Открытие окна поиска пользователей */
                        startActivity(Intent(this, FindUsersWindow::class.java))
                        overridePendingTransition(
                            android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right
                        )
                        true
                    }
                    R.id.new_group_chat_item -> {   /* Открытие окна создания беседы */
                        Toast.makeText(this, getString(R.string.wip_title), Toast.LENGTH_SHORT).show()
//                        startActivity(Intent(this, CreateGroupChatWindow::class.java))
                        true
                    }
                    R.id.settings_item -> {     /* Открытие окна настроек */
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
