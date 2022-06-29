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

class CGCLVAdapter(private val context: Activity, private val arrayList: ArrayList<CGCDataClass>) :
    ArrayAdapter<CGCDataClass>(context, R.layout.gc_user_item, arrayList) {

        lateinit var avatar: ImageView
        lateinit var login: TextView
        lateinit var checkBox: CheckBox

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.gc_user_item, null)

        avatar = view.findViewById(R.id.GCU_Avatar)
        login = view.findViewById(R.id.GCU_Login)
        checkBox = view.findViewById(R.id.GCU_IsSelected)

        login.text = arrayList[position].login
        
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                TODO("Добавить логин куда-то, где будут все выбранные пользователи для чата")
            } else
                TODO("Удалить логин откуда-то, где будут все выбранные пользователи для чата")
        }

        return view
    }
}