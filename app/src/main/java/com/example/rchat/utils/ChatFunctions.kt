package com.example.rchat.utils

import android.app.NotificationManager
import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat

class ChatFunctions {
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

    fun showNotification(context: Context, login: String, message: String, id: Int) {
        val builder = NotificationCompat.Builder(context)
            .setContentTitle(login)
            .setContentText(message)
        // Добавить иконку уведомления
        val notification = builder.build()
        val notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notifManager.notify(id, notification)
    }
}