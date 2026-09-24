package com.example.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.BroadcastApp
import com.example.presentation.auth.AuthViewModel
import com.example.presentation.dashboard.DashboardViewModel
import com.example.presentation.devices.DeviceFormViewModel
import com.example.presentation.devices.DevicesViewModel
import com.example.presentation.logs.LogsViewModel
import com.example.presentation.settings.SettingsViewModel
import com.example.presentation.users.UsersViewModel

object AppViewModelProvider {
    val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val app = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BroadcastApp
            val repository = app.repository
            val prefs = app.appPreferences

            return when {
                modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                    AuthViewModel(repository, prefs) as T
                }
                modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {
                    DashboardViewModel(repository) as T
                }
                modelClass.isAssignableFrom(DevicesViewModel::class.java) -> {
                    DevicesViewModel(repository) as T
                }
                modelClass.isAssignableFrom(DeviceFormViewModel::class.java) -> {
                    DeviceFormViewModel(repository) as T
                }
                modelClass.isAssignableFrom(UsersViewModel::class.java) -> {
                    UsersViewModel(repository) as T
                }
                modelClass.isAssignableFrom(LogsViewModel::class.java) -> {
                    LogsViewModel(repository) as T
                }
                modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                    SettingsViewModel(repository, prefs) as T
                }
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
