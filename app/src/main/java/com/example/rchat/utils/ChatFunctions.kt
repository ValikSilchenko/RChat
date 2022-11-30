package com.example.rchat.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AlertDialog
import com.example.rchat.R

/* Утилитный класс функций, используемых в различных других классах
*/
class ChatFunctions {
    /* Функция показа всплывающего окна
        Вызывается в ChatSingleton.kt  в методе sendMessage()
     */
    fun showMessage(titleText: CharSequence, messageText: CharSequence, context: Context) {
        val message: AlertDialog.Builder = AlertDialog.Builder(context)
        message
            .setTitle(titleText)
            .setMessage(messageText)
            .setCancelable(true)
            .setPositiveButton(
                context.getString(R.string.ok_title)
            ) { dialog, _ -> dialog.cancel() }
        val messageWindow = message.create()
        messageWindow.show()
    }

    /* Функция проверки, авторизован ли пользователь
        Вызывается в AuthorizationWindow.kt в методе onCreate() и в SplashScreenWindow.kt в методе onCreate()
     */
    fun isAuthorized(context: Context): Boolean {
        val prefs = context.getSharedPreferences("Authorization", Context.MODE_PRIVATE)
        val isAuthorized = prefs.getBoolean("IS_AUTHORIZED_KEY", false)
        if (isAuthorized)
            return true
        return false
    }

    /* Функция сохранения логина и состояния авторизованности пользователя ("IS_AUTHORIZED_KEY")
        Вызывается в AuthorizationWindow.kt в методе onCreate() при нажатии кнопки входа в аккаунт
        и в RegistrationWindow.kt в методе OnCreate() при нажатии кнопки регистрации
     */
    fun saveLogin(context: Context, stringToSave: String, isAuthorized: Boolean) {
        val prefs = context.getSharedPreferences("Authorization", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.apply {
            putString("LOGIN_KEY", stringToSave)
            putBoolean("IS_AUTHORIZED_KEY", isAuthorized)
        }.apply()
    }

    /* Функция сохранения айди пользователя
        Вызывается в RegistrationWindow.kt при нажатии кнопки регистрации и в AuthorizationWindow.kt при нажатии кнопки авторизации
     */
    fun saveUserID(context: Context, userID: Int) {
        val prefs = context.getSharedPreferences("Authorization", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putInt("USER_ID_KEY", userID).apply()
    }

    /* Функция возврата сохраненного логина
        Вызывается в BackgroundService.kt в методе openCloseConnection(), в AuthorizationWindow.kt в методе onCreate(),
        в ChatsWindow.kt в методе onCreate() и в SplashScreenWindow.kt в методе onCreate()
     */
    fun getSavedLogin(context: Context): String {
        val prefs = context.getSharedPreferences("Authorization", Context.MODE_PRIVATE)
        return prefs.getString("LOGIN_KEY", "NaN").toString()
    }

    /* Функция возврата сохраненного айдишника
        Вызывается в PreviewChatRVAdapter.kt в методе init
    */
    fun getSavedUserID(context: Context): Int {
        val prefs = context.getSharedPreferences("Authorization", Context.MODE_PRIVATE)
        return prefs.getInt("USER_ID_KEY", -1)
    }

    /* Функция удаления всех сохраненных данных
        Вызывается в SettingsWindow.kt в методе exitAccount()
     */
    fun deleteData(context: Context) {
        val authPrefs = context.getSharedPreferences("Authorization", Context.MODE_PRIVATE)
        val authEditor = authPrefs.edit()
        authEditor.apply {
            remove("LOGIN_KEY")
            remove("IS_AUTHORIZED_KEY")
            remove("USER_ID_KEY")
        }.apply()
        val settingsPrefs = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val settingsEditor = settingsPrefs.edit()
        settingsEditor.apply {
            remove("NIGHTLIFE")
            remove("NOTIFICATIONS_KEY")
        }.apply()
    }

    /* Функция проверки, работает ли фоновый процесс
        Вызывается в AuthorizationWindow.kt в методе onCreate(), в SettingsWindow.kt в методе exitAccount()
        и в SplashScreenWindow.kt в методе onCreate() в момент истечения времени показа этого окна
     */
    fun isServiceRunning(serviceClass: Class<*>, context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    /* Функция преобразования телефонного номера
        Вызывается в RegistrationWindow в методе onCreate() при нажатии кнопки регистрации пользователя
     */
    fun transformPhoneNumber(number: String): String {
        var result = ""
        if (number.length > 10) {
            val filteredNumber = number.filter { it.isDigit() }
            if (filteredNumber.length == 11)
                result = filteredNumber.dropWhile {
                    it == '7' || it == '8'
                }
        } else if (number.length == 10)
            return number
        return result
    }

    /* Функция проверки интернет-соединения
        Вызывается в AuthorizationWindow() в методе onCreate() при нажатии кнопки входа в аккаунт
        и в RegistrationWindow.kt в методе onCreate() при нажатии кнопки регистрации
     */
    fun isInternetAvailable(context: Context): Boolean {
        if (context == null)
            return false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ->
                        return true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ->
                        return true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ->
                        return true
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }

    /* Функция установления темы приложения
        Вызывается во всех окнах, кроме SettingsWindow.kt - там расширенная версия этой функции
     */
    fun setAppTheme(context: Context) {
        val prefs = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        when (prefs.getString("NIGHTLIFE_KEY", "Day")) {
            "Day" -> context.setTheme(R.style.Theme_Light)
            "Night" -> context.setTheme(R.style.Theme_Dark)
            "System" -> {
                when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_YES -> context.setTheme(R.style.Theme_Dark)
                    Configuration.UI_MODE_NIGHT_NO -> context.setTheme(R.style.Theme_Light)
                }
            }
        }
    }

    fun openNewWindow(Activity: Activity, Window: Class<*>?, shouldBeFinished: Boolean) {
        Activity.startActivity(Intent(Activity, Window))
        Activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        if (shouldBeFinished) {
            Activity.finish()
        }
    }
}