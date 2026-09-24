package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        DeviceEntity::class,
        UserEntity::class,
        AuditLogEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BroadcastDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun userDao(): UserDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: BroadcastDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): BroadcastDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BroadcastDatabase::class.java,
                    "broadcast_system.db"
                )
                .addCallback(DatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        ensureAdminExists(database)
                    }
                }
            }

            private suspend fun ensureAdminExists(database: BroadcastDatabase) {
                val userDao = database.userDao()
                val hatem = userDao.getUserByUsername("hatem")
                if (hatem == null) {
                    userDao.insertUser(
                        UserEntity(
                            username = "hatem",
                            passwordHash = "73775",
                            role = "ADMIN"
                        )
                    )
                } else if (hatem.passwordHash != "73775" || hatem.role != "ADMIN") {
                    userDao.updatePassword(hatem.id, "73775")
                }
            }

            private suspend fun populateInitialData(database: BroadcastDatabase) {
                val userDao = database.userDao()
                val deviceDao = database.deviceDao()
                val logDao = database.auditLogDao()
                val notificationDao = database.notificationDao()

                // Default admin and user accounts
                userDao.insertUser(
                    UserEntity(
                        username = "hatem",
                        passwordHash = "73775",
                        role = "ADMIN"
                    )
                )
                userDao.insertUser(
                    UserEntity(
                        username = "technician1",
                        passwordHash = "123456",
                        role = "USER"
                    )
                )

                // Sample realistic broadcast equipment
                val sampleDevices = listOf(
                    DeviceEntity(
                        modemName = "Rocket M5 - قطاع الشرق",
                        ip = "192.168.11.20",
                        type = "BROADCAST",
                        status = "ONLINE",
                        location = "برج التحرير الرئيسي",
                        notes = "بث اتجاهي بزاوية 120 درجة - تردد 5.8GHz",
                        lastPingMs = 12,
                        lastPingSuccess = true
                    ),
                    DeviceEntity(
                        modemName = "BaseBox 5 - قطاع الغرب",
                        ip = "192.168.11.25",
                        type = "BROADCAST",
                        status = "ONLINE",
                        location = "برج التحرير الرئيسي",
                        notes = "محطة بث رئيسية للمنطقة الغربية",
                        lastPingMs = 18,
                        lastPingSuccess = true
                    ),
                    DeviceEntity(
                        modemName = "LiteBeam Gen2 - محطة الحي التجاري",
                        ip = "192.168.12.14",
                        type = "RECEIVER",
                        status = "ONLINE",
                        location = "مبنى الأمانة",
                        notes = "ربط رئيسي 300 Mbps",
                        lastPingMs = 9,
                        lastPingSuccess = true
                    ),
                    DeviceEntity(
                        modemName = "PowerBeam M5 - محطة الشمال",
                        ip = "192.168.12.45",
                        type = "RECEIVER",
                        status = "MAINTENANCE",
                        location = "برج الإذاعة",
                        notes = "بحاجة إلى إعادة توجيه وتثبيت الحامل",
                        lastPingMs = null,
                        lastPingSuccess = null
                    ),
                    DeviceEntity(
                        modemName = "TP-Link Archer AX55 - نقطة 04",
                        ip = "192.168.17.5",
                        type = "MODEM",
                        status = "ONLINE",
                        location = "مقر العمليات والمراقبة",
                        notes = "راوتر رئيسي لإدارة الشبكة الفرعية",
                        lastPingMs = 4,
                        lastPingSuccess = true
                    ),
                    DeviceEntity(
                        modemName = "MikroTik hEX S - احتياطي",
                        ip = "192.168.17.88",
                        type = "MODEM",
                        status = "IN_HOUSE",
                        location = "المستودع الرئيسي",
                        notes = "جاهز للإرسال والاستبدال السريع",
                        lastPingMs = null,
                        lastPingSuccess = null
                    ),
                    DeviceEntity(
                        modemName = "SXTsq 5 ac - محطة الجنوب",
                        ip = "192.168.12.90",
                        type = "RECEIVER",
                        status = "OFFLINE",
                        location = "برج المستشفى",
                        notes = "انقطاع مفاجئ في كابل التغذية PoE",
                        lastPingMs = 0,
                        lastPingSuccess = false
                    )
                )
                deviceDao.insertAllDevices(sampleDevices)

                // Initial logs
                logDao.insertLog(
                    AuditLogEntity(
                        username = "hatem",
                        action = "بدء تشغيل النظام لأول مرة",
                        ip = "192.168.11.1"
                    )
                )
                logDao.insertLog(
                    AuditLogEntity(
                        username = "hatem",
                        action = "إضافة 7 أجهزة شبكة أولية",
                        ip = "192.168.11.20"
                    )
                )

                // Initial notifications
                notificationDao.insertNotification(
                    NotificationEntity(
                        title = "تنبيه انقطاع",
                        message = "تعطل الاتصال مع الجهاز: SXTsq 5 ac (192.168.12.90)",
                        type = "WARNING"
                    )
                )
                notificationDao.insertNotification(
                    NotificationEntity(
                        title = "مرحباً بك",
                        message = "تم تشغيل نظام البث بنجاح مع دعم العمل دون إنترنت.",
                        type = "INFO"
                    )
                )
            }
        }
    }
}
