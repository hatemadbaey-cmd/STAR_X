package com.example.domain.model

enum class UserRole(val titleAr: String) {
    ADMIN("مدير النظام"),
    USER("فني شبكة");

    companion object {
        fun fromString(value: String): UserRole {
            return entries.firstOrNull { 
                it.name.equals(value, ignoreCase = true) || it.titleAr == value 
            } ?: USER
        }
    }
}

data class User(
    val id: Long = 0L,
    val username: String,
    val role: UserRole = UserRole.USER,
    val createdAt: Long = System.currentTimeMillis()
)

data class AuditLog(
    val id: Long = 0L,
    val username: String,
    val action: String,
    val ip: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class NotificationItem(
    val id: Long = 0L,
    val title: String,
    val message: String,
    val type: String = "INFO",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
