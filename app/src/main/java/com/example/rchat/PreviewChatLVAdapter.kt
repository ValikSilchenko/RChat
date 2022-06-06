package com.example.rchat

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast

class PreviewChatLVAdapter(private val context: Activity, private val arrayList: ArrayList<PreviewChatDataClass>):
    ArrayAdapter<PreviewChatDataClass>(context, R.layout.preview_chat, arrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.preview_chat, null)

        val previewLogin: TextView = view.findViewById(R.id.Preview_Chat_Sender_Login)
        val previewTime: TextView = view.findViewById(R.id.Preview_Chat_Receiving_Time)
        val previewMessage: TextView = view.findViewById(R.id.Preview_Chat_Message_Txt)

        previewLogin.text = arrayList[position].previewLogin
        previewTime.text = arrayList[position].previewTime
        previewMessage.text = arrayList[position].previewMessage

        view.setOnClickListener {
            val name = previewLogin.text.toString()
            val intent = Intent(context, ChatItselfWindow::class.java)
            intent.putExtra("Chat Name", name)
            context.startActivity(intent)
        }

        return view
    }
}