package com.example.rchat.windows

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.provider.MediaStore
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

        when (prefs.getString("NightMode", "Day")) {
            "Day" -> {
                setTheme(R.style.Theme_Light)
                uiMode = 0
            }
            "Night" -> {
                setTheme(R.style.Theme_Dark)
                uiMode = 1
            }
            "System" -> {
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
        val dayModeCBox: CheckBox = findViewById(R.id.SW_DayModeCB)
        val nightModeCBox: CheckBox = findViewById(R.id.SW_NightModeCB)
        val systemModeCBox: CheckBox = findViewById(R.id.SW_SystemModeCB)

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
            } else
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
            } else
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
            } else
                editor.apply {
                    putString("NightMode", "Day")
                }.apply()
        }

        backBtn.setOnClickListener {
            startIntent()
        }

        avatarBtn.setOnClickListener {
//            takePicFromAlbum()
            Toast.makeText(this, getString(R.string.wip_title), Toast.LENGTH_SHORT).show()
        }

        exitAccountBtn.setOnClickListener {
            try {
                if (ChatFunctions().isServiceRunning(
                        BackgroundService::class.java,
                        applicationContext
                    )
                )
                    stopService(Intent(applicationContext, BackgroundService::class.java))
                ChatFunctions().deleteData(this)
                val intent = Intent(this, AuthorizationWindow::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                overridePendingTransition(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
                )
            } catch (exception: Exception) {
                ChatFunctions().showMessage(
                    "Ошибка",
                    "Ошибка выхода из аккаунта. Код ошибки: ${exception.message}",
                    this
                )
            }
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

    fun takePicFromAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null)
            startActivityForResult(intent, 1)
    }

    fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null)
            startActivityForResult(intent, 0)
    }
}
