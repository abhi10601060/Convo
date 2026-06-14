package com.app.contacts.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.contacts.domain.model.ContactDomain
import com.app.contacts.ui.screen.contacts.LocalContactsUiState
import com.app.contacts.ui.util.PermissionStatus

@Composable
fun LocalContacts(
    modifier: Modifier = Modifier,
    uiState: LocalContactsUiState,
    permissionStatus: PermissionStatus,
    onPermissionGrant: () -> Unit,
    onSettingsClick: () -> Unit,
    onContactClick: (ContactDomain) -> Unit,
    listState: LazyListState = rememberLazyListState()
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (permissionStatus != PermissionStatus.GRANTED ) {
            val showSettingsButton = permissionStatus == PermissionStatus.SHOW_SETTINGS
            PermissionDeniedContent(
                showSettingsButton = showSettingsButton,
                onGrantClick = { onPermissionGrant() },
                onSettingsClick = {
                    onSettingsClick()
                }
            )
        } else {
            when (val state = uiState) {
                is LocalContactsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is LocalContactsUiState.Success -> {
                    if (state.contacts.isEmpty()) {
                        EmptyContactsView()
                    } else {
                        ContactList(
                            contacts = state.contacts,
                            onContactClick = onContactClick,
                            state = listState
                        )
                    }
                }
                is LocalContactsUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                is LocalContactsUiState.Idle -> {}
            }
        }
    }

}

@Composable
fun ContactList(
    contacts: List<ContactDomain>,
    onContactClick: (ContactDomain) -> Unit,
    state: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        state = state,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(contacts) { contact ->
            ContactItem(
                contact = contact,
                onClick = { onContactClick(contact) }
            )
        }
    }
}

@Composable
fun PermissionDeniedContent(
    showSettingsButton: Boolean,
    onGrantClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Contacts,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = if (showSettingsButton)
                "Contacts access is permanently denied. Please enable it in system settings to use this feature."
            else "We need access to your local contacts to show them here.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { if (showSettingsButton) onSettingsClick() else onGrantClick() },
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(if (showSettingsButton) "Open Settings" else "Grant Permission")
        }
    }
}


@Preview
@Composable
private fun LocalContactsPrev() {
//    ConvoTheme {
//        LocalContacts(
//            modifier = Modifier.fillMaxSize()
//        )
//    }
}
