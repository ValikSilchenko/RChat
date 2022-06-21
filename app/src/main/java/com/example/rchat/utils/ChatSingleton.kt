package com.example.rchat.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.ListView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.rchat.*
import org.json.JSONObject

@SuppressLint("StaticFieldLeak")
object ChatSingleton {
    val CHANNEL_ID = "channel_id"
    val serverUrl = "http://194.87.248.192:8080"
    var isInChat = false

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
        setSelection()
    }

    fun setSelection() {
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
        webSocketClient.connect(username)
    }

    fun processMessage(message: String) {
        chatsWindowContext.runOnUiThread {
            val parsedMessage = JasonSTATHAM().parseMessage(message)
            updateChatList(parsedMessage[0], "", parsedMessage[1], "")
            if (parsedMessage[0] == Billy) {
                if (!isInChat) {
                    sendNotification(1, parsedMessage[0], parsedMessage[1])
                }
                updateMessageList(parsedMessage[0], parsedMessage[1])
                setSelection()
            } else
                sendNotification(1, parsedMessage[0], parsedMessage[1])
        }
    }

    fun sendMessage(recipientLogin: String, message: String) {
        //TODO("Обработка ошибки отправки сообщения")
        webSocketClient.send(recipientLogin, "$Van $message")
        updateMessageList(Van, message)
        updateChatList(recipientLogin, "", message, "You:")
    }

    fun updateChatList(recipientLogin: String, time: String, message: String, youTxt: String) {
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
            chatsArrayList[index].previewYouTxt = youTxt
        } else
            chatsArrayList.add(PreviewChatDataClass(recipientLogin, time, message, youTxt))

        chatArrayAdapter.notifyDataSetChanged()
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
                "$serverUrl/personal"
            )
        )
        for (el in response)
            updateMessageList(
                (el["sender"] as JSONObject)["username"].toString(),
                el["messageText"].toString()
            )
    }

    fun createNotifChannel(context: Context) {  // Для версий выше Орео
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, "notif_title", importance).apply {
                description = "notif description"
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(
        notifId: Int,
        loginTitle: String,
        messageText: String
    ) {    // Для версий ниже Орео
        val builder = NotificationCompat.Builder(chatsWindowContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(loginTitle)
            .setContentText(messageText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(chatsWindowContext)) {
            notify(notifId, builder.build())
        }
    }
}