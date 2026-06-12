package com.app.contacts.ui.screen.contacts

import com.app.contacts.domain.model.ContactDomain

sealed interface ContactsUiState {
    object Loading : ContactsUiState
    data class Success(val contacts: List<ContactDomain>) : ContactsUiState
    data class Error(val message: String) : ContactsUiState
}