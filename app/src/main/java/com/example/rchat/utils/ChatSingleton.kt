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

    private lateinit var chatItselfContext: Activity
    private lateinit var chatsWindowContext: Activity
    private lateinit var chatWindowLV: ListView
    private lateinit var chatArrayAdapter: PreviewChatLVAdapter
    private lateinit var messagesArrayAdapter: MessageItemLVAdapter
    private var Billy = "Herrington" // Логин собеседника
    private var Van = "Darkholme" // Логин авторизованного пользователя
    private var webSocketClient = WebSocketClient()
    private var chatItselfLV: ListView? = null
    private var chatsArrayList: ArrayList<PreviewChatDataClass> = ArrayList()
    private val messagesArrayList: ArrayList<MessageItemDataClass> = ArrayList()

    private fun updateMessageList(senderLogin: String, message: String) {
        if (senderLogin == Van) {
            messagesArrayList.add(MessageItemDataClass("", "", senderLogin, message))
        } else {
            messagesArrayList.add(MessageItemDataClass(senderLogin, message, "", ""))
        }
        messagesArrayAdapter.notifyDataSetChanged()
        chatItselfLV?.setSelection(messagesArrayList.size - 1)
    }

    fun setChatsWindow(listView: ListView, username: String, incomingContext: Activity) {
        chatWindowLV = listView
        chatsWindowContext = incomingContext
        Van = username
        chatArrayAdapter = PreviewChatLVAdapter(chatsWindowContext, chatsArrayList)
        chatWindowLV.adapter = chatArrayAdapter
    }

    fun setChatItselfWindow(listView: ListView, username: String, incomingContext: Activity) {
        chatItselfLV = listView
        Billy = username
        chatItselfContext = incomingContext
        messagesArrayAdapter = MessageItemLVAdapter(chatItselfContext, messagesArrayList)
        chatItselfLV!!.adapter = messagesArrayAdapter
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
        chatsWindowContext.runOnUiThread {
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
        println("1")
        for (el in chatsArrayList.indices) {
            if (chatsArrayList[el].previewLogin == recipientLogin) {
                isInArray = true
                index = el
                break
            }
        }
        println("2")
        if (isInArray) {
            chatsArrayList[index].previewMessage = message
        } else {
            chatsArrayList.add(PreviewChatDataClass(recipientLogin, time, message))
        }
        println("3")
        chatArrayAdapter.notifyDataSetChanged()
        println("4")
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