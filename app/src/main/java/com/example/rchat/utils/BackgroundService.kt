package com.example.rchat.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.rchat.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class BackgroundService : Service() {

    lateinit var notificationManager: NotificationManager
    lateinit var mHandler: Handler
    lateinit var mRunnable: Runnable

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val builder = NotificationCompat.Builder(applicationContext, "serviceID")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(R.string.app_name.toString())
            .setContentText("Приложение запущено в фоне")
            .setPriority(NotificationCompat.PRIORITY_MIN)

        val notification = builder.build()
        GlobalScope.async {
            ChatSingleton.openConnection(ChatFunctions().getSavedLogin(applicationContext))
        }
        startForeground(-1, notification)

//        mHandler = Handler()
//        mRunnable = Runnable { openCloseConnection() }
//        mHandler.postDelayed(mRunnable, 500)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        ChatSingleton.closeConnection()
        stopForeground(true)
//        mHandler.removeCallbacks(mRunnable)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_MIN
            val channel = NotificationChannel("serviceID", "Background working", importance).apply {
                description = "Notification of background working of R Chat"
            }
            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

//    private fun openCloseConnection() {
//        Toast.makeText(applicationContext, "Connection opened", Toast.LENGTH_SHORT).show()
//        Thread.sleep(10000)
//        Toast.makeText(applicationContext, "Connection closed", Toast.LENGTH_SHORT).show()
//        mHandler.postDelayed(mRunnable, 10000)
//    }
}