package com.example.rchat.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog

class ChatFunctions {

    private var symbols: Array<Char> = arrayOf('+', '-', '(', ')', '.')
    fun showMessage(titleText: CharSequence, messageText: CharSequence, context: Context) {
        val message: AlertDialog.Builder = AlertDialog.Builder(context)
        message
            .setTitle(titleText)
            .setMessage(messageText)
            .setCancelable(true)
            .setPositiveButton(
                "Ок"
            ) { dialog, _ -> dialog.cancel() }
        val messageWindow = message.create()
        messageWindow.show()
    }

    fun isAuthorized(context: Context): Boolean {
        val prefs = context.getSharedPreferences("Authorization", Context.MODE_PRIVATE)
        val isAuthorized = prefs.getBoolean("IS_AUTHORIZED_KEY", false)
        if (isAuthorized)
            return true
        return false
    }

    fun saveData(context: Context, stringToSave: String, isAuthorized: Boolean) {
        val prefs = context.getSharedPreferences("Authorization", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.apply {
            putString("LOGIN_KEY", stringToSave)
            putBoolean("IS_AUTHORIZED_KEY", isAuthorized)
        }.apply()
    }

    fun getSavedLogin(context: Context): String {
        val prefs = context.getSharedPreferences("Authorization", Context.MODE_PRIVATE)
        return prefs.getString("LOGIN_KEY", "NaN").toString()
    }

    fun deleteData(context: Context) {
        val prefs = context.getSharedPreferences("Authorization", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.apply {
            remove("LOGIN_KEY")
            remove("IS_AUTHORIZED_KEY")
        }.apply()
    }

    fun transformPhoneNumber(phoneNumber: String): String {
        var number = phoneNumber.filter { it.isDigit() }
        if (number.length == 12)
            number.drop(2)
        else
            number.drop(1)
        return number
    }
}