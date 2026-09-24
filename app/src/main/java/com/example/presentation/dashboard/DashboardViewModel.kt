package com.example.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.BroadcastRepository
import com.example.domain.model.Device
import com.example.domain.model.DeviceStatus
import com.example.domain.model.DeviceType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TypeDistribution(
    val broadcastCount: Int = 0,
    val receiverCount: Int = 0,
    val modemCount: Int = 0
) {
    val total: Int get() = broadcastCount + receiverCount + modemCount
    val broadcastPct: Float get() = if (total > 0) broadcastCount.toFloat() / total else 0f
    val receiverPct: Float get() = if (total > 0) receiverCount.toFloat() / total else 0f
    val modemPct: Float get() = if (total > 0) modemCount.toFloat() / total else 0f
}

data class DashboardUiState(
    val totalDevices: Int = 0,
    val totalUsers: Int = 0,
    val onlineCount: Int = 0,
    val offlineCount: Int = 0,
    val maintenanceCount: Int = 0,
    val inHouseCount: Int = 0,
    val typeDistribution: TypeDistribution = TypeDistribution(),
    val recentDevices: List<Device> = emptyList(),
    val isRefreshing: Boolean = false
)

class DashboardViewModel(
    private val repository: BroadcastRepository
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val currentUsername: StateFlow<String> = repository.currentUsername
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "hatem")

    val currentUserRole: StateFlow<String> = repository.currentUserRole
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "ADMIN")

    val uiState: StateFlow<DashboardUiState> = combine(
        repository.allDevices,
        repository.totalUsersCount,
        _isRefreshing
    ) { devices, usersCount, refreshing ->
        val online = devices.count { it.status == DeviceStatus.ONLINE }
        val offline = devices.count { it.status == DeviceStatus.OFFLINE }
        val maintenance = devices.count { it.status == DeviceStatus.MAINTENANCE }
        val inHouse = devices.count { it.status == DeviceStatus.IN_HOUSE }

        val broadcasts = devices.count { it.type == DeviceType.BROADCAST }
        val receivers = devices.count { it.type == DeviceType.RECEIVER }
        val modems = devices.count { it.type == DeviceType.MODEM }

        DashboardUiState(
            totalDevices = devices.size,
            totalUsers = usersCount,
            onlineCount = online,
            offlineCount = offline,
            maintenanceCount = maintenance,
            inHouseCount = inHouse,
            typeDistribution = TypeDistribution(broadcasts, receivers, modems),
            recentDevices = devices.take(5),
            isRefreshing = refreshing
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DashboardUiState()
    )

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            delay(600) // Brief animation to simulate remote network sync
            _isRefreshing.value = false
        }
    }
}
