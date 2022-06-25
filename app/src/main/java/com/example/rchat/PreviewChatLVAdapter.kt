package com.example.rchat

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.rchat.utils.ChatSingleton

class PreviewChatLVAdapter(
    private val context: Activity,
    private val arrayList: ArrayList<PreviewChatDataClass>
) :
    ArrayAdapter<PreviewChatDataClass>(context, R.layout.preview_chat, arrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.preview_chat, null)

        val previewLogin: TextView = view.findViewById(R.id.Preview_Chat_Sender_Login)
        val previewTime: TextView = view.findViewById(R.id.Preview_Chat_Receiving_Time)
        val previewMessage: TextView = view.findViewById(R.id.Preview_Chat_Message_Txt)
        val previewYouTxt: TextView = view.findViewById(R.id.You_Txt)
//        val unreadTxt: TextView = view.findViewById(R.id.Unread_Txt)

        previewLogin.text = arrayList[position].previewLogin
        previewTime.text = arrayList[position].previewTime
        previewMessage.text = arrayList[position].previewMessage
        previewYouTxt.text = arrayList[position].previewYouTxt
//        unreadTxt.text = arrayList[position].unreadTxt

//        if (previewYouTxt.text == "")
//            previewYouTxt.visibility = View.GONE

        view.setOnClickListener {
//            unreadTxt.visibility = View.GONE
            val intent = Intent(context, ChatItselfWindow::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            ChatSingleton.isInChat = true
            ChatSingleton.chatName = previewLogin.text.toString()
            context.startActivity(intent)
            context.overridePendingTransition(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
        }

        view.setOnLongClickListener {
            val popupMenu = PopupMenu(context, it)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete_chat_item -> {
                        showAlertMessage(context, previewLogin.text.toString())
                        true
                    }
                    else -> false
                }
            }
            popupMenu.inflate(R.menu.more_pc_menu)
            popupMenu.show()
            true
        }

        return view
    }

    private fun showAlertMessage(context: Activity, chatName: String) {
        val message: AlertDialog.Builder = AlertDialog.Builder(context)
        message
            .setTitle("Внимание")
            .setMessage("Вы действительно хотите удалить данный чат?")
            .setCancelable(true)
            .setPositiveButton(
                "Да"
            ) { _, _ ->
                Toast.makeText(
                    context,
                    "Delete chat $chatName",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Нет") { dialog, _ ->
                dialog.cancel()
            }
        val messageWindow = message.create()
        messageWindow.show()
    }
}