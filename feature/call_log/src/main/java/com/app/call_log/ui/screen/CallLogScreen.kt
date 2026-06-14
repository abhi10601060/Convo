package com.app.call_log.ui.screen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.CallLog
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.app.call_log.domain.model.CallLogDomain
import com.app.call_log.ui.component.CallLogItem
import com.app.ui.util.PermissionStatus
import com.app.ui.components.ConvoTabs
import com.app.ui.components.PermissionDeniedContent
import com.app.ui.theme.ConvoTheme

@Composable
fun CallLogScreen(
    viewModel: CallLogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    val checkPermissionStatus = {
        val permission = Manifest.permission.READ_CALL_LOG
        when {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.changePermissionStatus(PermissionStatus.GRANTED)
            }
            activity != null && ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) -> {
                viewModel.changePermissionStatus(PermissionStatus.SHOW_RATIONALE)
            }
            viewModel.hasAskedBeforeCallLogPermission && activity != null && !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) -> {
                viewModel.changePermissionStatus(PermissionStatus.SHOW_SETTINGS)
            }
            else -> {
                viewModel.changePermissionStatus(PermissionStatus.SHOW_FIRST_TIME)
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                checkPermissionStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        checkPermissionStatus()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { _ ->
            viewModel.hasAskedBeforeCallLogPermission = true
            checkPermissionStatus()
        }
    )

    LaunchedEffect(uiState.permissionStatus) {
        if (uiState.permissionStatus == PermissionStatus.GRANTED) {
            viewModel.loadCallLogs()
        } else if (uiState.permissionStatus == PermissionStatus.SHOW_FIRST_TIME) {
            launcher.launch(Manifest.permission.READ_CALL_LOG)
        }
    }

    Scaffold(
        topBar = {
            ConvoTabs(
                titles = listOf("Incoming", "Outgoing", "Missed"),
                selectedIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (uiState.permissionStatus) {
                PermissionStatus.GRANTED -> {
                    PullToRefreshBox(
                        isRefreshing = uiState is CallLogUiState.Loading,
                        onRefresh = {
                            viewModel.loadCallLogs()
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        when (val state = uiState.callLogUiState) {
                            is CallLogUiState.Loading -> {
                                // CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                                // No Need of handling as handled in Pull to refersh box
                            }
                            is CallLogUiState.Success -> {
                                val filteredLogs = when (selectedTabIndex) {
                                    0 -> state.callLogs.filter { it.type == CallLog.Calls.INCOMING_TYPE }
                                    1 -> state.callLogs.filter { it.type == CallLog.Calls.OUTGOING_TYPE }
                                    2 -> state.callLogs.filter { it.type == CallLog.Calls.MISSED_TYPE }
                                    else -> state.callLogs
                                }

                                if (filteredLogs.isEmpty()) {
                                    Text(
                                        text = "No calls found",
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                } else {
                                    LazyColumn {
                                        items(filteredLogs) { log ->
                                            CallLogItem(
                                                callLog = log,
                                                onCallClick = {
                                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                                        data = Uri.parse("tel:${log.number}")
                                                    }
                                                    context.startActivity(intent)
                                                }
                                            )
                                            HorizontalDivider(
                                                modifier = Modifier.padding(horizontal = 16.dp),
                                                thickness = 0.5.dp,
                                                color = MaterialTheme.colorScheme.outlineVariant
                                            )
                                        }
                                    }
                                }
                            }
                            is CallLogUiState.Error -> {
                                Text(
                                    text = state.message,
                                    modifier = Modifier.align(Alignment.Center),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            else -> {}
                        }
                    }
                }
                PermissionStatus.SHOW_RATIONALE, PermissionStatus.SHOW_FIRST_TIME -> {
                    PermissionDeniedContent(
                        icon = Icons.Default.Call,
                        message = "Call Log permission is required to show your calls.",
                        buttonText = "Grant Permission",
                        onButtonClick = { launcher.launch(Manifest.permission.READ_CALL_LOG) }
                    )
                }
                PermissionStatus.SHOW_SETTINGS -> {
                    PermissionDeniedContent(
                        icon = Icons.Default.Call,
                        message = "Call Log permission is required. Please enable it in settings.",
                        buttonText = "Open Settings",
                        onButtonClick = {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                            context.startActivity(intent)
                        }
                    )
                }
                else -> {}
            }
        }
    }
}

private fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
