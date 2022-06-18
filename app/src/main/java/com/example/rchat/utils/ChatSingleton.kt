package com.example.rchat.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.ListView
import com.example.rchat.MessageItemDataClass
import com.example.rchat.MessageItemLVAdapter
import com.example.rchat.PreviewChatDataClass
import com.example.rchat.PreviewChatLVAdapter
import org.json.JSONObject

@SuppressLint("StaticFieldLeak")
object ChatSingleton {
    private var webSocketClient = WebSocketClient()
    private lateinit var chatItselfContext: Activity
    private lateinit var chatsWindowContext: Activity
    private var Billy = "Herrington" // Логин собеседника
    private var Van = "Darkholme" // Логин авторизованного пользователя

    private lateinit var chatWindowLV: ListView
    private var chatItselfLV: ListView? = null
    var chatsArrayList: ArrayList<PreviewChatDataClass> = ArrayList()
    val messagesArrayList: ArrayList<MessageItemDataClass> = ArrayList()

    private lateinit var chatArrayAdapter: PreviewChatLVAdapter
    private lateinit var messagesArrayAdapter: MessageItemLVAdapter

    fun setChatsWindow(listView: ListView, username: String, incomingContext: Activity) {
        chatWindowLV = listView
        chatsWindowContext = incomingContext
        Van = username
        chatArrayAdapter = PreviewChatLVAdapter(chatsWindowContext, chatsArrayList)
    }

    fun setChatItselfWindow(listView: ListView, username: String, incomingContext: Activity) {
        chatItselfLV = listView
        Billy = username
        chatItselfContext = incomingContext
        messagesArrayAdapter = MessageItemLVAdapter(chatItselfContext, messagesArrayList)
    }

    fun clearChatList() {
        if (chatsArrayList.isNotEmpty())
            chatsArrayList.clear()
    }

    fun clearMessageList() {
        if (chatsArrayList.isNotEmpty())
            messagesArrayList.clear()
    }

    fun openConnection(username: String) {
        webSocketClient.connect("http://192.168.1.107:8080/ws", username)
    }

    fun processMessage(message: String) {
        val parsedMessage = JasonSTATHAM().parseMessage(message)
        println("parsed")

        updateChatList(parsedMessage[0], "", parsedMessage[1])
        println("after receive: chats list update")

        if (parsedMessage[0] == Billy) {
            println("//")
            updateMessageList(parsedMessage[0], parsedMessage[1])
            println("after receive: msg list updated")
        }
    }

    fun sendMessage(recipientLogin: String, message: String) {
        //TODO("Обработка ошибки отправки сообщения")
        webSocketClient.send("/app/user/", recipientLogin, "$Van $message")
        updateMessageList(Van, message)
        println("after send: msg list updated")
        updateChatList(recipientLogin, "", message)
        println("after send: chats list updated")
    }

    fun updateChatList(recipientLogin: String, time: String, message: String) {
        var isInArray = false
        var index = 0
        for (el in chatsArrayList.indices) {
            if (chatsArrayList[el].previewLogin == recipientLogin) {
                isInArray = true
                index = el
                break
            }
        }
        if (isInArray) {
            chatsArrayList[index].previewMessage = message
        } else {
            val data = PreviewChatDataClass(recipientLogin, time, "Вы: $message")
            chatsArrayList.add(data)
        }
        chatArrayAdapter.notifyDataSetChanged()
        chatWindowLV.adapter = chatArrayAdapter
    }

    fun updateMessageList(senderLogin: String, message: String) {
        if (senderLogin == Van) {
            val data1 = MessageItemDataClass("", "", senderLogin, message)
            messagesArrayList.add(data1)
        } else {
            val data2 = MessageItemDataClass(senderLogin, message, "", "")
            messagesArrayList.add(data2)
        }
        messagesArrayAdapter.notifyDataSetChanged()
        chatItselfLV?.adapter = messagesArrayAdapter
        chatItselfLV?.setSelection(messagesArrayList.size - 1)
    }

    fun sendMessagesRequest() {
        if (messagesArrayList.isNotEmpty())
            messagesArrayList.clear()
        val response: List<JSONObject> = JasonSTATHAM().zapretParsinga(
            Requests().get(
                mapOf(
                    "sender" to Van,
                    "recipient" to Billy
                ),
                "http://192.168.1.107:8080/personal"
            )
        )
        for (el in response)
            updateMessageList(
                (el["sender"] as JSONObject)["username"].toString(),
                el["messageText"].toString()
            )
    }
}