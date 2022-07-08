package com.example.rchat.windows

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.rchat.R
import com.example.rchat.utils.ChatFunctions
import com.example.rchat.utils.ChatSingleton
import com.example.rchat.utils.Requests


class RegistrationWindow : AppCompatActivity() {
    private lateinit var login: String
    override fun onCreate(savedInstanceState: Bundle?) {

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
        setContentView(R.layout.registration_window)

        val loginText: EditText = findViewById(R.id.RW_LoginET)
        val emailText: EditText = findViewById(R.id.RW_EmailET)
        val phoneNumberText: EditText = findViewById(R.id.RW_PhoneNumberET)
        val passwordText: EditText = findViewById(R.id.RW_PasswordET)
        val repeatPasswordText: EditText = findViewById(R.id.RW_RepeatPasswordET)
        val registrationBtn: Button = findViewById(R.id.RW_RegistrationBtn)
        val authorizeBtn: Button = findViewById(R.id.RW_AuthorizeBtn)

        authorizeBtn.setOnClickListener {
            onBackPressed()
        }

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
                            "username" to login,
                            "email" to emailText.text.toString(),
                            "phone" to ChatFunctions().transformPhoneNumber(phoneNumberText.text.toString()),
                            "password" to passwordText.text.toString()
                        ),
                        "${ChatSingleton.serverUrl}/user"
                    )
                    try {
                        ChatFunctions().saveLogin(this, login, true)
                        val mIntent = Intent(this, SplashScreenWindow::class.java)
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        startActivity(mIntent)
                        overridePendingTransition(
                            android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right
                        )
                        finish()

                    } catch (exception: Exception) {
                        if (ChatFunctions().isInternetAvailable(applicationContext)) {
                            ChatFunctions().showMessage(
                                "Ошибка",
                                "Ошибка установки соединения. Код: ${exception.message}",
                                this
                            )
                        } else
                            ChatFunctions().showMessage(
                                "Ошибка",
                                "Ошибка отправки данных. Код: ${exception.message}",
                                applicationContext
                            )
                    }
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
        val intent = Intent(this, AuthorizationWindow::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
}