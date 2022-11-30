package com.example.rchat.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.rchat.R
import com.example.rchat.dataclasses.MessageItemDataClass
import com.example.rchat.utils.ChatSingleton

/* Класс-адаптер для единичного элемента - сообщения
*/
class MessageItemRVAdapter(private var arrayList: ArrayList<MessageItemDataClass>) :
    RecyclerView.Adapter<MessageItemRVAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val incomingLogin: TextView = itemView.findViewById(R.id.MI_IncomingLoginTV)
        val incomingMessage: TextView = itemView.findViewById(R.id.MI_IncomingMessageTV)
        val incomingTime: TextView = itemView.findViewById(R.id.MI_IncomingTimeTV)
        val outgoingLogin: TextView = itemView.findViewById(R.id.MI_OutgoingLoginTV)
        val outgoingMessage: TextView = itemView.findViewById(R.id.MI_OutgoingMessageTV)
        val outgoingTime: TextView = itemView.findViewById(R.id.MI_OutgoingTimeTV)
        val incomingContainer: LinearLayout = itemView.findViewById(R.id.MI_IncomingContainer)
        val outgoingContainer: LinearLayout = itemView.findViewById(R.id.MI_OutgoingContainer)
        var messageId = -1
        var messageSender = ""
        var message = ""

        init {
            itemView.setOnLongClickListener {
                val popupMenu = PopupMenu(itemView.context, it)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.delete_message_item -> {   /* Удаление сообщения */
//                        if (messageSender == ChatSingleton.Van) {
////                            showAlertMenu(messageId)
//                        }
                            true
                        }
                        R.id.edit_message_item -> {     /* Редактирование сообщения */
                            true
                        }
                        R.id.reply_message_item -> {    /* Ответ на сообщение */
                            Toast.makeText(
                                itemView.context,
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
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
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
            // НЕ ЛЕЗЬ БЛЯТЬ ДЕБИЛ СУКА ЕБАНЫЙ
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

        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    private fun showAlertMenu(messageID: Int, context: Context) {
        val message: AlertDialog.Builder = AlertDialog.Builder(context)
        message
            .setTitle(context.getString(R.string.attention_title))
            .setMessage(context.getString(R.string.really_wanna_delete_chat_title))
            .setCancelable(true)
            .setPositiveButton(
                context.getString(R.string.yes_title)
            ) { _, _ ->
                deleteMessage(messageID, context)
            }
            .setNegativeButton(context.getString(R.string.no_title)) { dialog, _ ->
                dialog.cancel()
            }
        val messageWindow = message.create()
        messageWindow.show()
    }

    private fun deleteMessage(messageID: Int, context: Context) {
        try {
            ChatSingleton.deleteMessageFromMessageList(messageID)
        } catch (exception: Exception) {
            Toast.makeText(
                context,
                "Something went wrong, we don't give a fuck",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun addMessage(senderLogin: String, message: String, time: String, messageID: Int) {
        ChatSingleton.addMessageToMessagesArray(senderLogin, message, time, messageID)
        notifyItemInserted(ChatSingleton.getMessagesArraySize() - 1)
    }
}