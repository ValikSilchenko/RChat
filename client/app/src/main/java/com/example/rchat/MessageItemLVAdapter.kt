package com.example.rchat

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast

class MessageItemLVAdapter(
    private val context: Activity,
    private val arrayList: ArrayList<MessageItemDataClass>
) : ArrayAdapter<MessageItemDataClass>(context, R.layout.message_item, arrayList) {

    lateinit var incomingLogin: TextView
    lateinit var incomingMessage: TextView
    lateinit var outgoingLogin: TextView
    lateinit var outgoingMessage: TextView

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.message_item, null)

        incomingLogin = view.findViewById(R.id.Message_IncomingLogin_Txt)
        incomingMessage = view.findViewById(R.id.Message_IncomingMessage_Txt)
        outgoingLogin = view.findViewById(R.id.Message_OutgoingLogin_Txt)
        outgoingMessage = view.findViewById(R.id.Message_OutgoingMessage_Txt)

        incomingLogin.text = arrayList[position].incomingLogin
        incomingMessage.text = arrayList[position].incomingMessage
        outgoingLogin.text = arrayList[position].outgoingLogin
        outgoingMessage.text = arrayList[position].outgoingMessage

        if (incomingMessage.text == "") {
            incomingMessage.visibility = View.GONE
            incomingLogin.visibility = View.GONE

        } else if (outgoingMessage.text == "") {
            outgoingMessage.visibility = View.GONE
            outgoingLogin.visibility = View.GONE
        }

        view.setOnLongClickListener {
            val popupMenu = PopupMenu(context, it)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete_message_item -> {
                        Toast.makeText(
                            context,
                            "Delete message",
                            Toast.LENGTH_SHORT
                        ).show()
                        true
                    }
                    R.id.edit_message_item -> {
                        Toast.makeText(
                            context,
                            "Edit message",
                            Toast.LENGTH_SHORT
                        ).show()
                        true
                    }
                    R.id.reply_message_item -> {
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