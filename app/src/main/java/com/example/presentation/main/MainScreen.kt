package com.example.presentation.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Router
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.presentation.dashboard.DashboardScreen
import com.example.presentation.dashboard.DashboardViewModel
import com.example.presentation.devices.AddEditDeviceScreen
import com.example.presentation.devices.DeviceFormViewModel
import com.example.presentation.devices.DevicesListScreen
import com.example.presentation.devices.DevicesViewModel
import com.example.presentation.navigation.BottomNavItem
import com.example.presentation.settings.SettingsScreen
import com.example.presentation.settings.SettingsViewModel
import com.example.ui.theme.PrimaryBlue

@Composable
fun MainScreen(
    dashboardViewModel: DashboardViewModel,
    devicesViewModel: DevicesViewModel,
    deviceFormViewModel: DeviceFormViewModel,
    settingsViewModel: SettingsViewModel,
    onNavigateToEditDevice: (Long) -> Unit,
    onNavigateToDeviceDetails: (Long) -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToLogs: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onLoggedOut: () -> Unit
) {
    var currentTab by rememberSaveable { mutableStateOf(BottomNavItem.Dashboard.route) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val items = listOf(
                    NavigationItemConfig(
                        item = BottomNavItem.Dashboard,
                        title = stringResource(id = R.string.nav_dashboard),
                        icon = Icons.Default.Dashboard
                    ),
                    NavigationItemConfig(
                        item = BottomNavItem.Devices,
                        title = stringResource(id = R.string.nav_devices),
                        icon = Icons.Default.Router
                    ),
                    NavigationItemConfig(
                        item = BottomNavItem.Add,
                        title = stringResource(id = R.string.nav_add),
                        icon = Icons.Default.AddCircle
                    ),
                    NavigationItemConfig(
                        item = BottomNavItem.Account,
                        title = stringResource(id = R.string.nav_account),
                        icon = Icons.Default.Person
                    )
                )

                items.forEach { config ->
                    val isSelected = currentTab == config.item.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = config.item.route },
                        icon = { Icon(config.icon, contentDescription = config.title) },
                        label = { Text(config.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryBlue,
                            selectedTextColor = PrimaryBlue,
                            indicatorColor = PrimaryBlue.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag(config.item.testTag)
                    )
                }
            }
        }
    ) { paddingValues ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentTab) {
                BottomNavItem.Dashboard.route -> {
                    DashboardScreen(
                        viewModel = dashboardViewModel,
                        onNavigateToDevices = { currentTab = BottomNavItem.Devices.route },
                        onNavigateToDeviceDetails = onNavigateToDeviceDetails,
                        onNavigateToNotifications = onNavigateToNotifications
                    )
                }
                BottomNavItem.Devices.route -> {
                    DevicesListScreen(
                        viewModel = devicesViewModel,
                        onNavigateToAddDevice = { currentTab = BottomNavItem.Add.route },
                        onNavigateToEditDevice = onNavigateToEditDevice,
                        onNavigateToDeviceDetails = onNavigateToDeviceDetails
                    )
                }
                BottomNavItem.Add.route -> {
                    AddEditDeviceScreen(
                        viewModel = deviceFormViewModel,
                        existingDevice = null,
                        onNavigateBack = { currentTab = BottomNavItem.Devices.route }
                    )
                }
                BottomNavItem.Account.route -> {
                    SettingsScreen(
                        viewModel = settingsViewModel,
                        onNavigateToUsers = onNavigateToUsers,
                        onNavigateToLogs = onNavigateToLogs,
                        onNavigateToNotifications = onNavigateToNotifications,
                        onNavigateToChangePassword = onNavigateToChangePassword,
                        onNavigateToAbout = onNavigateToAbout,
                        onLoggedOut = onLoggedOut
                    )
                }
            }
        }
    }
}

private data class NavigationItemConfig(
    val item: BottomNavItem,
    val title: String,
    val icon: ImageVector
)
