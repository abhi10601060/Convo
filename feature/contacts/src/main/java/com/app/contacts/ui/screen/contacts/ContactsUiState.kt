package com.app.contacts.ui.screen.contacts

import androidx.paging.PagingData
import com.app.contacts.domain.model.ContactDomain
import com.app.ui.util.PermissionStatus
import kotlinx.coroutines.flow.Flow


data class ContactsScreenUiState(
    val localContactsUiState: LocalContactsUiState,
    val remoteContactsUiState: RemoteContactsUiState,
    val permissionStatus: PermissionStatus
)

sealed interface LocalContactsUiState {
    object Idle : LocalContactsUiState
    object Loading : LocalContactsUiState
    data class Success(val contacts: List<ContactDomain>) : LocalContactsUiState
    data class Error(val message: String) : LocalContactsUiState
}

sealed interface RemoteContactsUiState {
    object Idle : RemoteContactsUiState
    data class Success(val pagingData: Flow<PagingData<ContactDomain>>) : RemoteContactsUiState
}
