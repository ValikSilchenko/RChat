package com.example.rchat.windows

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.rchat.R
import com.example.rchat.utils.ChatFunctions

class ResetPasswordWindow : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        ChatFunctions().setAppTheme(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.reset_password_window)

        val backBtn: ImageButton = findViewById(R.id.RPW_BackBtn)

        backBtn.setOnClickListener {
            ChatFunctions().openNewWindow(this, AuthorizationWindow::class.java, shouldBeFinished = true)
        }
    }
}