package com.example.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.preferences.AppPreferences
import com.example.data.repository.BroadcastRepository
import com.example.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val user: User? = null,
    val isPasswordChanged: Boolean = false
)

class AuthViewModel(
    private val repository: BroadcastRepository,
    private val preferences: AppPreferences
) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean> = repository.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val currentUsername: StateFlow<String> = repository.currentUsername
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "hatem")

    val currentUserRole: StateFlow<String> = repository.currentUserRole
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "ADMIN")

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    suspend fun checkSessionValidity(): Boolean {
        return repository.checkSessionValidity()
    }

    fun login(username: String, password: String, onLoginSuccess: () -> Unit) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "يرجى ملء جميع الحقول المطلوبة")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.login(username.trim(), password)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(isLoading = false, user = result.getOrNull())
                onLoginSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "خطأ أثناء تسجيل الدخول"
                )
            }
        }
    }

    fun changePassword(oldPass: String, newPass: String, confirmPass: String, onSuccess: () -> Unit) {
        if (oldPass.isBlank() || newPass.isBlank() || confirmPass.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "يرجى تعبئة جميع الحقول")
            return
        }
        if (newPass != confirmPass) {
            _uiState.value = _uiState.value.copy(error = "كلمات المرور الجديدة غير متطابقة")
            return
        }
        if (newPass.length < 4) {
            _uiState.value = _uiState.value.copy(error = "يجب أن تتكون كلمة المرور من 4 خانات على الأقل")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val res = repository.changePassword(oldPass, newPass)
            if (res.isSuccess) {
                _uiState.value = _uiState.value.copy(isLoading = false, isPasswordChanged = true)
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = res.exceptionOrNull()?.message ?: "فشل تغيير كلمة المرور"
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

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
