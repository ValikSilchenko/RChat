package com.example.rchat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageItemRvAdapter(
    private var incomingLogins: List<String>,
    private var incomingMessageTexts: List<String>,
    private var outgoingLogins: List<String>,
    private var outgoingMessageTexts: List<String>
) : RecyclerView.Adapter<MessageItemRvAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val msgIncomingLogin: TextView = itemView.findViewById(R.id.Message_IncomingLogin_Txt)
        val msgOutgoingLogin: TextView = itemView.findViewById(R.id.Message_OutgoingLogin_Txt)
        val msgIncomingMessage: TextView = itemView.findViewById(R.id.Message_IncomingMessage_Txt)
        val msgOutgoingMessage: TextView = itemView.findViewById(R.id.Message_OutgoingMessage_Txt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.msgIncomingLogin.text = incomingLogins[position]
        holder.msgOutgoingLogin.text = outgoingLogins[position]
        holder.msgIncomingMessage.text = incomingMessageTexts[position]
        holder.msgOutgoingMessage.text = outgoingMessageTexts[position]
    }

    override fun getItemCount(): Int {
        return incomingLogins.size
    }
}