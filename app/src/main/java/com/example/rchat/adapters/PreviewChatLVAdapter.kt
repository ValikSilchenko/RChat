package com.example.rchat.adapters

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.rchat.R
import com.example.rchat.dataclasses.PreviewChatDataClass
import com.example.rchat.utils.ChatSingleton
import com.example.rchat.windows.ChatItselfWindow

class PreviewChatLVAdapter(
    private val context: Activity,
    private val arrayList: ArrayList<PreviewChatDataClass>
) :
    ArrayAdapter<PreviewChatDataClass>(context, R.layout.preview_chat, arrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.preview_chat, null)

        val login: TextView = view.findViewById(R.id.PC_Login)
        val time: TextView = view.findViewById(R.id.PC_ReceivingTime)
        val message: TextView = view.findViewById(R.id.PC_Message)
        val youTxt: TextView = view.findViewById(R.id.PC_You)
        val previewUnreadCount: TextView = view.findViewById(R.id.PC_UnreadCount)
        val previewRead: ImageView = view.findViewById(R.id.PC_ReadMsg)
        var isNewMsg = false

        login.text = arrayList[position].previewLogin
        time.text = arrayList[position].previewTime
        message.text = arrayList[position].previewMessage
        youTxt.text = arrayList[position].previewYouTxt
        isNewMsg = arrayList[position].isNewMsg

        message.typeface = if (isNewMsg)
            Typeface.DEFAULT_BOLD
        else
            Typeface.DEFAULT

        view.setOnClickListener {
            val intent = Intent(context, ChatItselfWindow::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            message.typeface = Typeface.DEFAULT
            ChatSingleton.isInChat = true
            ChatSingleton.chatName = login.text.toString()
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
                        showAlertMessage(context, login.text.toString())
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