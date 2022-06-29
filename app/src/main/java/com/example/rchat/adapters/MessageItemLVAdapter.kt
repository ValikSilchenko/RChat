package com.example.rchat.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.rchat.R
import com.example.rchat.dataclasses.MessageItemDataClass

class MessageItemLVAdapter(
    private val context: Activity,
    private val arrayList: ArrayList<MessageItemDataClass>
) : ArrayAdapter<MessageItemDataClass>(context, R.layout.message_item, arrayList) {

    lateinit var incomingLogin: TextView
    lateinit var incomingMessage: TextView
    lateinit var incomingContainer: LinearLayout
    lateinit var incomingTime: TextView
    lateinit var outgoingLogin: TextView
    lateinit var outgoingMessage: TextView
    lateinit var outgoingContainer: LinearLayout
    lateinit var outgoingTime: TextView

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.message_item, null)

        incomingLogin = view.findViewById(R.id.MI_IncomingLogin)
        incomingMessage = view.findViewById(R.id.MI_IncomingMessage)
        incomingContainer = view.findViewById(R.id.MI_IncomingContainer)
        incomingTime = view.findViewById(R.id.Message_IncomingTime)
        outgoingLogin = view.findViewById(R.id.MI_OutgoingLogin)
        outgoingMessage = view.findViewById(R.id.MI_OutgoingMessage)
        outgoingContainer = view.findViewById(R.id.MI_OutgoingContainer)
        outgoingTime = view.findViewById(R.id.MI_OutgoingTime)

        incomingLogin.text = arrayList[position].incomingLogin
        incomingMessage.text = arrayList[position].incomingMessage
        incomingTime.text = arrayList[position].incomingTime
        outgoingLogin.text = arrayList[position].outgoingLogin
        outgoingMessage.text = arrayList[position].outgoingMessage
        outgoingTime.text = arrayList[position].outgoingTime

        if (incomingMessage.text == "") {
            incomingContainer.visibility = View.GONE
            incomingLogin.visibility = View.GONE

        } else if (outgoingMessage.text == "") {
            outgoingContainer.visibility = View.GONE
            outgoingLogin.visibility = View.GONE
        }

        view.setOnLongClickListener {
            val popupMenu = PopupMenu(context, it)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete_message_item -> {
                        // Удаление сообщения
                        Toast.makeText(
                            context,
                            "Delete message",
                            Toast.LENGTH_SHORT
                        ).show()
                        true
                    }
                    R.id.edit_message_item -> {
                        // Редактирование сообщения
                        Toast.makeText(
                            context,
                            "Edit message",
                            Toast.LENGTH_SHORT
                        ).show()
                        true
                    }
                    R.id.reply_message_item -> {
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
            popupMenu.show()
            true
        }

        return view
    }
}