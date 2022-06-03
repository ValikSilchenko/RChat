package com.example.rchat

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

class AvailableContacts : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.Theme_Dark)
            Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.Theme_Light)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.available_contacts)

        val loginText: EditText = findViewById(R.id.AvailableContactsFindContact_EditText)
        val searchBtn: Button = findViewById(R.id.AvailableContactsFindContact_Btn)
        val backBtn: Button = findViewById(R.id.AvailableContactsBack_Btn)

        backBtn.setOnClickListener {
            super.onBackPressed()
        }

        searchBtn.setOnClickListener {
            if (loginText.text.isNotEmpty())
                sendAndReceiveData(loginText.text.toString())
            else
                showMessage("Внимание", "Не был введен логин")
        }
    }

    @Override
    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun sendAndReceiveData(login: String) {
        val client = OkHttpClient()
        val dataToSend = FormBody.Builder()
            .add("username", login)
            .build()
        val requestToSend = Request.Builder()
            .post(dataToSend)
            .url("http://localhost:8080")
            .build()
        val response = client.newCall(requestToSend).execute()
        if (response.body?.string() == "fail")
            showMessage("Ошибка", "Пользователь с таким логином не найден")
    }

    private fun showMessage(TitleText: CharSequence, MessageText: CharSequence) {
        val message: AlertDialog.Builder = AlertDialog.Builder(this)
        message
            .setTitle(TitleText)
            .setMessage(MessageText)
            .setCancelable(true)
            .setPositiveButton(
                "Ок"
            ) { dialog, _ -> dialog.cancel() }
        val messageWindow = message.create()
        messageWindow.show()
    }
}