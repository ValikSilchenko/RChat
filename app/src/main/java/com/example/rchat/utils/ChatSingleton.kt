package com.example.rchat.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.ListView
import androidx.recyclerview.widget.RecyclerView
import com.example.rchat.MessageItemDataClass
import com.example.rchat.MessageItemLVAdapter
import com.example.rchat.PreviewChatDataClass
import com.example.rchat.PreviewChatLVAdapter
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
    private lateinit var chatItselfContext: Activity
    private lateinit var chatsWindowContext: Activity
    private var Billy = "Herrington" // Логин собеседника
    private var Arnold = "Shwarzenegger" // Логин атворизованного пользователя

    private lateinit var chatWindowLV: ListView
    private lateinit var chatItselfLV: ListView
    var chatsArrayList: ArrayList<PreviewChatDataClass> = ArrayList()
    val messagesArrayList: ArrayList<MessageItemDataClass> = ArrayList()

    fun setChatsWindow(listView: ListView, username: String, incomingContext: Activity) {
        chatWindowLV = listView
        chatsWindowContext = incomingContext
        Arnold = username
    }

    fun setChatItselfWindow(listView: ListView, username: String, incomingContext: Activity) {
        chatItselfLV = listView
        Billy = username
        chatItselfContext = incomingContext
    }

    fun getLogin(): String {
        return Arnold
    }

    fun clearLists() {
        messagesArrayList.clear()
        chatItselfLV.adapter = MessageItemLVAdapter(chatItselfContext, messagesArrayList)
    }

    fun openConnection(username: String) {
        webSocketClient.connect("http://192.168.1.107:8080/ws", username)
    }

    fun processMessage(message: String) {
        val parsedMessage = JasonSTATHAM().parseMessage(message)
        updateChatList(Arnold, "", parsedMessage[1])

        if (chatItselfWindowRecView != null && parsedMessage[0] == Billy) {
            updateMessageList(parsedMessage[0], parsedMessage[1])
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
        var isInArray = false
        var index = 0
        for (el in chatsArrayList.indices)
        {
            if (chatsArrayList[el].previewLogin == recipientLogin) {
                isInArray = true
                index = el
                break
            }
        }
        if (isInArray) {
            chatsArrayList[index].previewMessage = message
        }
        else {
            val data = PreviewChatDataClass(recipientLogin, time, message)
            chatsArrayList.add(data)
        }
        chatWindowLV.adapter = PreviewChatLVAdapter(chatsWindowContext, chatsArrayList)
    }

    fun updateMessageList(senderLogin: String, message: String) {
        if (senderLogin == Arnold) {
            val data1 = MessageItemDataClass("", "", senderLogin, message)
            messagesArrayList.add(data1)
            chatItselfLV.adapter = MessageItemLVAdapter(chatItselfContext, messagesArrayList)
        }
        else {
            val data2 = MessageItemDataClass(senderLogin, message, "", "")
            messagesArrayList.add(data2)
            chatItselfLV.adapter = MessageItemLVAdapter(chatItselfContext, messagesArrayList)
        }
    }
}