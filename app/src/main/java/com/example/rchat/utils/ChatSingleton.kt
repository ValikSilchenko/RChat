package com.example.rchat.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
    private const val CHANNEL_ID = "channel_id"
    const val serverUrl = "http://194.87.248.192:8080"
    var isInChat = false
    lateinit var chatName: String

    private lateinit var chatItselfActivity: Activity
    private lateinit var chatsWindowActivity: Activity
    private lateinit var cgcWindowActivity: Activity
    private lateinit var chatArrayAdapter: PreviewChatLVAdapter
    private lateinit var messagesArrayAdapter: MessageItemLVAdapter
    private lateinit var cgcArrayAdapter: CGCLVAdapter
    private lateinit var messageEditText: EditText
    private var notificationId = -2
    var Billy = "Herrington" // Логин собеседника
    var Van = "Darkholme" // Логин авторизованного пользователя
    private var webSocketClient = WebSocketClient()
    private var chatWindowLV: ListView? = null
    private var chatItselfLV: ListView? = null
    private var cgcWindowLV: ListView? = null
    val chatsArrayList: ArrayList<PreviewChatDataClass> = ArrayList()
    val messagesArrayList: ArrayList<MessageItemDataClass> = ArrayList()
    private val cgcArrayList: ArrayList<CGCDataClass> = ArrayList()

    fun setSelection() {
        chatItselfLV?.setSelection(messagesArrayList.size - 1)
    }

    fun setChatsWindow(listView: ListView, username: String, incomingContext: Activity) {
        Van = username
        chatWindowLV = listView
        chatsWindowActivity = incomingContext
        chatArrayAdapter = PreviewChatLVAdapter(chatsWindowActivity, chatsArrayList)
        chatWindowLV!!.adapter = chatArrayAdapter
        createNotificationChannel()
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
        cgcWindowActivity = incomingContext
        cgcWindowLV = listView
        cgcArrayAdapter = CGCLVAdapter(cgcWindowActivity, cgcArrayList)
        cgcWindowLV!!.adapter = cgcArrayAdapter
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
                    "Вы:"
                )
                updateMessageList(sender, messageText, "$date $time")
            } else {
                updateChatList(sender, parsedMessage["time"].toString(), messageText, "")
                if (sender == Billy) {
                    if (!isInChat) {
                        sendNotification(userId, sender, messageText)
                    }
                    updateMessageList(sender, messageText, "$date $time")
                    setSelection()
                } else
                    sendNotification(userId, sender, messageText)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, "notification_title", importance).apply {
                description = "notification description"
            }
            val notificationManager =
                chatsWindowActivity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(notificationId: Int, loginTitle: String, messageText: String) {
        val intent = Intent(chatsWindowActivity, ChatItselfWindow::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        chatName = loginTitle
        val pendingIntent = PendingIntent.getActivity(
            chatsWindowActivity,
            0,
            intent,
            0
        )
        val builder = NotificationCompat.Builder(chatsWindowActivity, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(loginTitle)
            .setContentIntent(pendingIntent)
            .setContentText(messageText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(chatsWindowActivity)) {
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
        if (isInArray)
            chatsArrayList.removeAt(index)
        chatsArrayList.add(0, PreviewChatDataClass(recipientLogin, time, message, youTxt))
        chatArrayAdapter.notifyDataSetChanged()
    }

    fun updateUsersList(userLogin: String) {
        var isInArray = false
        for (el in cgcArrayList.indices) {
            if (cgcArrayList[el].login == userLogin) {
                isInArray = true
                break
            }
        }
        if (!isInArray)
            cgcArrayList.add(CGCDataClass(userLogin))
        cgcArrayAdapter.notifyDataSetChanged()
    }

    fun deleteUser(userLogin: String) {
        for (el in cgcArrayList.indices) {
            if (cgcArrayList[el].login == userLogin) {
                cgcArrayList.removeAt(el)
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
        setSelection()
    }

    fun editMessage(message: String) {
        messageEditText.setText(message)
    }
}