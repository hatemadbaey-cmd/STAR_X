package com.example.presentation.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.BroadcastRepository
import com.example.domain.model.AuditLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LogsViewModel(
    private val repository: BroadcastRepository
) : ViewModel() {

    val logs: StateFlow<List<AuditLog>> = repository.allLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentUserRole: StateFlow<String> = repository.currentUserRole
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "ADMIN")

    private val _isClearing = MutableStateFlow(false)
    val isClearing: StateFlow<Boolean> = _isClearing.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun clearLogs() {
        viewModelScope.launch {
            _isClearing.value = true
            val result = repository.clearLogs()
            _isClearing.value = false
            if (result.isSuccess) {
                _message.value = "تم مسح جميع سجلات العمليات بنجاح"
            } else {
                _message.value = "حدث خطأ أثناء مسح السجل"
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
