package com.example.domain.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.StatusInHouse
import com.example.ui.theme.StatusMaintenance
import com.example.ui.theme.StatusOffline
import com.example.ui.theme.StatusOnline
import com.example.ui.theme.TypeBroadcastBg
import com.example.ui.theme.TypeBroadcastText
import com.example.ui.theme.TypeModemBg
import com.example.ui.theme.TypeModemText
import com.example.ui.theme.TypeReceiverBg
import com.example.ui.theme.TypeReceiverText

enum class DeviceType(
    val titleAr: String,
    val defaultPrefix: String,
    val bgColor: Color,
    val textColor: Color
) {
    BROADCAST("بث", "192.168.11.", TypeBroadcastBg, TypeBroadcastText),
    RECEIVER("لاقط", "192.168.12.", TypeReceiverBg, TypeReceiverText),
    MODEM("مودم", "192.168.17.", TypeModemBg, TypeModemText);

    companion object {
        fun fromString(value: String): DeviceType {
            return entries.firstOrNull { 
                it.name.equals(value, ignoreCase = true) || it.titleAr == value 
            } ?: BROADCAST
        }
    }
}

enum class DeviceStatus(
    val titleAr: String,
    val color: Color
) {
    ONLINE("متصل", StatusOnline),
    OFFLINE("غير متصل", StatusOffline),
    MAINTENANCE("في الصيانة", StatusMaintenance),
    IN_HOUSE("في البيت", StatusInHouse);

    companion object {
        fun fromString(value: String): DeviceStatus {
            return entries.firstOrNull { 
                it.name.equals(value, ignoreCase = true) || it.titleAr == value 
            } ?: ONLINE
        }
    }
}

data class Device(
    val id: Long = 0L,
    val modemName: String,
    val ip: String,
    val type: DeviceType,
    val status: DeviceStatus,
    val location: String = "",
    val notes: String = "",
    val imageUri: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val lastPingMs: Long? = null,
    val lastPingSuccess: Boolean? = null
)

data class PingResult(
    val isSuccess: Boolean,
    val latencyMs: Long,
    val message: String
)
