package com.example.rchat.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rchat.MessageItemRvAdapter
import com.example.rchat.PreviewChatRvAdapter

@SuppressLint("StaticFieldLeak")
object ChatSingleton {
    var outgoingMessagesList = mutableListOf<String>()
    var incomingMessagesList = mutableListOf<String>()
    var previewMessagesList = mutableListOf<String>()
    var previewLoginsList = mutableListOf<String>()
    var previewTimeList = mutableListOf<String>()
    var incomingLoginsList = mutableListOf<String>()
    var outgoingLoginsList = mutableListOf<String>()
    private var webSocketClient = WebSocketClient()
    private var chatItselfWindowRecView: RecyclerView? = null
    private lateinit var chatsWindowRecView: RecyclerView
    private lateinit var chatItselfContext: Context
    private lateinit var chatsWindowContext: Context
    private var Billy = "Herrington" // Логин собеседника
    private var Arnold = "Shwarzenegger" // Логин атворизованного пользователя

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

    fun getLogin(): String {
        return Arnold
    }

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
        webSocketClient.connect("http://192.168.1.107:8080/ws", username)
    }

    fun processMessage(message: String) {
        val parsedMessage = JasonSTATHAM().parseMessage(message)
        if (parsedMessage[0] in previewLoginsList)
            previewMessagesList[previewLoginsList.indexOf(parsedMessage[0])] = parsedMessage[1]
        else
            ChatFunctions().addToList(
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
            ChatFunctions().addToList(
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
        webSocketClient.send("/app/user/", recipientLogin, "$Arnold $message")
        ChatFunctions().addToList(
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