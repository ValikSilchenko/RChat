package com.example.rchat.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.rchat.R
import com.example.rchat.dataclasses.MessageItemDataClass

/* Класс=адаптер для единичного элемента - сообщения
*/
class MessageItemRVAdapter(private val arrayList: ArrayList<MessageItemDataClass>) :
    RecyclerView.Adapter<MessageItemRVAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val incomingLogin: TextView = itemView.findViewById(R.id.MI_IncomingLoginTV)
        val incomingMessage: TextView = itemView.findViewById(R.id.MI_IncomingMessageTV)
        val incomingContainer: LinearLayout = itemView.findViewById(R.id.MI_IncomingContainer)
        val incomingTime: TextView = itemView.findViewById(R.id.MI_IncomingTimeTV)
        val outgoingLogin: TextView = itemView.findViewById(R.id.MI_OutgoingLoginTV)
        val outgoingMessage: TextView = itemView.findViewById(R.id.MI_OutgoingMessageTV)
        val outgoingContainer: LinearLayout = itemView.findViewById(R.id.MI_OutgoingContainer)
        val outgoingTime: TextView = itemView.findViewById(R.id.MI_OutgoingTimeTV)

        /* Действия на долгое нажатие на сообщение
        */
        init {
            itemView.setOnLongClickListener {
                val popupMenu = PopupMenu(itemView.context, it)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.delete_message_item -> {
                            // Удаление сообщения
                            Toast.makeText(
                                itemView.context,
                                "Delete message",
                                Toast.LENGTH_SHORT
                            ).show()
                            true
                        }
                        R.id.edit_message_item -> {
                            // Редактирование сообщения
                            Toast.makeText(
                                itemView.context,
                                "Edit message",
                                Toast.LENGTH_SHORT
                            ).show()
                            true
                        }
                        R.id.reply_message_item -> {
                            // Ответ на сообщение
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
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}