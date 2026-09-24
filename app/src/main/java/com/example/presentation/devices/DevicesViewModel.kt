package com.example.presentation.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.BroadcastRepository
import com.example.domain.model.Device
import com.example.domain.model.DeviceStatus
import com.example.domain.model.DeviceType
import com.example.domain.model.PingResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class SortOrder {
    NEWEST,
    IP,
    STATUS
}

data class DevicesUiState(
    val searchQuery: String = "",
    val selectedType: DeviceType? = null,
    val selectedStatus: DeviceStatus? = null,
    val sortOrder: SortOrder = SortOrder.NEWEST,
    val activeDeviceForStatusChange: Device? = null,
    val isPinging: Boolean = false,
    val pingResult: PingResult? = null,
    val pingingDeviceId: Long? = null,
    val feedbackMessage: String? = null
)

class DevicesViewModel(
    private val repository: BroadcastRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DevicesUiState())
    val uiState: StateFlow<DevicesUiState> = _uiState.asStateFlow()

    val currentUserRole: StateFlow<String> = repository.currentUserRole
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "ADMIN")

    private val _searchQuery = MutableStateFlow("")
    private val _selectedType = MutableStateFlow<DeviceType?>(null)
    private val _selectedStatus = MutableStateFlow<DeviceStatus?>(null)
    private val _sortOrder = MutableStateFlow(SortOrder.NEWEST)

    val filteredDevices: StateFlow<List<Device>> = combine(
        repository.allDevices,
        _searchQuery,
        _selectedType,
        _selectedStatus,
        _sortOrder
    ) { devices, query, type, status, sort ->
        devices.filter { device ->
            val matchesQuery = query.isBlank() ||
                    device.ip.contains(query.trim(), ignoreCase = true) ||
                    device.modemName.contains(query.trim(), ignoreCase = true) ||
                    device.location.contains(query.trim(), ignoreCase = true)

            val matchesType = type == null || device.type == type
            val matchesStatus = status == null || device.status == status

            matchesQuery && matchesType && matchesStatus
        }.let { list ->
            when (sort) {
                SortOrder.NEWEST -> list.sortedByDescending { it.timestamp }
                SortOrder.IP -> list.sortedBy { it.ip }
                SortOrder.STATUS -> list.sortedBy { it.status.ordinal }
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
        _uiState.update { it.copy(searchQuery = newQuery) }
    }

    fun onTypeSelected(type: DeviceType?) {
        _selectedType.value = type
        _uiState.update { it.copy(selectedType = type) }
    }

    fun onStatusSelected(status: DeviceStatus?) {
        _selectedStatus.value = status
        _uiState.update { it.copy(selectedStatus = status) }
    }

    fun onSortOrderChanged(order: SortOrder) {
        _sortOrder.value = order
        _uiState.update { it.copy(sortOrder = order) }
    }

    fun openStatusChangeSheet(device: Device) {
        _uiState.update { it.copy(activeDeviceForStatusChange = device) }
    }

    fun closeStatusChangeSheet() {
        _uiState.update { it.copy(activeDeviceForStatusChange = null) }
    }

    fun updateDeviceStatus(deviceId: Long, newStatus: DeviceStatus) {
        viewModelScope.launch {
            repository.updateDeviceStatus(deviceId, newStatus)
            _uiState.update {
                it.copy(
                    activeDeviceForStatusChange = null,
                    feedbackMessage = "تم تحديث حالة الجهاز بنجاح"
                )
            }
        }
    }

    fun deleteDevice(deviceId: Long, onDeleted: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteDevice(deviceId)
            _uiState.update { it.copy(feedbackMessage = "تم حذف الجهاز بنجاح") }
            onDeleted()
        }
    }

    fun pingDevice(deviceId: Long, ip: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isPinging = true,
                    pingingDeviceId = deviceId,
                    pingResult = null
                )
            }

            val result = repository.pingDevice(deviceId, ip)
            _uiState.update {
                it.copy(
                    isPinging = false,
                    pingResult = result
                )
            }
        }
    }

    fun clearFeedback() {
        _uiState.update {
            it.copy(
                feedbackMessage = null,
                pingResult = null
            )
        }
    }
}
