package com.example.rchat

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

class RegistrationWindow : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.Theme_Dark)
            Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.Theme_Light)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_window)

        val loginText: EditText = findViewById(R.id.RegistrationLogin_Input)
        val emailText: EditText = findViewById(R.id.RegistrationEmail_Input)
        val phoneNumberText: EditText = findViewById(R.id.RegistrationPhoneNumber_Input)
        val passwordText: EditText = findViewById(R.id.RegistrationPassword_Input)
        val repeatPasswordText: EditText = findViewById(R.id.RegistrationRepeatPassword_Input)
        val registrationBtn: Button = findViewById(R.id.RegistrationRegistration_Btn)
        val authorizeBtn: Button = findViewById(R.id.RegistrationAuthorize_Btn)

        // Нажатие кнопки RegistrationAuthorize_Btn
        authorizeBtn.setOnClickListener {
            startIntent(AuthorizeWindow::class.java)
        }

        // Нажатие кнопки RegistrationRegistration_Btn
        registrationBtn.setOnClickListener {
            if (emailText.text.isNotEmpty() && loginText.text.isNotEmpty()
                && foundSymbol(emailText.text, '@')
                && passwordText.text.isNotEmpty() && passwordText.text == repeatPasswordText.text
            )
            {
                sendAndReceiveData(loginText.text.toString(), emailText.text.toString(), phoneNumberText.text.toString(), passwordText.text.toString())
                startIntent(ChatList::class.java)
            }
            else
                showMessage(
                    "Ошибка",
                    "Внимательно проверьте корректность введенных данных, а также совпали ли пароли"
                )
        }
    }

    @Override
    override fun onBackPressed() {
        val exitMessage: AlertDialog.Builder = AlertDialog.Builder(this)
        exitMessage
            .setTitle("Предупреждение")
            .setMessage("Вы действительно хотите выйти?")
            .setCancelable(true)
            .setPositiveButton("Да") { _, _ -> finish() }
            .setNegativeButton(
                "Нет"
            ) { dialog, _ -> dialog.cancel() }
        val exitWindow = exitMessage.create()
        exitWindow.show()
    }

    // Открыть новое окно
    private fun startIntent(Window: Class<*>?) {
        startActivity(Intent(this, Window))
    }

    // Показать всплывающее сообщение
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

    // Проверка почты на валидность (ПЕРЕДЕЛАТЬ - ДЕБАГОВАЯ ВЕРСИЯ)
    private fun foundSymbol(whereFind: CharSequence, whatToFind: Char): Boolean {
        for (i in 1..whereFind.length - 2) {
            if (whereFind[i] == whatToFind && whereFind[i + 2] == '.')
                return true
        }
        return false
    }

    // Отправка данных в БД
    private fun sendAndReceiveData(login: String, email: String, phoneNumber: String, password: String) {
        val client = OkHttpClient()
        val dataToSend = FormBody.Builder()
            .add("username", login)
            .add("email", email)
            .add("phone", phoneNumber)
            .add("password", password)
            .build()
        val requestToSend = Request.Builder()
            .post(dataToSend)
            .url("http://localhost:8080/user")
            .build()
        client.newCall(requestToSend).execute().use { response ->
            if (!response.isSuccessful)
                showMessage("Ошибка", "Пользователь уже существует")
            else
                startIntent(ChatList::class.java)
        }
    }
}