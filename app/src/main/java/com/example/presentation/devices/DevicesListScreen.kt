package com.example.presentation.devices

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NetworkPing
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.model.Device
import com.example.domain.model.DeviceStatus
import com.example.domain.model.DeviceType
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryBlue
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesListScreen(
    viewModel: DevicesViewModel,
    onNavigateToAddDevice: () -> Unit,
    onNavigateToEditDevice: (Long) -> Unit,
    onNavigateToDeviceDetails: (Long) -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val devices by viewModel.filteredDevices.collectAsState()
    val userRole by viewModel.currentUserRole.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showSortMenu by remember { mutableStateOf(false) }
    var deviceToDelete by remember { mutableStateOf<Device?>(null) }

    LaunchedEffect(uiState.feedbackMessage) {
        uiState.feedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedback()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.nav_devices),
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = { showSortMenu = true },
                        modifier = Modifier.testTag("sort_devices_button")
                    ) {
                        Icon(Icons.Default.Sort, contentDescription = stringResource(id = R.string.sort_by))
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.sort_newest)) },
                            onClick = {
                                viewModel.onSortOrderChanged(SortOrder.NEWEST)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.sort_ip)) },
                            onClick = {
                                viewModel.onSortOrderChanged(SortOrder.IP)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.sort_status)) },
                            onClick = {
                                viewModel.onSortOrderChanged(SortOrder.STATUS)
                                showSortMenu = false
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddDevice,
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_device_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.nav_add))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Input
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = { Text(stringResource(id = R.string.search_devices_hint), fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryBlue) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("devices_search_input")
            )

            // Type Tabs: الكل / بث / لاقط / مودم
            val typeTabs = listOf(null) + DeviceType.entries
            val selectedTabIndex = typeTabs.indexOf(uiState.selectedType)

            TabRow(
                selectedTabIndex = if (selectedTabIndex >= 0) selectedTabIndex else 0,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = PrimaryBlue,
                divider = {}
            ) {
                typeTabs.forEachIndexed { index, type ->
                    Tab(
                        selected = (type == uiState.selectedType),
                        onClick = { viewModel.onTypeSelected(type) },
                        text = {
                            Text(
                                text = type?.titleAr ?: stringResource(id = R.string.type_all),
                                fontSize = 13.sp,
                                fontWeight = if (type == uiState.selectedType) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("type_tab_${type?.name ?: "ALL"}")
                    )
                }
            }

            // Status Filter Chips Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = (uiState.selectedStatus == null),
                        onClick = { viewModel.onStatusSelected(null) },
                        label = { Text(stringResource(id = R.string.status_all), fontSize = 12.sp) },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryBlue.copy(alpha = 0.15f),
                            selectedLabelColor = PrimaryBlue
                        )
                    )
                }
                items(DeviceStatus.entries) { status ->
                    FilterChip(
                        selected = (uiState.selectedStatus == status),
                        onClick = {
                            viewModel.onStatusSelected(if (uiState.selectedStatus == status) null else status)
                        },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(status.color, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(status.titleAr, fontSize = 12.sp)
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = status.color.copy(alpha = 0.15f),
                            selectedLabelColor = status.color
                        ),
                        modifier = Modifier.testTag("status_chip_${status.name}")
                    )
                }
            }

            // Device list
            if (devices.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.empty_devices),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(devices, key = { it.id }) { device ->
                        DeviceCardItem(
                            device = device,
                            isPinging = uiState.isPinging && uiState.pingingDeviceId == device.id,
                            pingResult = if (uiState.pingingDeviceId == device.id) uiState.pingResult else null,
                            onCardClick = { onNavigateToDeviceDetails(device.id) },
                            onQuickStatusClick = { viewModel.openStatusChangeSheet(device) },
                            onPingClick = { viewModel.pingDevice(device.id, device.ip) },
                            onEditClick = { onNavigateToEditDevice(device.id) },
                            onDeleteClick = { deviceToDelete = device },
                            onCopyIp = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Device IP", device.ip)
                                clipboard.setPrimaryClip(clip)
                                scope.launch {
                                    snackbarHostState.showSnackbar("تم نسخ الـ IP: ${device.ip}")
                                }
                            },
                            onOpenInBrowser = {
                                val url = "http://${device.ip}"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                try {
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    Toast.makeText(context, "لا يوجد متصفح مثبت", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onShare = {
                                val text = "بيانات الجهاز:\nالمودم: ${device.modemName}\nالـ IP: ${device.ip}\nالنوع: ${device.type.titleAr}\nالحالة: ${device.status.titleAr}\nالموقع: ${device.location}"
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, text)
                                }
                                context.startActivity(Intent.createChooser(intent, "مشاركة بيانات الجهاز"))
                            }
                        )
                    }
                }
            }
        }
    }

    // Status Change Bottom Sheet
    uiState.activeDeviceForStatusChange?.let { device ->
        QuickStatusBottomSheet(
            device = device,
            onStatusSelected = { newStatus ->
                viewModel.updateDeviceStatus(device.id, newStatus)
            },
            onDismiss = { viewModel.closeStatusChangeSheet() }
        )
    }

    // Delete Device Confirmation Dialog
    deviceToDelete?.let { device ->
        AlertDialog(
            onDismissRequest = { deviceToDelete = null },
            title = { Text(stringResource(id = R.string.delete_device)) },
            text = { Text(stringResource(id = R.string.delete_device_confirm, device.modemName)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDevice(device.id)
                        deviceToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    modifier = Modifier.testTag("confirm_delete_device_button")
                ) {
                    Text(stringResource(id = R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { deviceToDelete = null }) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DeviceCardItem(
    device: Device,
    isPinging: Boolean,
    pingResult: com.example.domain.model.PingResult?,
    onCardClick: () -> Unit,
    onQuickStatusClick: () -> Unit,
    onPingClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCopyIp: () -> Unit,
    onOpenInBrowser: () -> Unit,
    onShare: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .combinedClickable(
                onClick = onCardClick,
                onLongClick = onCopyIp
            )
            .testTag("device_card_${device.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Row 1: Status Dot, IP, Type Chip, More Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Colored status indicator dot
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(device.status.color, CircleShape)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // IP
                Text(
                    text = device.ip,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                // Type Chip
                Surface(
                    color = device.type.bgColor,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = device.type.titleAr,
                        fontSize = 11.sp,
                        color = device.type.textColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Options Menu
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("device_more_menu_${device.id}")
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = PrimaryBlue) },
                            text = { Text("تعديل") },
                            onClick = {
                                showMenu = false
                                onEditClick()
                            }
                        )
                        DropdownMenuItem(
                            leadingIcon = { Icon(Icons.Default.Language, contentDescription = null, tint = PrimaryBlue) },
                            text = { Text("فتح في المتصفح") },
                            onClick = {
                                showMenu = false
                                onOpenInBrowser()
                            }
                        )
                        DropdownMenuItem(
                            leadingIcon = { Icon(Icons.Default.Share, contentDescription = null, tint = PrimaryBlue) },
                            text = { Text("مشاركة") },
                            onClick = {
                                showMenu = false
                                onShare()
                            }
                        )
                        DropdownMenuItem(
                            leadingIcon = { Icon(Icons.Default.Clear, contentDescription = null, tint = ErrorRed) },
                            text = { Text("حذف الجهاز", color = ErrorRed) },
                            onClick = {
                                showMenu = false
                                onDeleteClick()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Modem Name & Location
            Text(
                text = device.modemName,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (device.location.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = device.location,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row 3: Status Badge (clickable to change) + Ping Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Badge Chip
                Surface(
                    color = device.status.color.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .clickable(onClick = onQuickStatusClick)
                        .testTag("device_status_badge_${device.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(device.status.color, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = device.status.titleAr,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = device.status.color
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.Sync,
                            contentDescription = "تغيير الحالة",
                            tint = device.status.color,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                // Ping action button
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .clickable(enabled = !isPinging, onClick = onPingClick)
                        .testTag("device_ping_btn_${device.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isPinging) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = PrimaryBlue
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("جارِ الفحص...", fontSize = 11.sp, color = PrimaryBlue)
                        } else if (pingResult != null) {
                            Icon(
                                Icons.Default.Speed,
                                contentDescription = null,
                                tint = if (pingResult.isSuccess) PrimaryBlue else ErrorRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${pingResult.latencyMs} ms",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (pingResult.isSuccess) PrimaryBlue else ErrorRed
                            )
                        } else if (device.lastPingMs != null) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${device.lastPingMs} ms",
                                fontSize = 11.sp,
                                color = PrimaryBlue
                            )
                        } else {
                            Icon(Icons.Default.NetworkPing, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "فحص Ping",
                                fontSize = 11.sp,
                                color = PrimaryBlue
                            )
                        }
                    }
                }
            }
        }
    }
}
