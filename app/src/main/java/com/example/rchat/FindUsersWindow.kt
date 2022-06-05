package com.example.rchat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rchat.utils.ChatFunctions
import com.example.rchat.utils.JasonSTATHAM
import com.example.rchat.utils.Requests

class FindUsersWindow : AppCompatActivity() {

    // Переменные для списка чатов
    private var previewChatLogins = mutableListOf<String>()
    private var previewChatReceivingTimes = mutableListOf<String>()
    private var previewChatMessages = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.find_users)

        val foundUsersRv: RecyclerView = findViewById(R.id.FoundUsers_Array)
        val backToChatsWindow: Button = findViewById(R.id.FindUserBack_Btn)
        val findBtn: Button = findViewById(R.id.FindUserFind_Btn)
        val loginInput: EditText = findViewById(R.id.FindUserLogin_EditText)
        var foundUsers: List<String>

        backToChatsWindow.setOnClickListener {
            super.onBackPressed()
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
                        ChatFunctions().addToList(
                            previewChatLogins,
                            previewChatReceivingTimes,
                            previewChatMessages,
                            element,
                            "",
                            ""
                        )
                    }
                    foundUsersRv.layoutManager = LinearLayoutManager(this) // Возможно, строки 54 и 55 надо поместить в цикл for, что выше
                    foundUsersRv.adapter = PreviewChatRvAdapter(
                        previewChatLogins,
                        previewChatReceivingTimes,
                        previewChatMessages,
                        this
                    )
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
        super.onBackPressed()
    }
}