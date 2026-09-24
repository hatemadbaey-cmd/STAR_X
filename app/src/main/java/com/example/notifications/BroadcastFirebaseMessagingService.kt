package com.example.notifications

import android.util.Log
import com.example.BroadcastApp
import com.example.data.preferences.AppPreferences
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * خدمة استقبال إشعارات Firebase Cloud Messaging (FCM)
 * تتعامل مع التنبيهات الفورية من الخادم (PHP Backend) عند تغيير حالة جهاز، إضافة، أو فشل فحص Ping
 */
class BroadcastFirebaseMessagingService : FirebaseMessagingService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM Token: $token")
        // حفظ التوكن أو إرساله إلى خادم الـ PHP
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val app = applicationContext as? BroadcastApp ?: return
        val preferences = app.appPreferences
        val repository = app.repository

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "نظام البث"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: "تنبيه جديد من شبكة البث"

        val eventType = remoteMessage.data["type"] ?: "GENERAL"

        serviceScope.launch {
            // التحقق من تفضيلات المستخدم للإشعارات
            val shouldShow = when (eventType) {
                "DEVICE_ADDED" -> preferences.notifyDeviceAdded.first()
                "DEVICE_DELETED" -> preferences.notifyDeviceDeleted.first()
                "DEVICE_UPDATED" -> preferences.notifyDeviceUpdated.first()
                "STATUS_CHANGED" -> preferences.notifyStatusChanged.first()
                "PING_FAILED" -> preferences.notifyPingFailed.first()
                "USER_CHANGED" -> preferences.notifyUserChanges.first()
                else -> true
            }

            if (shouldShow) {
                val channel = when (eventType) {
                    "PING_FAILED" -> NotificationHelper.CHANNEL_ALERTS
                    "STATUS_CHANGED" -> NotificationHelper.CHANNEL_STATUS
                    else -> NotificationHelper.CHANNEL_GENERAL
                }

                app.notificationHelper.showNotification(
                    id = (System.currentTimeMillis() % 100000).toInt(),
                    title = title,
                    message = body,
                    channelId = channel,
                    soundEnabled = preferences.notifySound.first(),
                    vibrateEnabled = preferences.notifyVibrate.first()
                )
            }

            // تحديث البيانات المحلية للمزامنة الفورية
            repository.syncWithRemote()
        }
    }

    companion object {
        private const val TAG = "BroadcastFCMService"
    }
}
