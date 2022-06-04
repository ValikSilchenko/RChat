package com.example.rchat

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class RegistrationWindow : AppCompatActivity() {
    private var login: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())

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
                && phoneNumberText.text.isNotEmpty() && passwordText.text.isNotEmpty()
                && repeatPasswordText.text.isNotEmpty()
                && passwordText.text.toString() == repeatPasswordText.text.toString()
            ) {
                login = loginText.text.toString()
                try {
                    Requests().post(
                        mapOf(
                            "username" to loginText.text.toString(),
                            "email" to emailText.text.toString(),
                            "phone" to phoneNumberText.text.toString(),
                            "password" to passwordText.text.toString()
                        ),
                        "http://192.168.1.107:8080/user"
                    )
                } catch (exception: Exception) {
                    showMessage(
                        "Ошибка",
                        "Ошибка отправки данных"
                    )
                }

            } else
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
        val intent = Intent(this, Window)
        intent.putExtra("User Login", login)
        startActivity(intent)
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
}