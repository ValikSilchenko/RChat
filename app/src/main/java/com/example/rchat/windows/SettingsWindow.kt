package com.example.rchat.windows

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.rchat.R
import com.example.rchat.utils.BackgroundService
import com.example.rchat.utils.ChatFunctions
import com.example.rchat.utils.ChatSingleton

class SettingsWindow : AppCompatActivity() {

    private val avatarImg: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        val prefs = getSharedPreferences("Night Mode", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        var uiMode = 0

        when {
            prefs.getString("NightMode", "Day") == "Day" -> {
                setTheme(R.style.Theme_Light)
                uiMode = 0
            }
            prefs.getString("NightMode", "Day") == "Night" -> {
                setTheme(R.style.Theme_Dark)
                uiMode = 1
            }
            prefs.getString("NightMode", "Day") == "System" -> {
                when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.Theme_Dark)
                    Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.Theme_Light)
                }
                uiMode = 2
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_window)

        val avatarBtn: Button = findViewById(R.id.SW_AvatarBtn)
        val backBtn: ImageButton = findViewById(R.id.SW_ToChatsBtn)
        val exitAccountBtn: Button = findViewById(R.id.SW_ExitAccountBtn)
        val dayModeCBox: CheckBox = findViewById(R.id.SW_DayMode_CheckBox)
        val nightModeCBox: CheckBox = findViewById(R.id.SW_NightMode_CheckBox)
        val systemModeCBox: CheckBox = findViewById(R.id.SW_SystemMode_CheckBox)

        when (uiMode) {
            0 -> {
                dayModeCBox.isChecked = true
                nightModeCBox.isChecked = false
                systemModeCBox.isChecked = false
            }
            1 -> {
                dayModeCBox.isChecked = false
                nightModeCBox.isChecked = true
                systemModeCBox.isChecked = false
            }
            2 -> {
                dayModeCBox.isChecked = false
                nightModeCBox.isChecked = false
                systemModeCBox.isChecked = true
            }
        }

        dayModeCBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                nightModeCBox.isChecked = false
                systemModeCBox.isChecked = false
                editor.apply {
                    putString("NightMode", "Day")
                }.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            else
                editor.apply {
                    putString("NightMode", "Day")
                }.apply()
        }

        nightModeCBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                dayModeCBox.isChecked = false
                systemModeCBox.isChecked = false
                editor.apply {
                    putString("NightMode", "Night")
                }.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else
                editor.apply {
                    putString("NightMode", "Day")
                }.apply()
        }

        systemModeCBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                dayModeCBox.isChecked = false
                nightModeCBox.isChecked = false
                editor.apply {
                    putString("NightMode", "System")
                }.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            else
                editor.apply {
                    putString("NightMode", "Day")
                }.apply()
        }

        backBtn.setOnClickListener {
            startIntent()
        }

        avatarBtn.setOnClickListener {
//            pickAndSetImage()
            Toast.makeText(this, getString(R.string.wip_title), Toast.LENGTH_SHORT).show()
        }

        exitAccountBtn.setOnClickListener {
            if (ChatFunctions().isServiceRunning(BackgroundService::class.java, applicationContext))
                stopService(Intent(applicationContext, BackgroundService::class.java))
            ChatFunctions().deleteData(this)
            val intent = Intent(this, AuthorizationWindow::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ChatSingleton.ImgRequestCode) {
            avatarImg?.setImageURI(data?.data)
        }
    }

    @Override
    override fun onBackPressed() {
        startIntent()
    }

    private fun startIntent() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }

    private fun pickAndSetImage() {
    }
}
