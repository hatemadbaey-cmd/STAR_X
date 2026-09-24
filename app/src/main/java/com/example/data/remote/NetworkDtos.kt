package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "username") val username: String,
    @Json(name = "password") val password: String,
    @Json(name = "csrf_token") val csrfToken: String? = "broadcast_csrf_token"
)

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "status") val status: String,
    @Json(name = "token") val token: String?,
    @Json(name = "username") val username: String?,
    @Json(name = "role") val role: String?,
    @Json(name = "message") val message: String?
)

@JsonClass(generateAdapter = true)
data class DeviceNetworkDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "modem_name") val modemName: String,
    @Json(name = "ip") val ip: String,
    @Json(name = "type") val type: String,
    @Json(name = "status") val status: String,
    @Json(name = "location") val location: String? = "",
    @Json(name = "notes") val notes: String? = ""
)

@JsonClass(generateAdapter = true)
data class GetDataResponse(
    @Json(name = "status") val status: String,
    @Json(name = "devices") val devices: List<DeviceNetworkDto>?,
    @Json(name = "total_devices") val totalDevices: Int?,
    @Json(name = "total_users") val totalUsers: Int?
)

@JsonClass(generateAdapter = true)
data class UpdateStatusRequest(
    @Json(name = "device_id") val deviceId: Long,
    @Json(name = "status") val status: String
)

@JsonClass(generateAdapter = true)
data class PingRequest(
    @Json(name = "ip") val ip: String
)

@JsonClass(generateAdapter = true)
data class PingResponse(
    @Json(name = "status") val status: String,
    @Json(name = "is_reachable") val isReachable: Boolean,
    @Json(name = "latency_ms") val latencyMs: Long?,
    @Json(name = "message") val message: String?
)

@JsonClass(generateAdapter = true)
data class BaseApiResponse(
    @Json(name = "status") val status: String,
    @Json(name = "message") val message: String?
)
