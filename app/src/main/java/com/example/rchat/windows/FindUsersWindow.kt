package com.example.rchat.windows

import android.content.res.Configuration
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.rchat.R
import com.example.rchat.adapters.PreviewChatLVAdapter
import com.example.rchat.dataclasses.PreviewChatDataClass
import com.example.rchat.utils.ChatFunctions
import com.example.rchat.utils.ChatSingleton
import com.example.rchat.utils.JasonSTATHAM
import com.example.rchat.utils.Requests

class FindUsersWindow : AppCompatActivity() {

    private var foundUserArrayList: ArrayList<PreviewChatDataClass> = ArrayList()
    private lateinit var arrayAdapter: PreviewChatLVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.Theme_Dark)
            Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.Theme_Light)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.find_users_window)

        val foundUsersLv: ListView = findViewById(R.id.FUW_FoundUsersArray)
        val backToChatsWindow: ImageButton = findViewById(R.id.FUW_BackBtn)
        val findBtn: ImageButton = findViewById(R.id.FUW_FindUserBtn)
        val loginInput: EditText = findViewById(R.id.FUW_FindUserLogin)
        var foundUsers: List<String>

        backToChatsWindow.setOnClickListener {
            onBackPressed()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        findBtn.setOnClickListener {
            if (loginInput.text.isNotEmpty()) {
                try {
                    if (foundUserArrayList.isNotEmpty())
                        foundUserArrayList.clear()

                    foundUsers = JasonSTATHAM().parseUsers(
                        Requests().get(
                            mapOf(
                                "username" to loginInput.text.toString()
                            ), "${ChatSingleton.serverUrl}/find"
                        )
                    )
                    for (element in foundUsers) {
                        foundUserArrayList.add(
                            PreviewChatDataClass(
                                element, "", "", ""
                            )
                        )
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
                ChatFunctions().showMessage(
                    " Ошибка",
                    "Ничего не было введено",
                    this
                )
        }
    }

    @Override
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
}