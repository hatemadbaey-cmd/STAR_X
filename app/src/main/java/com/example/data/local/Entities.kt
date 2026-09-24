package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.domain.model.AuditLog
import com.example.domain.model.Device
import com.example.domain.model.DeviceStatus
import com.example.domain.model.DeviceType
import com.example.domain.model.NotificationItem
import com.example.domain.model.User
import com.example.domain.model.UserRole

@Entity(
    tableName = "devices",
    indices = [Index(value = ["ip"], unique = true)]
)
data class DeviceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val modemName: String,
    val ip: String,
    val type: String, // BROADCAST, RECEIVER, MODEM
    val status: String, // ONLINE, OFFLINE, MAINTENANCE, IN_HOUSE
    val location: String = "",
    val notes: String = "",
    val imageUri: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val lastPingMs: Long? = null,
    val lastPingSuccess: Boolean? = null
) {
    fun toDomain(): Device = Device(
        id = id,
        modemName = modemName,
        ip = ip,
        type = DeviceType.fromString(type),
        status = DeviceStatus.fromString(status),
        location = location,
        notes = notes,
        imageUri = imageUri,
        timestamp = timestamp,
        lastPingMs = lastPingMs,
        lastPingSuccess = lastPingSuccess
    )

    companion object {
        fun fromDomain(device: Device): DeviceEntity = DeviceEntity(
            id = device.id,
            modemName = device.modemName,
            ip = device.ip,
            type = device.type.name,
            status = device.status.name,
            location = device.location,
            notes = device.notes,
            imageUri = device.imageUri,
            timestamp = device.timestamp,
            lastPingMs = device.lastPingMs,
            lastPingSuccess = device.lastPingSuccess
        )
    }
}

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val username: String,
    val passwordHash: String,
    val role: String, // ADMIN, USER
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): User = User(
        id = id,
        username = username,
        role = UserRole.fromString(role),
        createdAt = createdAt
    )
}

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val username: String,
    val action: String,
    val ip: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): AuditLog = AuditLog(
        id = id,
        username = username,
        action = action,
        ip = ip,
        timestamp = timestamp
    )

    companion object {
        fun fromDomain(log: AuditLog): AuditLogEntity = AuditLogEntity(
            id = log.id,
            username = log.username,
            action = log.action,
            ip = log.ip,
            timestamp = log.timestamp
        )
    }
}

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val message: String,
    val type: String = "INFO",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
) {
    fun toDomain(): NotificationItem = NotificationItem(
        id = id,
        title = title,
        message = message,
        type = type,
        timestamp = timestamp,
        isRead = isRead
    )

    companion object {
        fun fromDomain(n: NotificationItem): NotificationEntity = NotificationEntity(
            id = n.id,
            title = n.title,
            message = n.message,
            type = n.type,
            timestamp = n.timestamp,
            isRead = n.isRead
        )
    }
}
