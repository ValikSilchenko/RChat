package com.example.rchat

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PreviewChatRvAdapter(
    private var previewChatLogins: List<String>,
    private var previewChatReceivingTimes: List<String>,
    private var previewChatMessages: List<String>,
    private var context: Context,
    private var clazz: Class<*>?
) : RecyclerView.Adapter<PreviewChatRvAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val prvLogin: TextView = itemView.findViewById(R.id.Preview_Chat_Sender_Login)
        val prvReceivingTime: TextView = itemView.findViewById(R.id.Preview_Chat_Receiving_Time)
        val prvMessage: TextView = itemView.findViewById(R.id.Preview_Chat_Message_Txt)

        init {
            itemView.setOnClickListener {
                startNewActivity()
            }
        }

        private fun startNewActivity() {
            val intent = Intent(context, clazz)
            intent.putExtra("Chat Name", prvLogin.text.toString())
            context.startActivity(intent)
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
    }

    override fun getItemCount(): Int {
        return previewChatLogins.size
    }
}