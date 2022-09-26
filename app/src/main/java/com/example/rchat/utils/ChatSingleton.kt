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
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
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

/* Утилитный класс (объект) основной логики приложения
*/
@SuppressLint("StaticFieldLeak")
object ChatSingleton {
    const val serverUrl = "http://194.87.248.192:8080"
    private lateinit var messageEditText: EditText
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var chatItselfActivity: Activity
    private lateinit var chatsWindowActivity: Activity
    private lateinit var chatsArrayAdapter: PreviewChatRVAdapter
    private lateinit var messagesArrayAdapter: MessageItemLVAdapter
    private lateinit var noChatsText: TextView
    private const val channel_ID = "new_messages"
    private const val description = "Messages Notifications"
    private var webSocketClient = WebSocketClient()
    private var chatsWindowRV: RecyclerView? = null
    private var chatItselfRV: ListView? = null
    private val usersArrayList: ArrayList<CGCDataClass> = ArrayList()
    private val messagesArrayList: ArrayList<MessageItemDataClass> = ArrayList()
    val chatsArrayList: ArrayList<PreviewChatDataClass> = ArrayList()
    var isInChat = false
    var isNotificationOn = true
    var Billy = "Herrington" // Логин собеседника
    var Van = "Darkholme" // Логин авторизованного пользователя
    private var cPackageName = ""
    var chatName = ""
    var chatID = -1

    /* Сеттер окна чатов
        Вызывается в ChatsWindow.kt в методе onCreate()
     */
    fun setChatsWindow(
        recView: RecyclerView,
        username: String,
        incomingContext: Activity,
        pName: String,
        noChatsTxt: TextView
    ) {
        Van = username
        chatsWindowRV = recView
        chatsWindowActivity = incomingContext
        chatsArrayAdapter = PreviewChatRVAdapter(chatsArrayList)
        chatsWindowRV!!.layoutManager = LinearLayoutManager(chatsWindowActivity)
        chatsWindowRV!!.adapter = chatsArrayAdapter
        cPackageName = pName
        noChatsText = noChatsTxt
        createNotificationChannel()
    }

    /* Сеттер окна самого чата
        Вызывается в ChatItselfWindow.kt в методе onCreate()
     */
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

    /* Функция открытия соединения с сокетом
        Вызывается в BackgroundService.kt в методе openCloseConnection()
     */
    fun openConnection(username: String) {
        webSocketClient.connect(username)
    }

    /* Функция закрытия соединения с сокетом
        Вызывается в BackgroundService.kt в методе openCloseConnection() и в методе onDestroy()
     */
    fun closeConnection() {
        webSocketClient.disconnect()
    }

    /* Функция отправки сообщения
        Вызывается в ChatItselfWindow.kt в методе onCreate() при нажатии кнопки отправки сообщения
     */
    fun sendMessage(recipientLogin: String, message: String, messageField: EditText) {
        try {
            webSocketClient.sendMessage(recipientLogin, message, Van)
            messageField.text = null
        } catch (exception: Exception) {
            ChatFunctions().showMessage(
                chatItselfActivity.getString(R.string.error_title),
                "${chatItselfActivity.getString(R.string.error_sending_message_title)} ${exception.message}",
                chatItselfActivity
            )
        }
    }

    /* Функция получения и последующей обработки сообщения
        Вызывается в WebSocketClient.kt в методе connect()
     */
    // РЕФАКТОРИНГ ТУТ ЗАПРЕЩЕН БЛЯТЬ
    // ИДЕ ВЫРУБАЙ НАХУЙ
    fun processMessage(message: Map<*, *>) {
        chatsWindowActivity.runOnUiThread {
            val parsedMessage = JSONObject(message)
            if (parsedMessage.has("deleted")) {
                for (el in messagesArrayList.indices) {
                    if (messagesArrayList[el].messageID == (parsedMessage["deleted"] as String).toInt()) {
                        messagesArrayList.removeAt(el)
                        messagesArrayAdapter.notifyDataSetChanged()
                        if (isMessagesArrayEmpty()) {
                            for (ell in chatsArrayList.indices) {
                                if (chatsArrayList[ell].chatId == chatID) {
                                    deleteChatFromChatList(ell)
                                    break
                                }
                            }
                        } else if (el == messagesArrayList.size) {
                            if (messagesArrayList[el - 1].incomingLogin == Billy) {
                                updateChatList(
                                    Billy,
                                    messagesArrayList[el - 1].incomingTime.substring(11),
                                    messagesArrayList[el - 1].incomingMessage,
                                    "",
                                    chatID,
                                    0
                                )
                            } else {
                                updateChatList(
                                    Billy,
                                    messagesArrayList[el - 1].outgoingTime.substring(11),
                                    messagesArrayList[el - 1].outgoingMessage,
                                    chatsWindowActivity.getString(R.string.you_title),
                                    chatID,
                                    0
                                )
                            }
                        }
                        return@runOnUiThread
                    }
                }
            }
            val sender = (parsedMessage["sender"] as JSONObject)["username"].toString()
            val messageText = parsedMessage["messageText"].toString()
            val userId = (parsedMessage["sender"] as JSONObject)["id"] as Int
            val time = parsedMessage["time"].toString()
            val date = parsedMessage["date"].toString()
            val messageID = parsedMessage["id"] as Int
            val unreadMsgCount: Int
            if (parsedMessage["read"] as Boolean)
                return@runOnUiThread
            if (sender == Van) {
                updateChatList(
                    (parsedMessage["recipient"] as JSONObject)["username"].toString(),
                    parsedMessage["time"].toString(),
                    messageText,
                    chatsWindowActivity.getString(R.string.you_title),
                    userId,
                    0
                )
                updateMessageList(sender, messageText, "$date $time", messageID)
                focusOnLastItem(0)
            } else {
                unreadMsgCount = Requests().get(
                    mapOf("sender" to sender, "recipient" to Van),
                    "$serverUrl/count"
                ).toInt()
                updateChatList(
                    sender,
                    parsedMessage["time"].toString(),
                    messageText,
                    "",
                    userId,
                    unreadMsgCount
                )
                if (sender == Billy) {
                    if (!isInChat) {
                        sendNotification(userId, sender, messageText)
                    }
                    updateMessageList(sender, messageText, "$date $time", messageID)
                    focusOnLastItem(unreadMsgCount)
                } else
                    sendNotification(userId, sender, messageText)
            }
        }
    }

    /* Функция создания канала для получения уведомлений от чатов
        Вызывается в этом объекте в функции setChatsWindow()
     */
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

    /* Функция показа уведомлений от чатов
        Вызывается в этом объекте в функции processMessage()
     */
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun sendNotification(notificationId: Int, loginTitle: String, messageText: String) {
        if (isNotificationOn) {
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
                .setContentText(messageText)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
            notificationManager.notify(notificationId, builder.build())
        }
    }

    /* Функция удаления всех уведомлений
        Вызывается в SettingsWindow.kt в методе exitAccount()
     */
    fun deleteNotification() {
        notificationManager.cancelAll()
    }

    /* Функция добавления нового чата в список чатов
        Вызывается в этом объекте в функции processMessage() и в ChatsWindow.kt в методе onCreate() при получении ответа на запрос на список чатов
     */
    fun updateChatList(
        lastMessageRecipient: String,
        time: String,
        message: String,
        youTxt: String,
        chatId: Int,
        unreadMsgCount: Int
    ) {
        for (el in chatsArrayList.indices) {
            if (chatsArrayList[el].login == lastMessageRecipient) {
                deleteChatFromChatList(el)
                break
            }
        }
        chatsArrayList.add(
            0,
            PreviewChatDataClass(
                lastMessageRecipient,
                time,
                message,
                youTxt,
                unreadMsgCount,
                chatId
            )
        )
        chatsArrayAdapter.notifyItemInserted(0)
        noChatsText.visibility = View.GONE
    }

    /* Функция добавления нового сообщения в список сообщений
        Вызывается в этом объекте в функции processMessage() и в ChatItselfWindow.kt в методе onCreate()
     */
    fun updateMessageList(senderLogin: String, message: String, time: String, messageID: Int) {
        if (senderLogin == Van) {
            messagesArrayList.add(
                MessageItemDataClass(
                    "",
                    "",
                    "",
                    senderLogin,
                    message,
                    time,
                    messageID
                )
            )
        } else {
            messagesArrayList.add(
                MessageItemDataClass(
                    senderLogin,
                    message,
                    time,
                    "",
                    "",
                    "",
                    messageID
                )
            )
        }
        messagesArrayAdapter.notifyDataSetChanged()
    }

    /* Функция очистки списка сообщений
        Вызывается в ChatItselfWindow.kt в методе closeChatItselfWindow()
     */
    fun clearMessagesList() {
        if (messagesArrayList.isNotEmpty()) {
            messagesArrayList.clear()
        }
    }

    /* Функция очистки списка чатов
        Вызывается в SettingsWindow.kt в методе exitAccount()
     */
    fun clearChatsList() {
        if (chatsArrayList.isNotEmpty()) {
            chatsArrayList.clear()
        }
    }

    /* Функция фокусировки на последнем сообщении
        Вызывается в этом объекте в функции processMessage() и в ChatItselfWindow.kt в методе onCreate() после получения всех сообщений из запроса
     */
    fun focusOnLastItem(unreadCount: Int) {
        chatItselfRV?.setSelection(messagesArrayList.size - 1 - unreadCount)
    }

    /* Функция отправки запроса на пометку сообщения прочитанным
        Вызывается в ChatItselfWindow.kt в методе onCreate() после получения всех сообщений из запроса
     */
    fun sendRequestForReading(sender: String, messageID: Int) {
        webSocketClient.send(Van, sender, messageID)
    }

    /* Функция добавления пользователя в список для беседы
        Вызывается в CreateGroupChatRVAdapter.kt в методе init
     */
    fun addUserForGroupChat(userName: String) {
        usersArrayList.add(CGCDataClass(userName))
    }

    /* Функция удаления пользователя из списка для беседы
        Вызывается в CreateGroupChatRVAdapter.kt в методе init
     */
    fun deleteUserFromGroupChatArray(userName: String) {
        for (el in usersArrayList.indices) {
            if (usersArrayList[el].login == userName) {
                usersArrayList.removeAt(el)
                break
            }
        }
    }

    /* Функция удаления чата по его позиции в списке чатов
        Вызывается в PreviewChatRVAdapter.kt при удалении чата
     */
    fun deleteChatFromChatList(chatPosition: Int) {
        chatsArrayList.removeAt(chatPosition)
        chatsArrayAdapter.notifyItemRemoved(chatPosition)
        if (chatsArrayList.isEmpty()) {
            noChatsText.visibility = View.VISIBLE
        }
    }

    /* Функция удаления сообщения
        Вызывается в MessageItemLVAdapter.kt при удалении сообщения
     */
    fun deleteMessageFromMessageList(messageID: Int) {
        webSocketClient.deleteMessage(messageID, Van, Billy)
    }

    /* Функция проверки, пустой ли массив сообщений
        Вызывается в MessageItemLVAdapter.kt при удалении сообщения
     */
    private fun isMessagesArrayEmpty(): Boolean {
        return messagesArrayList.isEmpty()
    }

    /* Функция закрытия окна чатов
        Вызывается в ChatItselfWindow.kt в методе closeChatItselfWindow() и в MessageItemLVAdapter при удалении сообщения
     */
    fun closeChatWindow() {
        isInChat = false
        Billy = "Herrington"
        chatName = ""
        chatID = -1
        clearMessagesList()
    }
}