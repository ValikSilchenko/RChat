package com.example.rchat.windows

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.rchat.R
import com.example.rchat.utils.BackgroundService
import com.example.rchat.utils.ChatFunctions

/* Оконный класс приветственного окна
*/
class SplashScreenWindow : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        /* Установка темы приложения
        */
        ChatFunctions().setAppTheme(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen_window)

        val welcomeTxt: TextView = findViewById(R.id.SSW_WelcomeTV)
        val loginText: TextView = findViewById(R.id.SSW_LoginTV)

        /* Показ различных текстов в зависимости от того, авторизован пользователь или нет
        */
        if (!ChatFunctions().isAuthorized(this)) {
            welcomeTxt.text = getString(R.string.welcome_rchat_title)
            loginText.visibility = View.GONE
        } else {
            welcomeTxt.text = getString(R.string.hello_title)
            loginText.text = ChatFunctions().getSavedLogin(this)
        }

        /* Открытие нового окна через какое-то время
        */
        val handler = Handler()
        handler.postDelayed({
            if (ChatFunctions().isAuthorized(this)) {
                if (!ChatFunctions().isServiceRunning(
                        BackgroundService::class.java,
                        applicationContext
                    )
                ) {
                    startService(Intent(applicationContext, BackgroundService::class.java))
                }

                ChatFunctions().openNewWindow(
                    this,
                    ChatsWindow::class.java,
                    shouldBeFinished = true
                )
            } else {
                ChatFunctions().openNewWindow(
                    this,
                    AuthorizationWindow::class.java,
                    shouldBeFinished = true
                )
            }
        }, 3000)
    }
}