package com.app.sms.ui.screen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.app.sms.domain.model.SmsDomain
import com.app.sms.ui.component.SmsDetailDialog
import com.app.sms.ui.component.SmsItem
import com.app.sms.ui.util.PermissionStatus
import com.app.ui.theme.ConvoTheme

@Composable
fun SmsScreen(
    viewModel: SmsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    
    var selectedSms by remember { mutableStateOf<SmsDomain?>(null) }

    val checkPermissionStatus = {
        val permissions = arrayOf(Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS)
        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        when {
            allGranted -> {
                viewModel.changePermissionStatus(PermissionStatus.GRANTED)
            }
            activity != null && permissions.any { ActivityCompat.shouldShowRequestPermissionRationale(activity, it) } -> {
                viewModel.changePermissionStatus(PermissionStatus.SHOW_RATIONALE)
            }
            viewModel.hasAskedBeforeSmsPermission && activity != null -> {
                // If we've asked before and some are not granted and no rationale, they might have checked "Don't ask again"
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
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { _ ->
            viewModel.hasAskedBeforeSmsPermission = true
            checkPermissionStatus()
        }
    )

    LaunchedEffect(uiState.permissionStatus) {
        if (uiState.permissionStatus == PermissionStatus.GRANTED) {
            viewModel.loadSmsMessages()
        } else if (uiState.permissionStatus == PermissionStatus.SHOW_FIRST_TIME) {
            launcher.launch(arrayOf(Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState.permissionStatus) {
            PermissionStatus.GRANTED -> {
                PullToRefreshBox(
                    isRefreshing = uiState.smsUiState is SMSUiState.Loading,
                    onRefresh = {
                        viewModel.loadSmsMessages()
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (val state = uiState.smsUiState) {
                        is SMSUiState.Loading -> {
                        }
                        is SMSUiState.Success -> {
                            LazyColumn {
                                items(state.smsList) { sms ->
                                    SmsItem(sms = sms, onClick = { selectedSms = sms })
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        thickness = 0.5.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant
                                    )
                                }
                            }
                        }
                        is SMSUiState.Error -> {
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
                PermissionView(
                    message = "SMS and Contacts permissions are required to show your messages with names.",
                    buttonText = "Grant Permissions",
                    onButtonClick = { launcher.launch(arrayOf(Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS)) }
                )
            }
            PermissionStatus.SHOW_SETTINGS -> {
                PermissionView(
                    message = "SMS and Contacts permissions are required. Please enable them in settings.",
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

    selectedSms?.let { sms ->
        SmsDetailDialog(sms = sms, onDismiss = { selectedSms = null })
    }
}

@Composable
fun PermissionView(
    message: String,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, modifier = Modifier.padding(bottom = 16.dp))
        Button(onClick = onButtonClick) {
            Text(text = buttonText)
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

@Preview
@Composable
private fun SMSScreenPrev() {
    ConvoTheme {
        SmsScreen()
    }
}
