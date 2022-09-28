package com.example.rchat.windows

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rchat.R
import com.example.rchat.utils.ChatFunctions

class ResetPasswordWindow : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        ChatFunctions().setAppTheme(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.reset_password_window)
    }

    /* Функция открытия нового окна
    */
    private fun openNewWindow(Window: Class<*>?) {
        startActivity(Intent(this, Window))
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        finish()
    }
}