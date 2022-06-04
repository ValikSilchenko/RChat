package com.example.rchat

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request


class AuthorizeWindow : AppCompatActivity() {
    private var login: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.Theme_Dark)
            Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.Theme_Light)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.authorize_window)

        val authorizeLoginText: EditText = findViewById(R.id.AuthorizeLogin_Input)
        val authorizePasswordText: EditText = findViewById(R.id.AuthorizePassword_Input)
        val enterAccountBtn: Button = findViewById(R.id.AuthorizeAuthorize_Btn)
        val hasNoAccountBtn: Button = findViewById(R.id.Authorize_DontHaveAccount_Btn)

        // Переход на страницу регистрации
        hasNoAccountBtn.setOnClickListener {
            super.onBackPressed()
        }

        // Вход в аккаунт
        enterAccountBtn.setOnClickListener {
            if (authorizeLoginText.text.isNotEmpty() && authorizePasswordText.text.isNotEmpty()) {
                login = authorizeLoginText.text.toString()
                try {
                    Requests().post(
                        mapOf(
                            "username" to authorizeLoginText.text.toString(),
                            "password" to authorizePasswordText.text.toString()
                        ),
                        "http://192.168.1.107:8080/login"
                    )
                } catch (exception: Exception) {
                    showMessage(
                        "Ошибка",
                        "Ошибка отправки данных"
                    )
                }
            } else
                showMessage("Внимание", "Проверьте корректность введенных данных")
        }
    }

    @Override
    override fun onBackPressed() {
        super.onBackPressed()
    }

    // Открытие нового окна
    private fun startIntent(Window: Class<*>?) {
        val intent = Intent(this, Window)
        intent.putExtra("User Login", login)
        startActivity(intent)
    }

    // Показ всплывающего сообщения
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

    // Отправка почты/пароля и получение ответа от сервера
    private fun sendAndReceiveData(login: String, password: String) {
        val client = OkHttpClient()
        val dataToSend = FormBody.Builder()
            .add("username", login)
            .add("password", password)
            .build()
        val requestToSend = Request.Builder()
            .post(dataToSend)
            .url("http://192.168.1.107:8080/login")
            .build()
        client.newCall(requestToSend).execute().use { response ->
            if (!response.isSuccessful)
                showMessage("Ошибка", "Проверьте верность введенных данных")
            else {
                startIntent(Chats::class.java)
            }
        }
    }

//    private fun checkPhoneNumber(phoneNumber: String) {
//        if (phoneNumber[0] == '8') {
//            phoneNumber.replace(phoneNumber[0], '7')
//            phoneNumber.padStart(12, '+')
//            addSymbolsToPhoneNumber(phoneNumber)
//        }
//        else
//            addSymbolsToPhoneNumber(phoneNumber)
//    }
//
//    private fun addSymbolsToPhoneNumber(phoneNumber: String) {
//
//    }
}
