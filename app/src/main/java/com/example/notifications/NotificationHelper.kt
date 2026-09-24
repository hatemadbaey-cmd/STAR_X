package com.example.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_STATUS = "channel_status_change"
        const val CHANNEL_ALERTS = "channel_ping_alerts"
        const val CHANNEL_GENERAL = "channel_general"
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val statusChannel = NotificationChannel(
                CHANNEL_STATUS,
                "تغييرات حالة الأجهزة",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعارات عند تغيير حالة جهاز أو فصله عن الشبكة"
                enableVibration(true)
            }

            val alertsChannel = NotificationChannel(
                CHANNEL_ALERTS,
                "تنبيهات فحص الاتصال (Ping)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "تنبيهات عند فشل الاتصال مع أي جهاز أو برج"
                enableVibration(true)
            }

            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL,
                "إشعارات عامة والعمليات",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "إشعارات إضافة الأجهزة والتعديلات"
            }

            notificationManager.createNotificationChannels(
                listOf(statusChannel, alertsChannel, generalChannel)
            )
        }
    }

    fun showNotification(
        id: Int,
        title: String,
        message: String,
        channelId: String = CHANNEL_GENERAL,
        soundEnabled: Boolean = true,
        vibrateEnabled: Boolean = true
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (vibrateEnabled) {
            builder.setVibrate(longArrayOf(0, 300, 200, 300))
        }

        NotificationManagerCompat.from(context).notify(id, builder.build())
    }
}
