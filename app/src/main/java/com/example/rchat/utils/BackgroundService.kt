package com.example.rchat.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.rchat.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class BackgroundService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val builder = NotificationCompat.Builder(applicationContext, "serviceID")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("R Chat")
            .setContentText("Приложение запущено в фоне")
            .setPriority(NotificationCompat.PRIORITY_MIN)
        GlobalScope.async {
            ChatSingleton.openConnection(ChatFunctions().getSavedLogin(applicationContext)) //!
        }
        startForeground(-1, builder.build())
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        ChatSingleton.closeConnection() //!
        stopForeground(true)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_MIN
            val channel = NotificationChannel("serviceID", "Background working", importance).apply {
                description = "notification description"
            }
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}