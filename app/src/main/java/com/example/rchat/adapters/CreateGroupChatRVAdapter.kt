package com.example.rchat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rchat.R
import com.example.rchat.dataclasses.PreviewChatDataClass
import com.example.rchat.utils.ChatSingleton

class CreateGroupChatRVAdapter(private var arrayList: ArrayList<PreviewChatDataClass>) :
    RecyclerView.Adapter<CreateGroupChatRVAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val login: TextView = itemView.findViewById(R.id.GCU_LoginTV)
        private val checkBox: CheckBox = itemView.findViewById(R.id.GCU_IsSelectedCB)

        init {
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    ChatSingleton.addUserForGroupChat(login.text.toString())
                } else {
                    ChatSingleton.deleteUserFromGroupChatArray(login.text.toString())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.gc_user_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            login.text = arrayList[position].login
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}