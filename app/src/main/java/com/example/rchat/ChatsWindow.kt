package com.example.rchat

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        val chatArray: RecyclerView = findViewById(R.id.Chat_Array)
        val newChatBtn: Button = findViewById(R.id.NewChat_Btn)
        var response: List<JSONObject>

        ChatSingleton.setChatsWindow(
            chatArray,
            intent.getStringExtra("User Login").toString(),
            this
        )
        try {
            ChatSingleton.openConnection(intent.getStringExtra("User Login").toString())
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
            if ((el["sender"] as JSONObject)["username"].toString() == intent.getStringExtra("User Login").toString())
                username = (el["recipient"] as JSONObject)["username"].toString()
            else
                username = (el["sender"] as JSONObject)["username"].toString()
            ChatFunctions().addToList(
                ChatSingleton.previewLoginsList,
                ChatSingleton.previewTimeList,
                ChatSingleton.previewMessagesList,
                username,
                el["time"].toString(),
                el["messageText"].toString()
            )
        }
        chatArray.layoutManager = LinearLayoutManager(this)
        chatArray.adapter = PreviewChatRvAdapter(ChatSingleton.previewLoginsList, ChatSingleton.previewTimeList, ChatSingleton.previewMessagesList, this)


        newChatBtn.setOnClickListener {
            startActivity(Intent(this, FindUsersWindow::class.java))
        }
    }

    @Override
    override fun onBackPressed() {
    }
}
