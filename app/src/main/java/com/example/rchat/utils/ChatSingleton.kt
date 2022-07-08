package com.example.rchat.utils

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.widget.EditText
import android.widget.ListView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.rchat.R
import com.example.rchat.adapters.CGCLVAdapter
import com.example.rchat.adapters.MessageItemLVAdapter
import com.example.rchat.adapters.PreviewChatLVAdapter
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
    lateinit var chatsWindowActivity: Activity
    private lateinit var createGroupChatWindowActivity: Activity
    private lateinit var chatsArrayAdapter: PreviewChatLVAdapter
    private lateinit var messagesArrayAdapter: MessageItemLVAdapter
    private lateinit var createGroupChatArrayAdapter: CGCLVAdapter
    private lateinit var messageEditText: EditText
    private const val channel_ID = "new_messages"
    private const val description = "Messages Notifications"
    private val usersForGroupChatArrayList: ArrayList<CGCDataClass> = ArrayList()
    private val chatsArrayList: ArrayList<PreviewChatDataClass> = ArrayList()
    private val messagesArrayList: ArrayList<MessageItemDataClass> = ArrayList()
    private var notificationId = -2
    private var webSocketClient = WebSocketClient()
    private var chatsWindowLV: ListView? = null
    private var chatItselfLV: ListView? = null
    private var createGroupChatRV: ListView? = null
    var isInChat = false
    var Billy = "Herrington" // Логин собеседника
    var Van = "Darkholme" // Логин авторизованного пользователя

    private fun setSelection() {
        chatItselfLV?.setSelection(messagesArrayList.size - 1)
    }

    fun setChatsWindow(listView: ListView, username: String, incomingContext: Activity) {
        Van = username
        chatsWindowLV = listView
        chatsWindowActivity = incomingContext
        chatsArrayAdapter = PreviewChatLVAdapter(chatsWindowActivity, chatsArrayList)
        chatsWindowLV!!.adapter = chatsArrayAdapter
        createNotificationChannel()
        if (chatsArrayList.isNotEmpty())
            chatsArrayList.clear()
    }

    fun setChatItselfWindow(
        listView: ListView,
        username: String,
        incomingContext: Activity,
        editText: EditText
    ) {
        chatItselfLV = listView
        Billy = username
        chatItselfActivity = incomingContext
        messagesArrayAdapter = MessageItemLVAdapter(chatItselfActivity, messagesArrayList)
        chatItselfLV!!.adapter = messagesArrayAdapter
        messageEditText = editText
    }

    fun setCGCWindow(incomingContext: Activity, listView: ListView) {
        createGroupChatWindowActivity = incomingContext
        createGroupChatRV = listView
        createGroupChatArrayAdapter = CGCLVAdapter(createGroupChatWindowActivity, usersForGroupChatArrayList)
        createGroupChatRV!!.adapter = createGroupChatArrayAdapter
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
                setSelection()
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
            notificationId = userId

            if (sender == Van) {
                updateChatList(
                    (parsedMessage["recipient"] as JSONObject)["username"].toString(),
                    parsedMessage["time"].toString(),
                    messageText,
                    "Вы:",
                    !isInChat
                )
                updateMessageList(sender, messageText, "$date $time")
            } else {
                updateChatList(sender, parsedMessage["time"].toString(), messageText, "", !isInChat)
                if (sender == Billy) {
                    if (!isInChat) {
                        sendNotification(userId, sender, messageText)
                    }
                    updateMessageList(sender, messageText, "$date $time")
//                    setSelection()
                    chatItselfLV?.smoothScrollToPosition(messagesArrayList.size - 1)
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

        with(NotificationManagerCompat.from(chatsWindowActivity)) {
            builder.notification.flags = Notification.FLAG_AUTO_CANCEL
            notify(notificationId, builder.build())
        }
    }

    fun updateChatList(
        recipientLogin: String,
        time: String,
        message: String,
        youTxt: String,
        isNew: Boolean
    ) {
        var isInArray = false
        var index = 0
        for (el in chatsArrayList.indices) {
            if (chatsArrayList[el].previewLogin == recipientLogin) {
                isInArray = true
                index = el
                break
            }
        }
        if (isInArray)
            chatsArrayList.removeAt(index)
        chatsArrayList.add(0, PreviewChatDataClass(recipientLogin, time, message, youTxt, isNew))
        chatsArrayAdapter.notifyDataSetChanged()
    }

    fun updateUsersList(userLogin: String) {
        var isInArray = false
        for (el in usersForGroupChatArrayList.indices) {
            if (usersForGroupChatArrayList[el].login == userLogin) {
                isInArray = true
                break
            }
        }
        if (!isInArray)
            usersForGroupChatArrayList.add(CGCDataClass(userLogin))
        createGroupChatArrayAdapter.notifyDataSetChanged()
    }

    fun deleteUser(userLogin: String) {
        for (el in usersForGroupChatArrayList.indices) {
            if (usersForGroupChatArrayList[el].login == userLogin) {
                usersForGroupChatArrayList.removeAt(el)
                break
            }
        }
    }

    fun updateMessageList(senderLogin: String, message: String, time: String) {
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
                outgoingTime
            )
        )
        messagesArrayAdapter.notifyDataSetChanged()
//        setSelection()
        chatItselfLV?.smoothScrollToPosition(messagesArrayList.size - 1)
    }

    fun clearMessagesList() {
        if (messagesArrayList.isNotEmpty())
            messagesArrayList.clear()
    }
}