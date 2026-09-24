package com.example

import android.app.Application
import com.example.data.local.BroadcastDatabase
import com.example.data.preferences.AppPreferences
import com.example.data.remote.ApiClient
import com.example.data.repository.BroadcastRepository
import com.example.notifications.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class BroadcastApp : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    lateinit var database: BroadcastDatabase
        private set

    lateinit var appPreferences: AppPreferences
        private set

    lateinit var notificationHelper: NotificationHelper
        private set

    lateinit var repository: BroadcastRepository
        private set

    override fun onCreate() {
        super.onCreate()

        database = BroadcastDatabase.getDatabase(this, applicationScope)
        appPreferences = AppPreferences(this)
        notificationHelper = NotificationHelper(this)

        val apiService = ApiClient.createService()

        repository = BroadcastRepository(
            context = this,
            deviceDao = database.deviceDao(),
            userDao = database.userDao(),
            auditLogDao = database.auditLogDao(),
            notificationDao = database.notificationDao(),
            apiService = apiService,
            appPreferences = appPreferences,
            notificationHelper = notificationHelper
        )

        // جدولة المزامنة التلقائية الدورية في الخلفية
        com.example.data.sync.SyncDevicesWorker.schedulePeriodicSync(this)
    }
}
