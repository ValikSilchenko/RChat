package com.example.rchat.windows

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.rchat.R
import com.example.rchat.utils.ChatFunctions
import com.example.rchat.utils.ChatSingleton
import com.example.rchat.utils.Requests

/* Оконный класс окна регистрации
*/
class RegistrationWindow : AppCompatActivity() {
    private lateinit var login: String
    override fun onCreate(savedInstanceState: Bundle?) {

        /* Установка темы приложения
        */
        ChatFunctions().setAppTheme(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_window)

        val loginText: EditText = findViewById(R.id.RW_LoginET)
        val emailText: EditText = findViewById(R.id.RW_EmailET)
        val phoneNumberText: EditText = findViewById(R.id.RW_PhoneNumberET)
        val passwordText: EditText = findViewById(R.id.RW_PasswordET)
        val repeatPasswordText: EditText = findViewById(R.id.RW_RepeatPasswordET)
        val registrationBtn: Button = findViewById(R.id.RW_RegistrationBtn)
        val authorizeBtn: Button = findViewById(R.id.RW_AuthorizeBtn)

        /* Нажатие кнопки авторизации
        */
        authorizeBtn.setOnClickListener {
            ChatFunctions().openNewWindow(this, AuthorizationWindow::class.java, shouldBeFinished = true)
        }

        /* Нажатие кнопки регистрации
        */
        registrationBtn.setOnClickListener {
            if (emailText.text.isNotEmpty() && loginText.text.isNotEmpty()
                && phoneNumberText.text.isNotEmpty() && passwordText.text.isNotEmpty()
                && repeatPasswordText.text.isNotEmpty()
                && passwordText.text.toString() == repeatPasswordText.text.toString()
            ) {
                login = loginText.text.toString()
                try {
                    val userID = Requests().post(
                        mapOf(
                            "username" to login,
                            "email" to emailText.text.toString(),
                            "phone" to ChatFunctions().transformPhoneNumber(phoneNumberText.text.toString()),
                            "password" to passwordText.text.toString()
                        ),
                        "${ChatSingleton.serverUrl}/user"
                    ).toInt()
                    try {
                        ChatFunctions().apply {
                            saveLogin(applicationContext, login, true)
                            saveUserID(applicationContext, userID)
                        }
                        ChatFunctions().openNewWindow(this, SplashScreenWindow::class.java, shouldBeFinished = true)
                    } catch (exception: Exception) {
                        if (ChatFunctions().isInternetAvailable(applicationContext)) {
                            ChatFunctions().showMessage(
                                getString(R.string.error_title),
                                "${getString(R.string.error_of_connection_title)} ${exception.message}",
                                this
                            )
                        } else
                            ChatFunctions().showMessage(
                                getString(R.string.error_title),
                                "${getString(R.string.error_of_sending_data_title)} ${exception.message}",
                                applicationContext
                            )
                    }
                } catch (exception: Exception) {
                    ChatFunctions().showMessage(
                        getString(R.string.error_title),
                        "${getString(R.string.error_of_sending_data_title)} ${exception.message}", this
                    )
                }
            } else
                ChatFunctions().showMessage(
                    getString(R.string.error_title),
                    getString(R.string.check_data_and_passwords_title),
                    this
                )
        }
    }
}