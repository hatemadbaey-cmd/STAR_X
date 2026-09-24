package com.example.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.presentation.auth.AuthViewModel
import com.example.presentation.auth.ChangePasswordScreen
import com.example.presentation.auth.LoginScreen
import com.example.presentation.auth.SplashScreen
import com.example.presentation.common.AppViewModelProvider
import com.example.presentation.dashboard.DashboardViewModel
import com.example.presentation.devices.AddEditDeviceScreen
import com.example.presentation.devices.DeviceDetailsScreen
import com.example.presentation.devices.DeviceFormViewModel
import com.example.presentation.devices.DevicesViewModel
import com.example.presentation.logs.AuditLogScreen
import com.example.presentation.logs.LogsViewModel
import com.example.presentation.main.MainScreen
import com.example.presentation.settings.AboutScreen
import com.example.presentation.settings.NotificationSettingsScreen
import com.example.presentation.settings.SettingsViewModel
import com.example.presentation.users.UsersManagementScreen
import com.example.presentation.users.UsersViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val authViewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val dashboardViewModel: DashboardViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val devicesViewModel: DevicesViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val deviceFormViewModel: DeviceFormViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val usersViewModel: UsersViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val logsViewModel: LogsViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)

    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                checkSession = { authViewModel.checkSessionValidity() },
                onNavigateNext = { loggedIn ->
                    if (loggedIn) {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainScreen(
                dashboardViewModel = dashboardViewModel,
                devicesViewModel = devicesViewModel,
                deviceFormViewModel = deviceFormViewModel,
                settingsViewModel = settingsViewModel,
                onNavigateToEditDevice = { deviceId ->
                    navController.navigate(Screen.EditDevice.createRoute(deviceId))
                },
                onNavigateToDeviceDetails = { deviceId ->
                    navController.navigate(Screen.DeviceDetails.createRoute(deviceId))
                },
                onNavigateToNotifications = {
                    navController.navigate(Screen.NotificationSettings.route)
                },
                onNavigateToUsers = {
                    navController.navigate(Screen.UsersManagement.route)
                },
                onNavigateToLogs = {
                    navController.navigate(Screen.AuditLogs.route)
                },
                onNavigateToChangePassword = {
                    navController.navigate(Screen.ChangePassword.route)
                },
                onNavigateToAbout = {
                    navController.navigate(Screen.About.route)
                },
                onLoggedOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.EditDevice.route,
            arguments = listOf(navArgument("deviceId") { type = NavType.LongType })
        ) { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getLong("deviceId") ?: 0L
            val devices by devicesViewModel.filteredDevices.collectAsState()
            val existingDevice = devices.find { it.id == deviceId }

            AddEditDeviceScreen(
                viewModel = deviceFormViewModel,
                existingDevice = existingDevice,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.DeviceDetails.route,
            arguments = listOf(navArgument("deviceId") { type = NavType.LongType })
        ) { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getLong("deviceId") ?: 0L
            DeviceDetailsScreen(
                deviceId = deviceId,
                viewModel = devicesViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.EditDevice.createRoute(id))
                }
            )
        }

        composable(Screen.UsersManagement.route) {
            UsersManagementScreen(
                viewModel = usersViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AuditLogs.route) {
            AuditLogScreen(
                viewModel = logsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.NotificationSettings.route) {
            NotificationSettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ChangePassword.route) {
            ChangePasswordScreen(
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
