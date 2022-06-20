package com.example.rchat

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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async


class AuthorizationWindow : AppCompatActivity() {
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
            startIntent(RegistrationWindow::class.java)
        }

        // Вход в аккаунт
        enterAccountBtn.setOnClickListener {
            if (authorizeLoginText.text.isNotEmpty() && authorizePasswordText.text.isNotEmpty()) {
                login = authorizeLoginText.text.toString()
                try {
                    GlobalScope.async {
                        Requests().post(
                            mapOf(
                                "username" to authorizeLoginText.text.toString(),
                                "password" to authorizePasswordText.text.toString()
                            ),
                            "${ChatSingleton.serverUrl}/login"
                        )
                    }
                    try {
                        GlobalScope.async {
                            ChatSingleton.openConnection(authorizeLoginText.text.toString())
                        }
                        startIntent(ChatsWindow::class.java)
                    } catch (exception: Exception) {
                        ChatFunctions().showMessage("Ошибка", "Ошибка установки соединения", this)
                        //TODO("Обработка ошибки при отсутствии интернетов")
                    }

                } catch (exception: Exception) {
                    ChatFunctions().showMessage("Ошибка", "Ошибка отправки данных", this)
                }
            } else
                ChatFunctions().showMessage(
                    "Внимание",
                    "Проверьте корректность введенных данных",
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

    // Открытие нового окна
    private fun startIntent(Window: Class<*>?) {
        val intent = Intent(this, Window)
        intent.putExtra("User Login", login)
        startActivity(intent)
    }
}
