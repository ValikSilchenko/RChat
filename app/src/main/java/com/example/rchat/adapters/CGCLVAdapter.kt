package com.example.rchat.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.example.rchat.R
import com.example.rchat.dataclasses.CGCDataClass
import com.example.rchat.utils.ChatSingleton

class CGCLVAdapter(private val context: Activity, private val arrayList: ArrayList<CGCDataClass>) :
    ArrayAdapter<CGCDataClass>(context, R.layout.gc_user_item, arrayList) {

    lateinit var avatar: ImageView
    lateinit var login: TextView
    lateinit var checkBox: CheckBox

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.gc_user_item, null)

        avatar = view.findViewById(R.id.GCU_AvatarImg)
        login = view.findViewById(R.id.GCU_LoginTV)
        checkBox = view.findViewById(R.id.GCU_IsSelectedCB)

        login.text = arrayList[position].login

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                ChatSingleton.updateUsersList(login.text.toString())
            else
                ChatSingleton.deleteUser(login.text.toString())
        }

        return view
    }
}