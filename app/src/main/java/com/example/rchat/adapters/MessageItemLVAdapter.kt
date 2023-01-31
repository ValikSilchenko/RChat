package com.example.rchat.adapters

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.rchat.R
import com.example.rchat.dataclasses.MessageItemDataClass
import com.example.rchat.utils.ChatSingleton

/* Класс-адаптер для единичного элемента - сообщения
*/
class MessageItemLVAdapter(
    private val context: Activity,
    private val arrayList: ArrayList<MessageItemDataClass>
) : ArrayAdapter<MessageItemDataClass>(context, R.layout.message_item, arrayList) {

    private lateinit var incomingLogin: TextView
    private lateinit var incomingMessage: TextView
    private lateinit var incomingContainer: LinearLayout
    private lateinit var incomingTime: TextView
    private lateinit var outgoingLogin: TextView
    private lateinit var outgoingMessage: TextView
    private lateinit var outgoingContainer: LinearLayout
    private lateinit var outgoingTime: TextView
    private lateinit var message: String
    private lateinit var messageSender: String
    private var messageId: Int = -1

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.message_item, null)

        incomingLogin = view.findViewById(R.id.MI_IncomingLoginTV)
        incomingMessage = view.findViewById(R.id.MI_IncomingMessageTV)
        incomingContainer = view.findViewById(R.id.MI_IncomingContainer)
        incomingTime = view.findViewById(R.id.MI_IncomingTimeTV)
        outgoingLogin = view.findViewById(R.id.MI_OutgoingLoginTV)
        outgoingMessage = view.findViewById(R.id.MI_OutgoingMessageTV)
        outgoingContainer = view.findViewById(R.id.MI_OutgoingContainer)
        outgoingTime = view.findViewById(R.id.MI_OutgoingTimeTV)

        incomingLogin.text = arrayList[position].incomingLogin
        incomingMessage.text = arrayList[position].incomingMessage
        incomingTime.text = arrayList[position].incomingTime
        outgoingLogin.text = arrayList[position].outgoingLogin
        outgoingMessage.text = arrayList[position].outgoingMessage
        outgoingTime.text = arrayList[position].outgoingTime
        messageId = arrayList[position].messageID
        messageSender = arrayList[position].messageSender

        /* Скрытие ненужных блоков в сообщении
        */
        // НИ В КОЕМ СЛУЧАЕ НЕ УБИРАТЬ ToString() - БЕЗ НЕГО НОРМАЛЬНО СООБЩЕНИЯ НЕ ВЫДЕЛЯЮТСЯ
        if (incomingMessage.text.toString() == "") {
            incomingContainer.visibility = View.GONE
            incomingLogin.visibility = View.GONE

        } else if (outgoingMessage.text.toString() == "") {
            outgoingContainer.visibility = View.GONE
            outgoingLogin.visibility = View.GONE
        }

        message = if (incomingMessage.text == "")
            outgoingMessage.text.toString()
        else
            incomingMessage.text.toString()

        /* Действия на долгое нажатие на сообщение
        */
        view.setOnLongClickListener {
            val popupMenu = PopupMenu(context, it)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete_message_item -> {   /* Удаление сообщения */
//                        if (messageSender == ChatSingleton.Van) {
////                            showAlertMenu(messageId)
//                        }
                        Toast.makeText(context, context.getString(R.string.wip_title), Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.edit_message_item -> {     /* Редактирование сообщения */
                        true
                    }
                    R.id.reply_message_item -> {    /* Ответ на сообщение */
                        Toast.makeText(
                            context,
                            "Reply message",
                            Toast.LENGTH_SHORT
                        ).show()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.inflate(R.menu.more_messages_menu)
            try {
                val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                fieldMPopup.isAccessible = true
                val mPopup = fieldMPopup.get(popupMenu)
                mPopup.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(mPopup, true)
            } catch (e: Exception) {
                Log.e("Main", "Error of showing popup menu icons")
            } finally {
                popupMenu.show()
            }
            true
        }
        return view
    }

    private fun showAlertMenu(messageID: Int) {
        val message: AlertDialog.Builder = AlertDialog.Builder(context)
        message
            .setTitle(context.getString(R.string.attention_title))
            .setMessage(context.getString(R.string.really_wanna_delete_chat_title))
            .setCancelable(true)
            .setPositiveButton(
                context.getString(R.string.yes_title)
            ) { _, _ ->
                deleteMessage(messageID)
            }
            .setNegativeButton(context.getString(R.string.no_title)) { dialog, _ ->
                dialog.cancel()
            }
        val messageWindow = message.create()
        messageWindow.show()
    }

    private fun deleteMessage(messageID: Int) {
        try {
            ChatSingleton.deleteMessageFromMessageList(messageID)
        } catch (exception: Exception) {
            Toast.makeText(
                context,
                "Error of deleting message",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /* Функция показа сообщения на экране
        Вызывается в объекте ChatSingleton в методе updateMessageList()
     */
    fun addMessage(senderLogin: String, message: String, time: String, messageID: Int) {
        ChatSingleton.addMessageToMessagesArray(senderLogin, message, time, messageID)
        notifyDataSetChanged()
    }
}