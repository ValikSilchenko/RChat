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
    lateinit var chatName: String

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

    fun setSelection() {
        chatItselfLV?.setSelection(messagesArrayList.size - 1)
    }

    fun setChatsWindow(listView: ListView, username: String, incomingContext: Activity) {
        Van = username
        chatWindowLV = listView
        chatsWindowContext = incomingContext
        chatArrayAdapter = PreviewChatLVAdapter(chatsWindowContext, chatsArrayList)
        chatWindowLV.adapter = chatArrayAdapter
        createNotificationChannel()
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

    fun closeConnection() {
        webSocketClient.disconnect()
    }

    fun sendMessage(recipientLogin: String, message: String) {
        //TODO("Обработка ошибки отправки сообщения")
        webSocketClient.send(recipientLogin, message, Van)
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

    fun processMessage(message: Map<*, *>) {
        chatsWindowContext.runOnUiThread {
            val parsedMessage = JSONObject(message)
            val sender = (parsedMessage["sender"] as JSONObject)["username"].toString()
            val messageText = parsedMessage["messageText"].toString()
            val userId = (parsedMessage["sender"] as JSONObject)["id"] as Int

            if (sender == Van) {
                updateChatList(
                    (parsedMessage["recipient"] as JSONObject)["username"].toString(),
                    parsedMessage["time"].toString(),
                    messageText,
                    "You:"
                )
                updateMessageList(sender, messageText)
            } else {
                updateChatList(sender, parsedMessage["time"].toString(), messageText, "")
                if (sender == Billy) {
                    if (!isInChat) {
                        sendNotification(userId, sender, messageText)
                    }
                    updateMessageList(sender, messageText)
                    setSelection()
                } else
                    sendNotification(userId, sender, messageText)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, "notif_title", importance).apply {
                description = "notif description"
            }
            val notificationManager =
                chatsWindowContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(
        notificationId: Int,
        loginTitle: String,
        messageText: String
    ) {
//        val intent = Intent(chatsWindowContext, ChatItselfWindow::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//        chatName = loginTitle
//        val pendingIntent = PendingIntent.getActivity(chatsWindowContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(chatsWindowContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(loginTitle)
//            .setContentIntent(pendingIntent)
            .setContentText(messageText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(chatsWindowContext)) {
            notify(notificationId, builder.build())
        }
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
            chatsArrayList[index].previewTime = time
        } else
            chatsArrayList.add(PreviewChatDataClass(recipientLogin, time, message, youTxt))

        chatArrayAdapter.notifyDataSetChanged()
    }

    private fun updateMessageList(senderLogin: String, message: String) {
        if (senderLogin == Van)
            messagesArrayList.add(MessageItemDataClass("", "", senderLogin, message))
        else
            messagesArrayList.add(MessageItemDataClass(senderLogin, message, "", ""))

        messagesArrayAdapter.notifyDataSetChanged()
        setSelection()
    }
}