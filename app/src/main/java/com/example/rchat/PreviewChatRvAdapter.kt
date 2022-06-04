package com.example.rchat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

class PreviewChatRvAdapter(
    private var previewChatLogins: List<String>,
    private var previewChatReceivingTimes: List<String>,
    private var previewChatMessages: List<String>,
    private var previewChatIDs: List<Int>,
    private var chatListWindow: LinearLayout,
    private var chatItselfWindow: LinearLayout,
    private var findUserWindow: LinearLayout
) : RecyclerView.Adapter<PreviewChatRvAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val prvLogin: TextView = itemView.findViewById(R.id.Preview_Chat_Sender_Login)
        val prvReceivingTime: TextView = itemView.findViewById(R.id.Preview_Chat_Receiving_Time)
        val prvMessage: TextView = itemView.findViewById(R.id.Preview_Chat_Message_Txt)
        var prvChatID: Int = 0

        init {
            itemView.setOnClickListener {
                openChat()
            }
        }

        private fun openChat() {
            chatListWindow.isVisible = false
            findUserWindow.isVisible = false
            chatItselfWindow.isVisible = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.preview_chat, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.prvLogin.text = previewChatLogins[position]
        holder.prvReceivingTime.text = previewChatReceivingTimes[position]
        holder.prvMessage.text = previewChatMessages[position]
        holder.prvChatID = previewChatIDs[position]
    }

    override fun getItemCount(): Int {
        return previewChatLogins.size
    }
}