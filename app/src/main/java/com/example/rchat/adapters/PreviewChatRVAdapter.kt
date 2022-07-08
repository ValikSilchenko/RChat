package com.example.rchat.adapters

import android.content.Intent
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

        val mLogin: TextView = itemView.findViewById(R.id.PC_Login)
        val mTime: TextView = itemView.findViewById(R.id.PC_ReceivingTime)
        val mMessage: TextView = itemView.findViewById(R.id.PC_Message)
        val mInfoTxt: TextView = itemView.findViewById(R.id.PC_ShownMessageInfo)
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
            mLogin.text = arrayList[position].previewLogin
            mTime.text = arrayList[position].previewTime
            mMessage.text = arrayList[position].previewMessage
            mInfoTxt.text = arrayList[position].previewYouTxt
            isNewMsg = arrayList[position].isNewMsg

            mMessage.typeface = if (isNewMsg && mInfoTxt.text == "")
                Typeface.DEFAULT_BOLD
            else
                Typeface.DEFAULT
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}