package com.example.rchat.windows

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.rchat.R
import com.example.rchat.utils.ForegroundService
import com.example.rchat.utils.ChatFunctions
import com.example.rchat.utils.ChatSingleton
import com.example.rchat.utils.Requests

class AuthorizationWindow : AppCompatActivity() {
    private lateinit var login: String
    override fun onCreate(savedInstanceState: Bundle?) {

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.Theme_Dark)
            Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.Theme_Light)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.authorize_window)

        if (ChatFunctions().isAuthorized(this)) {
            login = ChatFunctions().getSavedLogin(this)
            if (!ChatFunctions().isServiceRunning(ForegroundService::class.java, applicationContext))
                startService(Intent(applicationContext, ForegroundService::class.java))
            startIntent(ChatsWindow::class.java)
        }

        val authorizeLoginText: EditText = findViewById(R.id.AW_Login)
        val authorizePasswordText: EditText = findViewById(R.id.AW_Password)
        val enterAccountBtn: Button = findViewById(R.id.AW_AuthorizeBtn)
        val noBitches: Button = findViewById(R.id.AW_NoAccountBtn)

        noBitches.setOnClickListener {
            val intent = Intent(this, RegistrationWindow::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(intent)
        }

        enterAccountBtn.setOnClickListener {
            if (authorizeLoginText.text.isNotEmpty() && authorizePasswordText.text.isNotEmpty()) {
                login = authorizeLoginText.text.toString()
                try {
                    Requests().post(
                        mapOf(
                            "username" to login,
                            "password" to authorizePasswordText.text.toString()
                        ),
                        "${ChatSingleton.serverUrl}/login"
                    )
                    try {
                        ChatFunctions().saveData(this, login, true)
                        if (!ChatFunctions().isServiceRunning(ForegroundService::class.java, applicationContext))
                            startService(Intent(applicationContext, ForegroundService::class.java))
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
        TODO("Выход из приложения")
    }

    // Открытие нового окна
    private fun startIntent(Window: Class<*>?) {
        startActivity(Intent(this, Window))
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        finish()
    }
}
