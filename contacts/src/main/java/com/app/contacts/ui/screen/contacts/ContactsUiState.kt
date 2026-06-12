package com.app.contacts.ui.screen.contacts

import com.app.contacts.domain.model.ContactDomain
import com.app.contacts.ui.util.PermissionStatus


data class ContactsScreenUiState(
    val localContactsUiState: ContactsUiState,
    val remoteContactsUiState: ContactsUiState,
    val permissionStatus: PermissionStatus
)

sealed interface ContactsUiState {
    object Idle : ContactsUiState
    object Loading : ContactsUiState
    data class Success(val contacts: List<ContactDomain>) : ContactsUiState
    data class Error(val message: String) : ContactsUiState
}