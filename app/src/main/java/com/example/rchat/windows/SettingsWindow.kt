package com.example.rchat.windows

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.example.rchat.R
import com.example.rchat.utils.BackgroundService
import com.example.rchat.utils.ChatFunctions
import com.example.rchat.utils.ChatSingleton

/* Оконный класс окна настроек
*/
class SettingsWindow : AppCompatActivity() {

    private val avatarImg: ImageView? = null
    private lateinit var dayModeCBox: CheckBox
    private lateinit var nightModeCBox: CheckBox
    private lateinit var systemModeCBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {

        val prefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        var uiMode = 0

        /* Установка темы приложения
        */
        when (prefs.getString("NIGHTLIFE_KEY", "Day")) {
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

        val backBtn: ImageButton = findViewById(R.id.SW_ToChatsBtn)
        val avatarBtn: Button = findViewById(R.id.SW_AvatarBtn)
        val exitAccountBtn: Button = findViewById(R.id.SW_ExitAccountBtn)
        val langSpinner: Spinner = findViewById(R.id.SW_LangSpinner)
        val notificationsOnTV: TextView = findViewById(R.id.SW_NotifOnTextView)
        val notificationsOffTV: TextView = findViewById(R.id.SW_NotifOffTextView)
        val notificationsSwitch: SwitchCompat = findViewById(R.id.SW_NotificationsSC)
        dayModeCBox = findViewById(R.id.SW_DayModeCB)
        nightModeCBox = findViewById(R.id.SW_NightModeCB)
        systemModeCBox = findViewById(R.id.SW_SystemModeCB)

        val languages = arrayOf("En", "Ru")
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, languages)
        langSpinner.adapter = arrayAdapter

        /* Настройка "темовых" CheckBox в зависимости от темы приложения при открытии окна
        */
        when (uiMode) {
            0 -> {
                setThemeCheckBoxes(dayCB = true, nightCB = false, systemCB = false)
            }
            1 -> {
                setThemeCheckBoxes(dayCB = false, nightCB = true, systemCB = false)
            }
            2 -> {
                setThemeCheckBoxes(dayCB = false, nightCB = false, systemCB = true)
            }
        }

        /* Настройка ползунка уведомлений при открытии окна
        */
        when (prefs.getBoolean("NOTIFICATIONS_KEY", true)) {
            true -> {
                notificationsOffTV.visibility = View.INVISIBLE
                notificationsOnTV.visibility = View.VISIBLE
                notificationsSwitch.isChecked = true
                ChatSingleton.isNotificationOn = true
            }
            false -> {
                notificationsOffTV.visibility = View.VISIBLE
                notificationsOnTV.visibility = View.INVISIBLE
                notificationsSwitch.isChecked = false
                ChatSingleton.isNotificationOn = false
            }
        }

        /* Установка светлой темы
        */
        dayModeCBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setThemeCheckBoxes(dayCB = true, nightCB = false, systemCB = false)
                editor.apply {
                    putString("NIGHTLIFE_KEY", "Day")
                }.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else
                editor.apply {
                    putString("NIGHTLIFE_KEY", "Day")
                }.apply()
        }

        /* Установка темной темы
        */
        nightModeCBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setThemeCheckBoxes(dayCB = false, nightCB = true, systemCB = false)
                editor.apply {
                    putString("NIGHTLIFE_KEY", "Night")
                }.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else
                editor.apply {
                    putString("NIGHTLIFE_KEY", "Day")
                }.apply()
        }

        /* Установка системной темы
        */
        systemModeCBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setThemeCheckBoxes(dayCB = false, nightCB = false, systemCB = true)
                editor.apply {
                    putString("NIGHTLIFE_KEY", "System")
                }.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            } else
                editor.apply {
                    putString("NIGHTLIFE_KEY", "Day")
                }.apply()
        }

        /* Нажатие кнопки возврата
        */
        backBtn.setOnClickListener {
            startIntent()
        }

        /* Нажатие кнопки смены аватара
        */
        avatarBtn.setOnClickListener {
            Toast.makeText(this, getString(R.string.wip_title), Toast.LENGTH_SHORT).show()
        }

        /* Нажатие кнопки выхода из аккаунта
        */
        exitAccountBtn.setOnClickListener {
            val message: AlertDialog.Builder = AlertDialog.Builder(this)
            message
                .setTitle(getString(R.string.attention_title))
                .setMessage(getString(R.string.really_wanna_exit_account_title))
                .setCancelable(true)
                .setPositiveButton(
                    getString(R.string.yes_title)
                ) { _, _ ->
                    try {
                        exitAccount()
                    } catch (exception: Exception) {
                        ChatFunctions().showMessage(
                            getString(R.string.error_title),
                            "${getString(R.string.error_exit_acc_title)} ${exception.message}",
                            this
                        )
                    }
                }
                .setNegativeButton(getString(R.string.no_title)) { dialog, _ -> dialog.cancel() }
            val messageWindow = message.create()
            messageWindow.show()
        }

        /* Переключение ползунка уведомлений
        */
        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                notificationsOffTV.visibility = View.INVISIBLE
                notificationsOnTV.visibility = View.VISIBLE
                editor.apply {
                    putBoolean("NOTIFICATIONS_KEY", true)
                }.apply()
                ChatSingleton.isNotificationOn = true
            }
            else {
                notificationsOnTV.visibility = View.INVISIBLE
                notificationsOffTV.visibility = View.VISIBLE
                editor.apply {
                    putBoolean("NOTIFICATIONS_KEY", false)
                }.apply()
                ChatSingleton.isNotificationOn = false
            }
        }

//        langSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
//                val config = resources.configuration
//                val lang = languages[position]
//                val locale = Locale(lang)
//                Locale.setDefault(locale)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
//                    config.setLocale(locale)
//                else
//                    config.locale = locale
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
//                    createConfigurationContext(config)
//                resources.updateConfiguration(config, resources.displayMetrics)
//
//                setContentView(R.layout.settings_window)
//            }
//
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//                TODO("Not yet implemented")
//            }
//        }
    }

    @Override
    override fun onBackPressed() {
        startIntent()
    }

    /* Функция возврата на предыдущее окно
    */
    private fun startIntent() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }

    /* Функция выхода из аккаунта и очистки всех данных аккаунта в приложении
    */
    private fun exitAccount() {
        if (ChatFunctions().isServiceRunning(BackgroundService::class.java, applicationContext))
            stopService(Intent(applicationContext, BackgroundService::class.java))
        ChatFunctions().deleteData(this)
        ChatSingleton.apply {
            deleteNotification()
            clearChatsList()
            clearMessagesList()
        }
        val intent = Intent(this, AuthorizationWindow::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }

    private fun setThemeCheckBoxes(dayCB: Boolean, nightCB: Boolean, systemCB: Boolean)
    {
        dayModeCBox.isChecked = dayCB
        nightModeCBox.isChecked = nightCB
        systemModeCBox.isChecked = systemCB
    }
}
