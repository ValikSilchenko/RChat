package com.example.rchat

import android.content.res.Configuration
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

class Chats : AppCompatActivity() {

    // Переменные для списка чатов
    private var previewChatLogins = mutableListOf<String>()
    private var previewChatReceivingTimes = mutableListOf<String>()
    private var previewChatMessages = mutableListOf<String>()
    private var previewChatIDs = mutableListOf<Int>()

    // Переменные для чата-переписки
    private var incomingLoginsList = mutableListOf<String>()
    private var incomingMessagesTexts = mutableListOf<String>()
    private var outgoingLoginsList = mutableListOf<String>()
    private var outgoingMessagesTexts = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.Theme_Dark)
            Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.Theme_Light)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.chats)

        val newChatBtn: Button = findViewById(R.id.NewChat_Btn)
        val mainMenuBtn: Button = findViewById(R.id.MainMenu_Btn)
        val findUserBackBtn: Button = findViewById(R.id.FindUserBack_Btn)
        val findUserFindBtn: Button = findViewById(R.id.FindUserFind_Btn)
        val sendMessageBtn: Button = findViewById(R.id.Send_Btn)
        val chatArray: RecyclerView = findViewById(R.id.Chat_Array)
        val messagesArray: RecyclerView = findViewById(R.id.Messages_List)
        val chatItselfWindow: LinearLayout = findViewById(R.id.Chat_Window)
        val chatsListWindow: LinearLayout = findViewById(R.id.ChatsList_Window)
        val findUserWindow: LinearLayout = findViewById(R.id.FindUser_Window)
        val findUserLoginEditText: EditText = findViewById(R.id.FindUserLogin_EditText)
        val messageInput: EditText = findViewById(R.id.Message_Input)
        val chatName: TextView = findViewById(R.id.UserName_ChatText)
        chatItselfWindow.isVisible = false
        findUserWindow.isVisible = false
        chatsListWindow.isVisible = true

        postToList()
        chatArray.layoutManager = LinearLayoutManager(this)
        chatArray.adapter =
            PreviewChatRvAdapter(
                previewChatLogins,
                previewChatReceivingTimes,
                previewChatMessages,
                previewChatIDs,
                chatsListWindow,
                chatItselfWindow,
                findUserWindow
            )

        //Поиск чата (окно списка чатов)
        newChatBtn.setOnClickListener {
            chatItselfWindow.isVisible = false
            chatsListWindow.isVisible = false
            findUserWindow.isVisible = true
        }

        // Возврат в главное меню (окно самого чата)
        mainMenuBtn.setOnClickListener {
            chatItselfWindow.isVisible = false
            findUserWindow.isVisible = false
            chatsListWindow.isVisible = true
        }

        // Возврат в главное меню (окно поиска пользователей)
        findUserBackBtn.setOnClickListener {
            chatItselfWindow.isVisible = false
            findUserWindow.isVisible = false
            chatsListWindow.isVisible = true
        }

        // Нажатие кнопки поиска пользователя (окно поиска пользователей)
        findUserFindBtn.setOnClickListener {
            if (findUserLoginEditText.text.isNotEmpty()) {
                val client = OkHttpClient()
                val dataToSend = FormBody.Builder()
                    .add("username", findUserLoginEditText.text.toString())
                    .build()
                val requestToSend = Request.Builder()
                    .post(dataToSend)
                    .url("http://192.168.1.107:8080/login")
                    .build()
                client.newCall(requestToSend).execute().use { response ->
                    if (!response.isSuccessful)
                        Toast.makeText(this, "Пользователь не найден", Toast.LENGTH_SHORT).show()
                    // else вывести на экран
                }
            }
        }

        sendMessageBtn.setOnClickListener {
            messagesArray.layoutManager = LinearLayoutManager(this)
            messagesArray.adapter = MessageItemRvAdapter(incomingLoginsList, incomingMessagesTexts, outgoingLoginsList, outgoingMessagesTexts)
        }
    }

    @Override
    override fun onBackPressed() {
    }

    private fun addToChatList(
        previewLogin: String,
        previewReceivingTime: String,
        previewMessage: String,
        chatID: Int
    ) {
        previewChatLogins.add(previewLogin)
        previewChatReceivingTimes.add(previewReceivingTime)
        previewChatMessages.add(previewMessage)
        previewChatIDs.add(chatID)
    }

    private fun addToMessagesList(incomingLogin: String, incomingMessage: String, outgoingLogin: String, outgoingMessage: String) {
        incomingLoginsList.add(incomingLogin)
        incomingMessagesTexts.add(incomingMessage)
        outgoingLoginsList.add(outgoingLogin)
        outgoingMessagesTexts.add(outgoingMessage)
    }

    // Заполнение RecyclerView (ДЕБАГ)
    private fun postToList() {
        for (i in 1..25) {
            addToChatList(
                "Login #${i}",
                "19:57",
                "Some message text",
                (0..100).random()
            )
        }
    }
}
