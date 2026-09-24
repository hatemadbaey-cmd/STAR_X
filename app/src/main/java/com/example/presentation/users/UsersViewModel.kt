package com.example.presentation.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.BroadcastRepository
import com.example.domain.model.User
import com.example.domain.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UsersUiState(
    val isAddUserDialogOpen: Boolean = false,
    val newUsername: String = "",
    val newPassword: String = "",
    val newRole: UserRole = UserRole.USER,
    val userToDelete: User? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class UsersViewModel(
    private val repository: BroadcastRepository
) : ViewModel() {

    val users: StateFlow<List<User>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentUsername: StateFlow<String> = repository.currentUsername
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "hatem")

    val currentUserRole: StateFlow<String> = repository.currentUserRole
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "ADMIN")

    private val _uiState = MutableStateFlow(UsersUiState())
    val uiState: StateFlow<UsersUiState> = _uiState.asStateFlow()

    fun openAddUserDialog() {
        _uiState.value = _uiState.value.copy(
            isAddUserDialogOpen = true,
            newUsername = "",
            newPassword = "",
            newRole = UserRole.USER,
            errorMessage = null
        )
    }

    fun closeAddUserDialog() {
        _uiState.value = _uiState.value.copy(isAddUserDialogOpen = false)
    }

    fun onUsernameChange(u: String) {
        _uiState.value = _uiState.value.copy(newUsername = u)
    }

    fun onPasswordChange(p: String) {
        _uiState.value = _uiState.value.copy(newPassword = p)
    }

    fun onRoleChange(r: UserRole) {
        _uiState.value = _uiState.value.copy(newRole = r)
    }

    fun addUser() {
        val state = _uiState.value
        if (state.newUsername.isBlank() || state.newPassword.isBlank()) {
            _uiState.value = state.copy(errorMessage = "يرجى إدخال اسم المستخدم وكلمة المرور")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)
            val result = repository.addUser(
                username = state.newUsername.trim(),
                password = state.newPassword,
                role = state.newRole
            )
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAddUserDialogOpen = false,
                    successMessage = "تمت إضافة المستخدم بنجاح"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "فشل إضافة المستخدم"
                )
            }
        }
    }

    fun requestDeleteUser(user: User) {
        _uiState.value = _uiState.value.copy(userToDelete = user)
    }

    fun dismissDeleteDialog() {
        _uiState.value = _uiState.value.copy(userToDelete = null)
    }

    fun confirmDeleteUser() {
        val target = _uiState.value.userToDelete ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, userToDelete = null)
            val result = repository.deleteUser(target.id, target.username)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "تم حذف المستخدم بنجاح"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "فشل حذف المستخدم"
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}
