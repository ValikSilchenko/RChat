package com.example.rchat.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.ListView
import com.example.rchat.MessageItemDataClass
import com.example.rchat.MessageItemLVAdapter
import com.example.rchat.PreviewChatDataClass
import com.example.rchat.PreviewChatLVAdapter

@SuppressLint("StaticFieldLeak")
object ChatSingleton {
    private var webSocketClient = WebSocketClient()
    private lateinit var chatItselfContext: Activity
    private lateinit var chatsWindowContext: Activity
    private var Billy = "Herrington" // Логин собеседника
    private var Arnold = "Shwarzenegger" // Логин атворизованного пользователя

    private lateinit var chatWindowLV: ListView
    private var chatItselfLV: ListView? = null
    var chatsArrayList: ArrayList<PreviewChatDataClass> = ArrayList()
    val messagesArrayList: ArrayList<MessageItemDataClass> = ArrayList()

    val chatArrayAdapter = PreviewChatLVAdapter(chatsWindowContext, chatsArrayList)
    val messagesArrayAdapter = MessageItemLVAdapter(chatItselfContext, messagesArrayList)

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

    fun clearMessageList() {
        messagesArrayList.clear()
        chatItselfLV?.adapter = messagesArrayAdapter
    }

    fun openConnection(username: String) {
        webSocketClient.connect("http://192.168.1.107:8080/ws", username)
    }

    fun processMessage(message: String) {
        val parsedMessage = JasonSTATHAM().parseMessage(message)

        updateChatList(parsedMessage[0], "", parsedMessage[1])

        if (chatItselfLV != null && parsedMessage[0] == Billy)
            updateMessageList(parsedMessage[0], parsedMessage[1])
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
        chatArrayAdapter.notifyDataSetChanged()
        chatWindowLV.adapter = chatArrayAdapter
    }

    fun updateMessageList(senderLogin: String, message: String) {
        if (senderLogin == Arnold) {
            val data1 = MessageItemDataClass("", "", senderLogin, message)
            messagesArrayList.add(data1)
            messagesArrayAdapter.notifyDataSetChanged()
            chatItselfLV?.adapter = messagesArrayAdapter
        }
        else {
            val data2 = MessageItemDataClass(senderLogin, message, "", "")
            messagesArrayList.add(data2)
            messagesArrayAdapter.notifyDataSetChanged()
            chatItselfLV?.adapter = messagesArrayAdapter
        }
//        chatItselfLV?.setSelection((chatItselfLV?.adapter?.count ?: 1) - 1)
    }
}