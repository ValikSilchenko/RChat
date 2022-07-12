package com.example.rchat.windows

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.rchat.R
import com.example.rchat.utils.BackgroundService
import com.example.rchat.utils.ChatFunctions
import com.example.rchat.utils.ChatSingleton
import com.example.rchat.utils.Requests

class AuthorizationWindow : AppCompatActivity() {
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
        setContentView(R.layout.authorize_window)

        if (ChatFunctions().isAuthorized(this)) {
            login = ChatFunctions().getSavedLogin(this)
            if (!ChatFunctions().isServiceRunning(
                    BackgroundService::class.java,
                    applicationContext
                )
            )
                startService(Intent(applicationContext, BackgroundService::class.java))
            startIntent(ChatsWindow::class.java)
        }

        val authorizeLoginText: EditText = findViewById(R.id.AW_LoginET)
        val authorizePasswordText: EditText = findViewById(R.id.AW_PasswordET)
        val enterAccountBtn: Button = findViewById(R.id.AW_AuthorizeBtn)
        val noBitches: Button = findViewById(R.id.AW_NoAccountBtn)

        noBitches.setOnClickListener {
            val intent = Intent(this, RegistrationWindow::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(intent)
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
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
                        "${getString(R.string.error_of_sending_data_title)} ${exception.message}",
                        this
                    )
                }
            } else
                ChatFunctions().showMessage(
                    getString(R.string.attention_title),
                    getString(R.string.check_correct_title),
                    this
                )
        }
    }

    @Override
    override fun onBackPressed() {
        val message: AlertDialog.Builder = AlertDialog.Builder(this)
        message
            .setTitle(getString(R.string.attention_title))
            .setMessage(getString(R.string.really_wanna_exit_app_title))
            .setCancelable(true)
            .setPositiveButton(
                getString(R.string.yes_title)
            ) { _, _ -> finish() }
            .setNegativeButton(getString(R.string.no_title)) { dialog, _ -> dialog.cancel() }
        val messageWindow = message.create()
        messageWindow.show()
    }

    private fun startIntent(Window: Class<*>?) {
        startActivity(Intent(this, Window))
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        finish()
    }
}
