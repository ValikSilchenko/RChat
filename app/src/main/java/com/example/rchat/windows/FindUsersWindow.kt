package com.example.rchat.windows

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rchat.R
import com.example.rchat.adapters.PreviewChatRVAdapter
import com.example.rchat.dataclasses.PreviewChatDataClass
import com.example.rchat.utils.ChatFunctions
import com.example.rchat.utils.ChatSingleton
import com.example.rchat.utils.JasonSTATHAM
import com.example.rchat.utils.Requests

/* Оконный класс поиска пользователей для создания нового чата
*/
class FindUsersWindow : AppCompatActivity() {

    private var foundUserArrayList: ArrayList<PreviewChatDataClass> = ArrayList()
    private lateinit var arrayAdapter: PreviewChatRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        /* Установка темы приложения
        */
        val prefs = getSharedPreferences("Night Mode", Context.MODE_PRIVATE)
        when (prefs.getString("NightMode", "Day")) {
            "Day" -> setTheme(R.style.Theme_Light)
            "Night" -> setTheme(R.style.Theme_Dark)
            "System" -> {
                when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.Theme_Dark)
                    Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.Theme_Light)
                }
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.find_users_window)

        val foundUsersRV: RecyclerView = findViewById(R.id.FUW_FoundUsersRV)
        val backToChatsWindow: ImageButton = findViewById(R.id.FUW_BackBtn)
        val findBtn: ImageButton = findViewById(R.id.FUW_FindUserBtn)
        val loginInput: EditText = findViewById(R.id.FUW_FindUserLoginET)
        var foundUsers: List<String>

        arrayAdapter = PreviewChatRVAdapter(foundUserArrayList)
        foundUsersRV.layoutManager = LinearLayoutManager(this)
        foundUsersRV.adapter = arrayAdapter

        /* Нажатие кнопки возврата
        */
        backToChatsWindow.setOnClickListener {
            onBackPressed()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            finish()
        }

        /* Нажатие кнопки поиска пользователей по логину
        */
        findBtn.setOnClickListener {
            if (loginInput.text.isNotEmpty()) {
                try {
                    if (foundUserArrayList.isNotEmpty()) {
                        foundUserArrayList.clear()
                        arrayAdapter.notifyDataSetChanged()
                    }

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
                                element, "", "", "", 0, 0)  //!
                        )
                    }
//                    arrayAdapter.notifyDataSetChanged()
                    arrayAdapter.notifyItemInserted(foundUserArrayList.size)    //!
                    loginInput.text = null
                    if (foundUserArrayList.isEmpty())
                        Toast.makeText(this, getString(R.string.users_not_found_title), Toast.LENGTH_SHORT).show()
                } catch (exception: Exception) {
                    ChatFunctions().showMessage(
                        getString(R.string.error_title),
                        "${getString(R.string.error_of_sending_data_title)} ${exception.message}", this
                    )
                }
            } else
                ChatFunctions().showMessage(
                    getString(R.string.error_title),
                    getString(R.string.nothing_was_input_title),
                    this
                )
        }
    }

    @Override
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        finish()
    }
}