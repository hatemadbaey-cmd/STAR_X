package com.example.presentation.settings

import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.preferences.AppPreferences
import com.example.data.repository.BroadcastRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isExporting: Boolean = false,
    val feedbackMessage: String? = null,
    val exportedFilePath: String? = null
)

class SettingsViewModel(
    private val repository: BroadcastRepository,
    private val preferences: AppPreferences
) : ViewModel() {

    val currentUsername: StateFlow<String> = repository.currentUsername
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "hatem")

    val currentUserRole: StateFlow<String> = repository.currentUserRole
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "ADMIN")

    val themeMode: StateFlow<String> = repository.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "SYSTEM")

    val notifyDeviceAdded: StateFlow<Boolean> = preferences.notifyDeviceAdded
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val notifyDeviceDeleted: StateFlow<Boolean> = preferences.notifyDeviceDeleted
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val notifyDeviceUpdated: StateFlow<Boolean> = preferences.notifyDeviceUpdated
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val notifyStatusChanged: StateFlow<Boolean> = preferences.notifyStatusChanged
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val notifyPingFailed: StateFlow<Boolean> = preferences.notifyPingFailed
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val notifyUserChanges: StateFlow<Boolean> = preferences.notifyUserChanges
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val notifySound: StateFlow<Boolean> = preferences.notifySound
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val notifyVibrate: StateFlow<Boolean> = preferences.notifyVibrate
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun toggleNotification(key: Preferences.Key<Boolean>, enabled: Boolean) {
        viewModelScope.launch {
            preferences.setNotificationToggle(key, enabled)
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            preferences.setThemeMode(mode)
        }
    }

    fun exportSqlBackup() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true, feedbackMessage = null)
            val result = repository.exportSqlBackup()
            if (result.isSuccess) {
                val file = result.getOrNull()
                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    feedbackMessage = "تم حفظ ملف SQL بنجاح في:\n${file?.name}",
                    exportedFilePath = file?.absolutePath
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    feedbackMessage = "فشل تصدير النسخة الاحتياطية"
                )
            }
        }
    }

    fun exportExcelCsv() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true, feedbackMessage = null)
            val result = repository.exportDevicesCsv()
            if (result.isSuccess) {
                val file = result.getOrNull()
                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    feedbackMessage = "تم تصدير ملف CSV للأجهزة بنجاح:\n${file?.name}",
                    exportedFilePath = file?.absolutePath
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    feedbackMessage = "فشل تصدير ملف التقرير"
                )
            }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            repository.logout()
            onLoggedOut()
        }
    }

    fun clearFeedback() {
        _uiState.value = _uiState.value.copy(feedbackMessage = null)
    }
}
