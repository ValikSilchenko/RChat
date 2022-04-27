package com.example.rchat

import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

class AuthorizeWindow : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        when(resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK)
        {
            Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.Theme_Dark)
            Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.Theme_Light)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.authorize_window)

        val authorizeEmailText: EditText = findViewById(R.id.AuthorizeEmail_Input)
        val authorizePasswordText: EditText = findViewById(R.id.AuthorizePassword_Input)
        val enterAccountBtn: Button = findViewById(R.id.AuthorizeAuthorize_Btn)
        val hasNoAccountBtn: Button = findViewById(R.id.Authorize_DontHaveAccount_Btn)
        val forgotPasswordBtn: Button = findViewById(R.id.Authorize_ForgotPassword_Btn)

        var eMail = "Admin"
        var password = "12345"

        // Переход на страницу регистрации
        hasNoAccountBtn.setOnClickListener {
            startIntent(RegistrationWindow::class.java)
        }

        // Вход в аккаунт
        enterAccountBtn.setOnClickListener {
            // Если почта и пароль совпадают
            if (authorizeEmailText.text.toString() == eMail && authorizePasswordText.text.toString() == password) {
                // Settings.putBoolean("Authorized", true).apply()
                startIntent(ChatList::class.java)
            }

            // Если почта или пароль не заполнены
            else if (authorizeEmailText.text.toString() == "" || authorizePasswordText.text.toString() == "") {
                showMessage("Ошибка", "Присутствуют пустые поля")
            }

            // Если почта или пароль не совпадают
            else if (authorizeEmailText.text.toString() != eMail || authorizePasswordText.text.toString() != password) {
                showMessage("Ошибка", "Неверные почта или пароль")
                authorizeEmailText.setText("")
                authorizePasswordText.setText("")
            }
        }
    }

    @Override
    override fun onBackPressed() {
        startIntent(RegistrationWindow::class.java)
    }

    private fun startIntent(Window: Class<*>?)
    {
        startActivity(Intent(this, Window))
    }

    private fun showMessage(TitleText: CharSequence, MessageText: CharSequence)
    {
        val message: AlertDialog.Builder = AlertDialog.Builder(this)
        message
            .setTitle(TitleText)
            .setMessage(MessageText)
            .setCancelable(true)
            .setPositiveButton(
                "Ок",
                DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })
        val messageWindow = message.create()
        messageWindow.show()
    }
}
