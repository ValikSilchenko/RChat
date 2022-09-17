package com.example.rchat.windows

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.rchat.R

class ResetPasswordWindow : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reset_password_window)

        val backBtn: ImageButton = findViewById(R.id.RPW_BackBtn)
        val sendEmailBtn: Button = findViewById(R.id.RPW_SendEmailBtn)
        val sendCodeBtn: Button = findViewById(R.id.RPW_SendCodeBtn)
        val sendPasswordBtn: Button = findViewById(R.id.RPW_SendNewPasswordBtn)
        val enterEmailED: EditText = findViewById(R.id.RPW_EmailED)
        val enterCodeED: EditText = findViewById(R.id.RPW_CodeED)
        val enterNewPasswordED: EditText = findViewById(R.id.RPW_NewPasswordED)
        val repeatNewPasswordED: EditText = findViewById(R.id.RPW_RepeatNewPasswordED)
        val acceptCodeContainer: ConstraintLayout = findViewById(R.id.RPW_AcceptCodeContainer)
        val newPasswordContainer: ConstraintLayout = findViewById(R.id.RPW_NewPasswordContainer)
        var isEmailEntered = false
        var isCodeEntered = false

        /* Нажатие кнопки выхода назад
        */
        backBtn.setOnClickListener {
            openNewWindow(AuthorizationWindow::class.java)
        }
    }

    /* Функция открытия нового окна
    */
    private fun openNewWindow(Window: Class<*>?) {
        startActivity(Intent(this, Window))
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        finish()
    }
}