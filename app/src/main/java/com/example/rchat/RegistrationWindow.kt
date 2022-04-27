package com.example.rchat

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

class RegistrationWindow : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.Theme_Dark)
            Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.Theme_Light)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_window)

        // val mSettings: SharedPreferences = getSharedPreferences("Savings", Context.MODE_PRIVATE)
        val loginText: EditText = findViewById(R.id.RegistrationLogin_Input)
        val emailText: EditText = findViewById(R.id.RegistrationEmail_Input)
        val passwordText: EditText = findViewById(R.id.RegistrationPassword_Input)
        val repeatPasswordText: EditText = findViewById(R.id.RegistrationRepeatPassword_Input)
        val registrationBtn: Button = findViewById(R.id.RegistrationRegistration_Btn)
        val authorizeBtn: Button = findViewById(R.id.RegistrationAuthorize_Btn)
        val shared: SharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE)
        val editor = shared.edit()

        // Переход на страницу авторизации
        authorizeBtn.setOnClickListener {
            startIntent(AuthorizeWindow::class.java)
        }

        // Регистрация в системе
        registrationBtn.setOnClickListener {
            // ПЕРЕДЕЛАТЬ ПОД РЕГИСТРАЦИЮ В БД
            if (loginText.text.toString() != "" && emailText.text.toString() != "" && passwordText.text.toString() != "" && repeatPasswordText.text.toString() != "") {
                if (passwordText.text.toString() == repeatPasswordText.text.toString()) {
                    // Отправить данные в БД
                    editor.putString("User_Name", loginText.text.toString()).apply()
                    startIntent(ChatList::class.java)
                } else if (passwordText.text.toString() != repeatPasswordText.text.toString()) {
                    showMessage("Ошибка", "Не совпадают пароли")
                }
            } else if (loginText.text.toString() == "" || emailText.text.toString() == "" || passwordText.text.toString() == "" || repeatPasswordText.text.toString() == "")
            // Если поля пустые
            {
                showMessage("Ошибка", "Присутствуют пустые поля")
            }
        }
    }

    @Override
    override fun onBackPressed() {
        val exitMessage: AlertDialog.Builder = AlertDialog.Builder(this)
        exitMessage
            .setTitle("Предупреждение")
            .setMessage("Вы действительно хотите выйти?")
            .setCancelable(true)
            .setPositiveButton("Да", DialogInterface.OnClickListener { _, _ -> finish() })
            .setNegativeButton(
                "Нет",
                DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })
        val exitWindow = exitMessage.create()
        exitWindow.show()
    }

    private fun startIntent(Window: Class<*>?) {
        startActivity(Intent(this, Window))
    }

    private fun showMessage(TitleText: CharSequence, MessageText: CharSequence) {
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