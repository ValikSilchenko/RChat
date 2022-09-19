package com.example.rchat.windows

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.rchat.R
import com.example.rchat.utils.ChatFunctions
import com.example.rchat.utils.ChatSingleton
import com.example.rchat.utils.JasonSTATHAM
import com.example.rchat.utils.Requests
import org.json.JSONObject

/* Оконный класс чата
*/
class ChatItselfWindow : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        val idArray: ArrayList<Int> = ArrayList()

        /* Установка темы приложения
        */
        ChatFunctions().setAppTheme(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_itself_window)

        val backToMainMenuBtn: ImageButton = findViewById(R.id.CIW_BackBtn)
        val sendMessageBtn: ImageButton = findViewById(R.id.CIW_SendMessageBtn)
        val attachBtn: ImageButton = findViewById(R.id.CIW_AttachBtn)
        val chatNameTV: TextView = findViewById(R.id.CIW_ChatNameTV)
        val messagesLV: ListView = findViewById(R.id.CIW_MessagesRV)
        val messageInputET: EditText = findViewById(R.id.CIW_MessageInputET)

        val chatLogin = ChatSingleton.chatName
        chatNameTV.text = chatLogin

        /* Сеттер данного окна в синглтоне
        */
        ChatSingleton.setChatItselfWindow(
            messagesLV,
            chatLogin,
            this,
            messageInputET
        )

        /* Запрос на количество непрочитанных сообщений
        */
        val unreadCount = Requests().get(
            mapOf("sender" to chatLogin, "recipient" to ChatSingleton.Van),
            "${ChatSingleton.serverUrl}/count"
        ).toInt()

        /* Запрос и последующий показ списка сообщений
        */
        val response: List<JSONObject> = JasonSTATHAM().stringToListOfJSONObj(
            Requests().get(
                mapOf(
                    "sender" to ChatSingleton.Van,
                    "recipient" to ChatSingleton.Billy
                ),
                "${ChatSingleton.serverUrl}/personal"
            )
        )
        for (el in response) {
            if (!(el["read"] as Boolean))
                idArray.add(el["id"] as Int)
            ChatSingleton.updateMessageList(
                (el["sender"] as JSONObject)["username"].toString(),
                el["messageText"].toString(),
                "${el["date"]} ${el["time"]}",
                el["id"] as Int
            )
        }
        ChatSingleton.focusOnLastItem(unreadCount)

        /* Отправка запроса на прочтение сообщений
        */
        idArray.forEach {
            ChatSingleton.sendRequestForReading(chatLogin, it)
        }

        /* Нажатие кнопки выхода в список чатов через кнопку выхода
        */
        backToMainMenuBtn.setOnClickListener {
            closeChatItselfWindow()
        }

        /* Нажатие кнопки перехода в окно медиа чатов
        */
        chatNameTV.setOnClickListener {
            val mIntent = Intent(this, MediaChatWindow::class.java)
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(mIntent)
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        /* Нажатие кнопки прикрепления файлов
        */
        attachBtn.setOnClickListener {
            Toast.makeText(applicationContext, getString(R.string.wip_title), Toast.LENGTH_SHORT).show()
        }

        /* Нажатие кнопки отправки сообщения
        */
        sendMessageBtn.setOnClickListener {
            if (messageInputET.text.isNotEmpty())
                ChatSingleton.sendMessage(
                    chatLogin,
                    messageInputET.text.toString(),
                    messageInputET
                )
        }
    }

    @Override
    override fun onBackPressed() {
        closeChatItselfWindow()
    }

    /* Выход из чата - очистка списка сообщений, установка флага, что пользователь не в чате и очистка имени собеседника
    */
    private fun closeChatItselfWindow() {
        ChatSingleton.apply {
            isInChat = false
            Billy = "Herrington"
            clearMessagesList()
        }
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        finish()
    }
}