package com.example.rchat.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rchat.MessageItemRvAdapter
import com.example.rchat.PreviewChatRvAdapter

@SuppressLint("StaticFieldLeak")
object ChatSingleton {
    private var outgoingMessagesList = mutableListOf<String>()
    private var incomingMessagesList = mutableListOf<String>()
    private var previewMessagesList = mutableListOf<String>()
    private var previewLoginsList = mutableListOf<String>()
    private var previewTimeList = mutableListOf<String>()
    var incomingLoginsList = mutableListOf<String>()
    var outgoingLoginsList = mutableListOf<String>()

    private var webSocketClient = WebSocketClient()

    private lateinit var chatsWindowRecView: RecyclerView
    private var chatItselfWindowRecView: RecyclerView? = null

    private lateinit var chatItselfContext: Context
    private lateinit var chatsWindowContext: Context

    // Логин собеседника
    private var Billy = "Herrington"

    // Логин атворизованного пользователя
    private var Arnold = "Shwarzenegger"

    fun setChatsWindow(recView: RecyclerView, username: String, incomingContext: Context) {
        chatsWindowRecView = recView
        chatsWindowContext = incomingContext
        Arnold = username
    }

    fun setChatItselfWindow(recView: RecyclerView, username: String, incomingContext: Context) {
        chatItselfWindowRecView = recView
        Billy = username
        chatItselfContext = incomingContext
    }

    // Очистка массивов
    fun clearLists(recView: RecyclerView) {
        incomingLoginsList.clear()
        incomingMessagesList.clear()
        outgoingLoginsList.clear()
        outgoingMessagesList.clear()
        recView.layoutManager = LinearLayoutManager(chatItselfContext)
        recView.adapter = MessageItemRvAdapter(
            incomingLoginsList, incomingMessagesList, outgoingLoginsList, outgoingMessagesList
        )
    }

    fun openConnection(username: String) {
        webSocketClient.connect("http://localhost:8080/ws", username)
    }

    fun processMessage(message: String) {
        val parsedMessage = JasonSTATHAM().parseMessage(message)
        if (parsedMessage[0] in previewLoginsList)
            previewMessagesList[previewLoginsList.indexOf(parsedMessage[0])] = parsedMessage[1]
        else
            Functions().addToList(
                previewLoginsList,
                previewTimeList,
                previewMessagesList,
                parsedMessage[0],
                "",
                parsedMessage[1]
            )
        chatsWindowRecView.layoutManager = LinearLayoutManager(chatsWindowContext)
        chatsWindowRecView.adapter = PreviewChatRvAdapter(
            previewLoginsList,
            previewTimeList,
            previewMessagesList,
            chatsWindowContext
        )

        if (chatItselfWindowRecView != null && parsedMessage[0] == Billy) {
            Functions().addToList(
                incomingLoginsList,
                incomingMessagesList,
                outgoingLoginsList,
                outgoingMessagesList,
                Billy,
                parsedMessage[1],
                "",
                ""
            )
            chatItselfWindowRecView?.layoutManager = LinearLayoutManager(chatItselfContext)
            chatItselfWindowRecView?.adapter = MessageItemRvAdapter(
                incomingLoginsList,
                incomingMessagesList,
                outgoingLoginsList,
                outgoingMessagesList
            )
        }
    }

    fun sendMessage(recipientLogin: String, message: String) {
        //TODO("Обработка ошибки отправки сообщения")
        webSocketClient.send("/user/", recipientLogin, message)
        Functions().addToList(
            incomingLoginsList,
            incomingMessagesList,
            outgoingLoginsList,
            outgoingMessagesList,
            "",
            "",
            Arnold,
            message
        )
        chatItselfWindowRecView?.layoutManager = LinearLayoutManager(chatItselfContext)
        chatItselfWindowRecView?.adapter = MessageItemRvAdapter(
            incomingLoginsList,
            incomingMessagesList,
            outgoingLoginsList,
            outgoingMessagesList
        )
    }
}