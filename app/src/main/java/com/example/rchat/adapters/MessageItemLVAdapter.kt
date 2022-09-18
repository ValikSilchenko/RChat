package com.example.rchat.adapters

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.rchat.R
import com.example.rchat.dataclasses.MessageItemDataClass

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
        val msgId = arrayList[position].msgId

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

        /* Действия на долгое нажатие на сообщение
        */
        view.setOnLongClickListener {
            val popupMenu = PopupMenu(context, it)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete_message_item -> {   /* Удаление сообщения */
                        // Удаление сообщения
                        Toast.makeText(
                            context,
                            "Delete message",
                            Toast.LENGTH_SHORT
                        ).show()
                        true
                    }
                    R.id.edit_message_item -> {     /* Редактирование сообщения */
                        // Редактирование сообщения
                        true
                    }
                    R.id.reply_message_item -> {    /* Ответ на сообщение */
                        // Ответ на сообщение
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
}