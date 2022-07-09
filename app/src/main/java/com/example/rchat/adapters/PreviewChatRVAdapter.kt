package com.example.rchat.adapters

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
        var isNewMsg = false

        init {
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ChatItselfWindow::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                mMessage.typeface = Typeface.DEFAULT
                ChatSingleton.isInChat = true
                ChatSingleton.chatName = mLogin.text.toString()
                itemView.context.startActivity(intent)
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

            if (arrayList[position].unreadMsgCount == 0) {
                mInfoTxt.apply {
                    if (arrayList[position].infoTxt != "") {
                        text = arrayList[position].infoTxt
                        setPadding(0, 0, 0, 0)
                        backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.white))
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
}