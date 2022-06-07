package com.example.rchat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.rchat.utils.ChatFunctions
import com.example.rchat.utils.JasonSTATHAM
import com.example.rchat.utils.Requests

class FindUsersWindow : AppCompatActivity() {

    private var foundUserArrayList: ArrayList<PreviewChatDataClass> = ArrayList()
    private lateinit var arrayAdapter: PreviewChatLVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.find_users)

        val foundUsersLv: ListView = findViewById(R.id.FoundUsers_Array)
        val backToChatsWindow: Button = findViewById(R.id.FindUserBack_Btn)
        val findBtn: Button = findViewById(R.id.FindUserFind_Btn)
        val loginInput: EditText = findViewById(R.id.FindUserLogin_EditText)
        var foundUsers: List<String>

        backToChatsWindow.setOnClickListener {
            startActivity(Intent(this, ChatsWindow::class.java))
        }

        findBtn.setOnClickListener {
            if (loginInput.text.isNotEmpty()) {
                try {
                    foundUsers = JasonSTATHAM().parseUsers(
                        Requests().get(
                            mapOf(
                                "username" to loginInput.text.toString()
                            ), "http://192.168.1.107:8080/find"
                        )
                    )
                    for (element in foundUsers) {
                        foundUserArrayList.add(PreviewChatDataClass(element, "", ""))
                    }
                    arrayAdapter = PreviewChatLVAdapter(this, foundUserArrayList)
                    arrayAdapter.notifyDataSetChanged()
                    foundUsersLv.adapter = arrayAdapter
                    loginInput.text = null
                } catch (exception: Exception) {
                    ChatFunctions().showMessage(
                        "Ошибка",
                        "Ошибка отправки данных. Код: ${exception.message}", this
                    )
                }
            } else
                ChatFunctions().showMessage(" Ошибка", "Ничего не было введено", this)
        }
    }

    @Override
    override fun onBackPressed() {
        startActivity(Intent(this, ChatsWindow::class.java))
    }
}