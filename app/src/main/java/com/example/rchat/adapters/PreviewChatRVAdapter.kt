package com.example.rchat.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.rchat.R
import com.example.rchat.dataclasses.PreviewChatDataClass
import com.example.rchat.utils.ChatSingleton
import com.example.rchat.windows.ChatItselfWindow

class PreviewChatRVAdapter(private var arrayList: ArrayList<PreviewChatDataClass>) :
    RecyclerView.Adapter<PreviewChatRVAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val mLogin: TextView = itemView.findViewById(R.id.PC_LoginTV)
        val mTime: TextView = itemView.findViewById(R.id.PC_ReceivingTimeTV)
        val mMessage: TextView = itemView.findViewById(R.id.PC_MessageTV)
        val mInfoTxt: TextView = itemView.findViewById(R.id.PC_ShownMessageInfoTV)
        var chatId = 0

        init {
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ChatItselfWindow::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                ChatSingleton.apply {
                    isInChat = true
                    chatName = mLogin.text.toString()
                }
                itemView.context.startActivity(intent)
//                itemView.context.overridePendingTransition(
//                    android.R.anim.slide_in_left,
//                    android.R.anim.slide_out_right
//                )
            }

            itemView.setOnLongClickListener {
                val popupMenu = PopupMenu(itemView.context, it)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.delete_chat_item -> {
                            showAlertMessage(itemView.context, mLogin.text.toString())
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.inflate(R.menu.more_pc_menu)
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
            LayoutInflater.from(parent.context).inflate(R.layout.preview_chat_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            mLogin.text = arrayList[position].login
            mTime.text = arrayList[position].time
            mMessage.text = arrayList[position].message
            mInfoTxt.text = arrayList[position].infoTxt
            chatId = arrayList[position].chatId

            if (arrayList[position].unreadMsgCount == 0) {
                mInfoTxt.apply {
                    if (arrayList[position].infoTxt != "") {
                        text = arrayList[position].infoTxt
//                    setPadding(0, 0, 0, 0)
                    } else
                        visibility = View.GONE
                }
            } else {
                mInfoTxt.apply {
                    text = if (arrayList[position].unreadMsgCount >= 100)
                        "99+"
                    else
                        arrayList[position].unreadMsgCount.toString()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    private fun showAlertMessage(context: Context, chatName: String) {
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