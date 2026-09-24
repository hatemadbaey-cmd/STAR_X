package com.example.presentation.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.BroadcastRepository
import com.example.domain.model.Device
import com.example.domain.model.DeviceStatus
import com.example.domain.model.DeviceType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class DeviceFormUiState(
    val id: Long = 0L,
    val isEditMode: Boolean = false,
    val modemName: String = "",
    val ipAddress: String = "192.168.11.",
    val selectedType: DeviceType = DeviceType.BROADCAST,
    val selectedStatus: DeviceStatus = DeviceStatus.ONLINE,
    val location: String = "",
    val notes: String = "",
    val imageUri: String? = null,
    val isIpDuplicate: Boolean = false,
    val isIpValid: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isSavedSuccess: Boolean = false
)

class DeviceFormViewModel(
    private val repository: BroadcastRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeviceFormUiState())
    val uiState: StateFlow<DeviceFormUiState> = _uiState.asStateFlow()

    private val ipv4Regex = Regex("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}\$")

    fun initForAdd() {
        _uiState.value = DeviceFormUiState(
            selectedType = DeviceType.BROADCAST,
            ipAddress = DeviceType.BROADCAST.defaultPrefix
        )
    }

    fun initForEdit(device: Device) {
        _uiState.value = DeviceFormUiState(
            id = device.id,
            isEditMode = true,
            modemName = device.modemName,
            ipAddress = device.ip,
            selectedType = device.type,
            selectedStatus = device.status,
            location = device.location,
            notes = device.notes,
            imageUri = device.imageUri,
            isIpValid = true
        )
    }

    fun onTypeChanged(type: DeviceType) {
        val currentIp = _uiState.value.ipAddress
        // If current IP starts with one of default prefixes or is edit mode, auto-switch prefix smartly
        val newPrefix = type.defaultPrefix
        val updatedIp = if (currentIp.startsWith("192.168.")) {
            val lastPart = currentIp.substringAfterLast(".", "")
            newPrefix + lastPart
        } else {
            newPrefix
        }

        _uiState.value = _uiState.value.copy(
            selectedType = type,
            ipAddress = updatedIp
        )
        validateIp(updatedIp)
    }

    fun onIpChanged(ip: String) {
        _uiState.value = _uiState.value.copy(ipAddress = ip)
        validateIp(ip)
    }

    fun onModemNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(modemName = name)
    }

    fun onLocationChanged(loc: String) {
        _uiState.value = _uiState.value.copy(location = loc)
    }

    fun onNotesChanged(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    fun onStatusChanged(status: DeviceStatus) {
        _uiState.value = _uiState.value.copy(selectedStatus = status)
    }

    fun onImagePicked(uri: String?) {
        _uiState.value = _uiState.value.copy(imageUri = uri)
    }

    private fun validateIp(ip: String) {
        val trimmed = ip.trim()
        val isValid = trimmed.matches(ipv4Regex)
        viewModelScope.launch {
            val all = repository.allDevices.first()
            val isDup = all.any { it.ip.equals(trimmed, ignoreCase = true) && it.id != _uiState.value.id }
            _uiState.value = _uiState.value.copy(
                isIpValid = isValid,
                isIpDuplicate = isDup
            )
        }
    }

    fun saveDevice(onSaved: () -> Unit) {
        val state = _uiState.value

        if (state.modemName.isBlank()) {
            _uiState.value = state.copy(errorMessage = "اسم المودم إلزامي")
            return
        }

        if (state.ipAddress.isBlank() || !state.isIpValid) {
            _uiState.value = state.copy(errorMessage = "يرجى إدخال عنوان IP صالح (مثال: 192.168.11.20)")
            return
        }

        if (state.isIpDuplicate) {
            _uiState.value = state.copy(errorMessage = "عنوان الـ IP مستخدم بالفعل لجهاز آخر!")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, errorMessage = null)
            val device = Device(
                id = state.id,
                modemName = state.modemName.trim(),
                ip = state.ipAddress.trim(),
                type = state.selectedType,
                status = state.selectedStatus,
                location = state.location.trim(),
                notes = state.notes.trim(),
                imageUri = state.imageUri
            )

            val result = if (state.isEditMode) {
                repository.updateDevice(device)
            } else {
                repository.addDevice(device)
            }

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(isSaving = false, isSavedSuccess = true)
                onSaved()
            } else {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "فشل حفظ الجهاز"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
