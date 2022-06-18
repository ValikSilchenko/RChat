package com.example.rchat

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.rchat.utils.ChatFunctions
import com.example.rchat.utils.ChatSingleton
import com.example.rchat.utils.Requests


class RegistrationWindow : AppCompatActivity() {
    private var login: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.Theme_Dark)
            Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.Theme_Light)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_window)

        val pref = getSharedPreferences("Account", Context.MODE_PRIVATE)
        val loginText: EditText = findViewById(R.id.RegistrationLogin_Input)
        val emailText: EditText = findViewById(R.id.RegistrationEmail_Input)
        val phoneNumberText: EditText = findViewById(R.id.RegistrationPhoneNumber_Input)
        val passwordText: EditText = findViewById(R.id.RegistrationPassword_Input)
        val repeatPasswordText: EditText = findViewById(R.id.RegistrationRepeatPassword_Input)
        val registrationBtn: Button = findViewById(R.id.RegistrationRegistration_Btn)
        val authorizeBtn: Button = findViewById(R.id.RegistrationAuthorize_Btn)

        // Нажатие кнопки RegistrationAuthorize_Btn
        authorizeBtn.setOnClickListener {
            startIntent(AuthorizationWindow::class.java)
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
                    try {

                        val editor = pref.edit()
                        editor.putBoolean("IsAuthorized", true)
                        editor.putString("User Login", login)
                        editor.apply()

                        ChatSingleton.openConnection(loginText.text.toString())
                        startIntent(ChatsWindow::class.java)
                    } catch (exception: Exception) {
                        ChatFunctions().showMessage("Ошибка", "Ошибка установки соединения", this)
                        //TODO("Обработка ошибки при отсутствии интернетов")
                    }
                    startIntent(ChatsWindow::class.java)
                } catch (exception: Exception) {
                    ChatFunctions().showMessage(
                        "Ошибка",
                        "Ошибка отправки данных. Код: ${exception.message}", this
                    )
                }

            } else
                ChatFunctions().showMessage(
                    "Ошибка",
                    "Внимательно проверьте корректность введенных данных, а также совпали ли пароли",
                    this
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
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        startActivity(intent)
    }
}