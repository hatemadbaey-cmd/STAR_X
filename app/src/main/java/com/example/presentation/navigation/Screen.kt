package com.example.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Main : Screen("main")
    object AddDevice : Screen("add_device")
    object EditDevice : Screen("edit_device/{deviceId}") {
        fun createRoute(deviceId: Long) = "edit_device/$deviceId"
    }
    object DeviceDetails : Screen("device_details/{deviceId}") {
        fun createRoute(deviceId: Long) = "device_details/$deviceId"
    }
    object UsersManagement : Screen("users_management")
    object ChangePassword : Screen("change_password")
    object AuditLogs : Screen("audit_logs")
    object NotificationSettings : Screen("notification_settings")
    object About : Screen("about")
}

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val testTag: String
) {
    object Dashboard : BottomNavItem("dashboard_tab", "الرئيسية", "nav_dashboard_tab")
    object Devices : BottomNavItem("devices_tab", "الأجهزة", "nav_devices_tab")
    object Add : BottomNavItem("add_tab", "إضافة", "nav_add_tab")
    object Account : BottomNavItem("account_tab", "الحساب", "nav_account_tab")
}
