package com.example.rchat.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rchat.MessageItemRvAdapter
import com.example.rchat.PreviewChatRvAdapter
import org.json.JSONObject

@SuppressLint("StaticFieldLeak")
object ChatSingleton {
    var outgoingMessagesList = mutableListOf<String>()
    var incomingMessagesList = mutableListOf<String>()
    var previewMessagesList = mutableListOf<String>()
    var previewLoginsList = mutableListOf<String>()
    var previewTimeList = mutableListOf<String>()
    var incomingLoginsList = mutableListOf<String>()
    var outgoingLoginsList = mutableListOf<String>()
    private lateinit var response: List<JSONObject>
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

    fun getChatsWindowContext(): Context {
        return chatsWindowContext
    }

    fun getChatsWindowRecyclerView(): RecyclerView {
        return chatsWindowRecView
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
        //sendChatsRequest()
        updateChatList(Arnold, "", parsedMessage[1])

        if (chatItselfWindowRecView != null && parsedMessage[0] == Billy) {
            updateMessageList(parsedMessage[0], parsedMessage[1])
            //sendChatsRequest()
            updateChatList(Billy, "", parsedMessage[1])
        }
    }

    fun sendMessage(recipientLogin: String, message: String) {
        //TODO("Обработка ошибки отправки сообщения")
        webSocketClient.send("/app/user/", recipientLogin, "$Arnold $message")
        updateMessageList(Arnold, message)
        updateChatList(recipientLogin, "", message)
    }

    fun updateChatList(recipientLogin: String, time: String, message: String) {
        if (recipientLogin in previewLoginsList)  // TODO обновление времени
            previewMessagesList[previewLoginsList.indexOf(recipientLogin)] = message
        else
            ChatFunctions().addToList(
                previewLoginsList,
                previewTimeList,
                previewMessagesList,
                recipientLogin,
                time,
                message
            )
        chatsWindowRecView.layoutManager = LinearLayoutManager(chatsWindowContext)
        chatsWindowRecView.adapter = PreviewChatRvAdapter(
            previewLoginsList,
            previewTimeList,
            previewMessagesList,
            chatsWindowContext
        )
    }

    fun updateMessageList(senderLogin: String, message: String) {
        if (senderLogin == Arnold)
            ChatFunctions().addToList(
                incomingLoginsList,
                incomingMessagesList,
                outgoingLoginsList,
                outgoingMessagesList,
                "",
                "",
                senderLogin,
                message
            )
        else
            ChatFunctions().addToList(
                incomingLoginsList,
                incomingMessagesList,
                outgoingLoginsList,
                outgoingMessagesList,
                senderLogin,
                message,
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

    fun sendChatsRequest() {
        if (previewLoginsList.isNotEmpty())
            previewLoginsList.clear()
        if (previewTimeList.isNotEmpty())
            previewTimeList.clear()
        if (previewMessagesList.isNotEmpty())
            previewMessagesList.clear()
        response = JasonSTATHAM().zapretParsinga(
            Requests().get(
                mapOf(
                    "username" to Arnold
                ), "http://192.168.1.107:8080/chats"
            )
        )
        var username: String
        for (el in response) {
            username =
                if ((el["sender"] as JSONObject)["username"].toString() == Arnold
                )
                    (el["recipient"] as JSONObject)["username"].toString()
                else
                    (el["sender"] as JSONObject)["username"].toString()
            updateChatList(
                username,
                el["time"].toString(),
                el["messageText"].toString()
            )
        }
    }
}