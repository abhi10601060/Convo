package com.app.contacts.ui.screen.contacts

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.contacts.ui.components.LocalContacts
import com.app.contacts.ui.components.RemoteContacts
import com.app.contacts.ui.util.PermissionStatus
import com.app.ui.components.ConvoTabs
import com.app.ui.theme.ConvoTheme

@Composable
fun ContactsScreen(
    viewModel: ContactsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }

    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val localListState = rememberLazyListState()
    val remoteListState = rememberLazyListState()

    val checkPermissionStatus = {
        val permission = Manifest.permission.READ_CONTACTS
         when{
             ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.changeLocalContactsPermissionStatus(PermissionStatus.GRANTED)
            }

             activity != null && ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) -> {
                 viewModel.changeLocalContactsPermissionStatus(PermissionStatus.SHOW_RATIONALE)
             }

             viewModel.hasAskedBeforeContactPermission && activity != null && !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) -> {
                  viewModel.changeLocalContactsPermissionStatus(PermissionStatus.SHOW_SETTINGS)
             }

             else -> {
                 viewModel.changeLocalContactsPermissionStatus(PermissionStatus.SHOW_FIRST_TIME)
             }
        }
    }

    LaunchedEffect(Unit) {
        checkPermissionStatus()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { _ ->
            viewModel.hasAskedBeforeContactPermission = true
            checkPermissionStatus()
        }
    )

    // Load local contacts when permission is granted
    LaunchedEffect(uiState.permissionStatus) {
        if (uiState.permissionStatus == PermissionStatus.GRANTED) {
            viewModel.loadLocalContacts()
        } else if (uiState.permissionStatus == PermissionStatus.SHOW_FIRST_TIME && selectedTabIndex == 0) {
            launcher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    // Trigger permission request if tab 0 is selected and it's first time
    LaunchedEffect(selectedTabIndex) {
        if (selectedTabIndex == 0 && uiState.permissionStatus == PermissionStatus.SHOW_FIRST_TIME) {
            launcher.launch(Manifest.permission.READ_CONTACTS)
        }
        else{
            if (uiState.remoteContactsUiState is ContactsUiState.Idle) viewModel.loadRemoteContacts()
        }
    }

    ConvoTheme {
        Scaffold(
            topBar = {
                ConvoTabs(
                    titles = listOf("Local", "Remote"),
                    selectedIndex = selectedTabIndex,
                    onTabSelected = { selectedTabIndex = it }
                )
            }
        ) { padding ->
            if (selectedTabIndex == 0){
                LocalContacts(
                    modifier = Modifier.padding(padding),
                    uiState = uiState.localContactsUiState,
                    permissionStatus = uiState.permissionStatus,
                    onPermissionGrant = { launcher.launch(Manifest.permission.READ_CONTACTS) },
                    onSettingsClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    },
                    listState = localListState
                )
            } else {
                RemoteContacts(
                    modifier = Modifier.padding(padding),
                    uiState = uiState.remoteContactsUiState,
                    listState = remoteListState
                )
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

