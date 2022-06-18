package com.example.rchat

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SettingsWindow : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.Theme_Dark)
            Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.Theme_Light)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        val pref = getSharedPreferences("Account", Context.MODE_PRIVATE)
        val backBtn: Button = findViewById(R.id.SettingsChat_Btn)
        val exitAccBtn: Button = findViewById(R.id.ExitAcccount_Btn)

        backBtn.setOnClickListener {
            startActivity(Intent(this, ChatsWindow::class.java))
        }

        // Тестовая система выхода из аккаунта, не бейте, если не работает
//        exitAccBtn.setOnClickListener {
//            val messageBody = AlertDialog.Builder(this)
//            messageBody
//                .setTitle("Внимание")
//                .setMessage("Вы уверены, что хотите выйти из текущего аккаунта?")
//                .setCancelable(true)
//                .setNegativeButton("Нет") { dialog, _ -> dialog.cancel() }
//                .setPositiveButton("Да") { dialog, _ ->
//
//                    val editor = pref.edit()
//                    editor.remove("IsAuthorized")
//                    editor.remove("User Login")
//                    editor.apply()
//
//                    val intent = Intent(this, AuthorizationWindow::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                    startActivity(intent)
//                }
//            val messageWindow = messageBody.create()
//            messageWindow.show()
//        }
    }

    @Override
    override fun onBackPressed() {
        startActivity(Intent(this, ChatsWindow::class.java))
    }
}