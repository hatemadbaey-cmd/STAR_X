package com.example.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.preferences.AppPreferences
import com.example.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val notifyDeviceAdded by viewModel.notifyDeviceAdded.collectAsState()
    val notifyDeviceDeleted by viewModel.notifyDeviceDeleted.collectAsState()
    val notifyDeviceUpdated by viewModel.notifyDeviceUpdated.collectAsState()
    val notifyStatusChanged by viewModel.notifyStatusChanged.collectAsState()
    val notifyPingFailed by viewModel.notifyPingFailed.collectAsState()
    val notifyUserChanges by viewModel.notifyUserChanges.collectAsState()
    val notifySound by viewModel.notifySound.collectAsState()
    val notifyVibrate by viewModel.notifyVibrate.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.notification_settings),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("notif_settings_back_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.cancel))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Events Notifications Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "تنبيهات أحداث الشبكة",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    NotificationSwitchRow(
                        title = stringResource(id = R.string.notify_device_added),
                        subtitle = "إشعار عند إضافة أي برج أو جهاز بث جديد",
                        checked = notifyDeviceAdded,
                        onCheckedChange = { viewModel.toggleNotification(AppPreferences.KEY_NOTIFY_DEVICE_ADDED, it) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    NotificationSwitchRow(
                        title = stringResource(id = R.string.notify_status_changed),
                        subtitle = "إشعار عند تغيير حالة الجهاز (متصل، صيانة، غير متصل)",
                        checked = notifyStatusChanged,
                        onCheckedChange = { viewModel.toggleNotification(AppPreferences.KEY_NOTIFY_STATUS_CHANGED, it) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    NotificationSwitchRow(
                        title = stringResource(id = R.string.notify_ping_failed),
                        subtitle = "تنبيه فوري عند فشل فحص الاستجابة (Ping) مع جهاز",
                        checked = notifyPingFailed,
                        onCheckedChange = { viewModel.toggleNotification(AppPreferences.KEY_NOTIFY_PING_FAILED, it) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    NotificationSwitchRow(
                        title = stringResource(id = R.string.notify_device_updated),
                        subtitle = "إشعار عند تعديل بيانات الأجهزة",
                        checked = notifyDeviceUpdated,
                        onCheckedChange = { viewModel.toggleNotification(AppPreferences.KEY_NOTIFY_DEVICE_UPDATED, it) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    NotificationSwitchRow(
                        title = stringResource(id = R.string.notify_device_deleted),
                        subtitle = "إشعار عند حذف جهاز من النظام",
                        checked = notifyDeviceDeleted,
                        onCheckedChange = { viewModel.toggleNotification(AppPreferences.KEY_NOTIFY_DEVICE_DELETED, it) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    NotificationSwitchRow(
                        title = stringResource(id = R.string.notify_user_changes),
                        subtitle = "إشعار عند إنشاء أو تعديل حسابات المشرفين",
                        checked = notifyUserChanges,
                        onCheckedChange = { viewModel.toggleNotification(AppPreferences.KEY_NOTIFY_USER_CHANGES, it) }
                    )
                }
            }

            // Alert Sound & Vibration Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "خيارات التنبيه والتفاعل",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    NotificationSwitchRow(
                        title = stringResource(id = R.string.notify_sound),
                        subtitle = "تشغيل نغمة عند وصول إشعار مهم",
                        checked = notifySound,
                        onCheckedChange = { viewModel.toggleNotification(AppPreferences.KEY_NOTIFY_SOUND, it) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    NotificationSwitchRow(
                        title = stringResource(id = R.string.notify_vibrate),
                        subtitle = "اهتزاز الهاتف عند تنبيهات الأجهزة",
                        checked = notifyVibrate,
                        onCheckedChange = { viewModel.toggleNotification(AppPreferences.KEY_NOTIFY_VIBRATE, it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryBlue)
        )
    }
}
