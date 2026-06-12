package com.app.contacts.ui.screen.contacts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.contacts.domain.usecase.GetLocalContactsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val getLocalContactsUseCase: GetLocalContactsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ContactsUiState>(ContactsUiState.Loading)
    val uiState: StateFlow<ContactsUiState> = _uiState.asStateFlow()

    fun loadContacts() {
        viewModelScope.launch {
            Log.d("ContactsViewModel", "loadContacts called")
            _uiState.value = ContactsUiState.Loading
            try {
                val contacts = getLocalContactsUseCase.invoke()
                Log.d("ContactsViewModel", "Loaded ${contacts.size} contacts")
                _uiState.value = ContactsUiState.Success(contacts)
            } catch (e: Exception) {
                Log.e("ContactsViewModel", "Error loading contacts", e)
                _uiState.value = ContactsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}