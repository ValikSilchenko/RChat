package com.example.rchat.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.widget.EditText
import android.widget.ListView
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rchat.R
import com.example.rchat.adapters.MessageItemLVAdapter
import com.example.rchat.adapters.PreviewChatRVAdapter
import com.example.rchat.dataclasses.CGCDataClass
import com.example.rchat.dataclasses.MessageItemDataClass
import com.example.rchat.dataclasses.PreviewChatDataClass
import com.example.rchat.windows.ChatItselfWindow
import org.json.JSONObject

@SuppressLint("StaticFieldLeak")
object ChatSingleton {
    const val serverUrl = "http://194.87.248.192:8080"
    const val ImgRequestCode = 100
    lateinit var chatName: String
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var chatItselfActivity: Activity
    private lateinit var chatsWindowActivity: Activity
    private lateinit var chatsArrayAdapter: PreviewChatRVAdapter
    private lateinit var messagesArrayAdapter: MessageItemLVAdapter
    private lateinit var messageEditText: EditText
    private const val channel_ID = "new_messages"
    private const val description = "Messages Notifications"
    private val messagesArrayList: ArrayList<MessageItemDataClass> = ArrayList()
    private var webSocketClient = WebSocketClient()
    private var chatsWindowRV: RecyclerView? = null
    private var chatItselfRV: ListView? = null
    private var usersArrayList: ArrayList<CGCDataClass> = ArrayList()
    val chatsArrayList: ArrayList<PreviewChatDataClass> = ArrayList()
    var isInChat = false
    var Billy = "Herrington"
    var Van = "Darkholme"

    fun setChatsWindow(recView: RecyclerView, username: String, incomingContext: Activity) {
        Van = username
        chatsWindowRV = recView
        chatsWindowActivity = incomingContext
        chatsArrayAdapter = PreviewChatRVAdapter(chatsArrayList)
        chatsWindowRV!!.layoutManager = LinearLayoutManager(chatsWindowActivity)
        chatsWindowRV!!.adapter = chatsArrayAdapter
        createNotificationChannel()
    }

    fun setChatItselfWindow(
        recView: ListView,
        username: String,
        incomingContext: Activity,
        editText: EditText
    ) {
        chatItselfRV = recView
        Billy = username
        chatItselfActivity = incomingContext
        messagesArrayAdapter = MessageItemLVAdapter(chatItselfActivity, messagesArrayList)
        chatItselfRV!!.adapter = messagesArrayAdapter
        messageEditText = editText
    }

    fun openConnection(username: String) {
        webSocketClient.connect(username)
    }

    fun closeConnection() {
        webSocketClient.disconnect()
    }

    fun sendMessage(recipientLogin: String, message: String, messageField: EditText) {
        try {
            webSocketClient.send(recipientLogin, message, Van)
            messageField.text = null
//            focusOnLastItem(0)
        } catch (exception: Exception) {
            ChatFunctions().showMessage(
                "Ошибка",
                "Ошибка отправки сообщения. Код: ${exception.message}", chatItselfActivity
            )
        }
    }

    fun processMessage(message: Map<*, *>) {
        chatsWindowActivity.runOnUiThread {
            val parsedMessage = JSONObject(message)
            val sender = (parsedMessage["sender"] as JSONObject)["username"].toString()
            val messageText = parsedMessage["messageText"].toString()
            val userId = (parsedMessage["sender"] as JSONObject)["id"] as Int
            val time = parsedMessage["time"].toString()
            val date = parsedMessage["date"].toString()
            val msgId = parsedMessage["id"] as Int
            val unreadMsg: Int

            if (parsedMessage["read"] as Boolean)
                return@runOnUiThread
            if (sender == Van) {
                updateChatList(
                    (parsedMessage["recipient"] as JSONObject)["username"].toString(),
                    parsedMessage["time"].toString(),
                    messageText,
                    "Вы:",
                    userId,
                    0
                )
                updateMessageList(sender, messageText, "$date $time", msgId)
                focusOnLastItem(0)
            } else {
                unreadMsg = Requests().get(
                    mapOf("sender" to sender, "recipient" to Van),
                    "$serverUrl/count"
                ).toInt()
                updateChatList(
                    sender,
                    parsedMessage["time"].toString(),
                    messageText,
                    "",
                    userId,
                    unreadMsg
                )
                if (sender == Billy) {
                    if (!isInChat) {
                        sendNotification(userId, sender, messageText)
                    }
                    updateMessageList(sender, messageText, "$date $time", msgId)
                    focusOnLastItem(unreadMsg)
                } else
                    sendNotification(userId, sender, messageText)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager =
                chatsWindowActivity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationChannel =
                NotificationChannel(channel_ID, description, NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.apply {
                enableLights(true)
                lightColor = Color.WHITE
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun sendNotification(notificationId: Int, loginTitle: String, messageText: String) {
        val intent = Intent(chatsWindowActivity, ChatItselfWindow::class.java)
        chatName = loginTitle
        val pendingIntent = PendingIntent.getActivity(
            chatsWindowActivity,
            0,
            intent,
            0
        )
        val builder = NotificationCompat.Builder(chatsWindowActivity, channel_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(loginTitle)
            .setContentIntent(pendingIntent)
            .setContentText(messageText)
            .setAutoCancel(true)
        notificationManager.notify(notificationId, builder.build())
    }

    fun deleteNotification() {
        notificationManager.cancelAll()
    }

    fun updateChatList(
        lastMessageRecipient: String,
        time: String,
        message: String,
        youTxt: String,
        chatId: Int,
        unreadMsg: Int
    ) {
        for (el in chatsArrayList.indices) {
            if (chatsArrayList[el].login == lastMessageRecipient) {
                chatsArrayList.removeAt(el)
                chatsArrayAdapter.notifyItemRemoved(el)
                break
            }
        }
        chatsArrayList.add(
            0,
            PreviewChatDataClass(lastMessageRecipient, time, message, youTxt, unreadMsg, chatId)
        )
        chatsArrayAdapter.notifyItemInserted(0)
    }

    fun updateMessageList(senderLogin: String, message: String, time: String, msgId: Int) {
        val incomingLogin: String
        val incomingMessage: String
        val incomingTime: String
        val outgoingLogin: String
        val outgoingMessage: String
        val outgoingTime: String

        if (senderLogin == Van) {
            incomingLogin = ""
            incomingMessage = ""
            incomingTime = ""
            outgoingLogin = senderLogin
            outgoingMessage = message
            outgoingTime = time
        } else {
            incomingLogin = senderLogin
            incomingMessage = message
            incomingTime = time
            outgoingLogin = ""
            outgoingMessage = ""
            outgoingTime = ""
        }
        messagesArrayList.add(
            MessageItemDataClass(
                incomingLogin,
                incomingMessage,
                incomingTime,
                outgoingLogin,
                outgoingMessage,
                outgoingTime,
                msgId
            )
        )
        messagesArrayAdapter.notifyDataSetChanged()
    }

    fun clearMessagesList() {
        if (messagesArrayList.isNotEmpty()) {
            messagesArrayList.clear()
            messagesArrayAdapter.notifyDataSetChanged()
        }
    }

    fun focusOnLastItem(unreadCount: Int) {
        chatItselfRV?.setSelection(messagesArrayList.size - 1 - unreadCount)
    }

    fun sendRequestForReading(sender: String, msgId: Int) {
        webSocketClient.send(Van, sender, msgId)
    }

    fun addUserForGroupChat(userName: String) {
        usersArrayList.add(CGCDataClass(userName))
    }

    fun deleteUserFromGroupChatArray(userName: String) {
        for (el in usersArrayList.indices) {
            if (usersArrayList[el].login == userName) {
                usersArrayList.removeAt(el)
                break
            }
        }
    }
}