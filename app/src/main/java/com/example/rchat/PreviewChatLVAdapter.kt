package com.example.rchat

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
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

        view.setOnClickListener {
//            unreadTxt.visibility = View.GONE
            val intent = Intent(context, ChatItselfWindow::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.putExtra("Chat Name", previewLogin.text.toString())
            ChatSingleton.isInChat = true
            context.startActivity(intent)
            context.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        return view
    }
}