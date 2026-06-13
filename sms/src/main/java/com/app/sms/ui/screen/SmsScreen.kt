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
import androidx.compose.runtime.*
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
        val permission = Manifest.permission.READ_SMS
        when {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.changePermissionStatus(PermissionStatus.GRANTED)
            }
            activity != null && ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) -> {
                viewModel.changePermissionStatus(PermissionStatus.SHOW_RATIONALE)
            }
            viewModel.hasAskedBeforeSmsPermission && activity != null && !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) -> {
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
            viewModel.hasAskedBeforeSmsPermission = true
            checkPermissionStatus()
        }
    )

    LaunchedEffect(uiState.permissionStatus) {
        if (uiState.permissionStatus == PermissionStatus.GRANTED) {
            viewModel.loadSmsMessages()
        } else if (uiState.permissionStatus == PermissionStatus.SHOW_FIRST_TIME) {
            launcher.launch(Manifest.permission.READ_SMS)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState.permissionStatus) {
            PermissionStatus.GRANTED -> {
                when (val state = uiState.smsUiState) {
                    is SMSUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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
            PermissionStatus.SHOW_RATIONALE, PermissionStatus.SHOW_FIRST_TIME -> {
                PermissionView(
                    message = "SMS permission is required to show your messages.",
                    buttonText = "Grant Permission",
                    onButtonClick = { launcher.launch(Manifest.permission.READ_SMS) }
                )
            }
            PermissionStatus.SHOW_SETTINGS -> {
                PermissionView(
                    message = "SMS permission is required. Please enable it in settings.",
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
        modifier = Modifier.fillMaxSize().padding(16.dp),
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
