package com.app.contacts.ui.screen.contacts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.contacts.domain.usecase.GetLocalContactsUseCase
import com.app.contacts.domain.usecase.GetRemoteContactsUseCase
import com.app.contacts.ui.util.PermissionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val getLocalContactsUseCase: GetLocalContactsUseCase,
    private val getRemoteContactsUseCase: GetRemoteContactsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ContactsScreenUiState>(ContactsScreenUiState(localContactsUiState = ContactsUiState.Idle, remoteContactsUiState = ContactsUiState.Idle, permissionStatus = PermissionStatus.IDLE))
    val uiState: StateFlow<ContactsScreenUiState> = _uiState.asStateFlow()
    var hasAskedBeforeContactPermission = false;

    fun changeLocalContactsPermissionStatus(permissionStatus: PermissionStatus){
        _uiState.update {
            it.copy(permissionStatus = permissionStatus)
        }
    }

    fun loadLocalContacts() {
        viewModelScope.launch {
            Log.d("ContactsViewModel", "loadLocalContacts called")
            _uiState.update {
                it.copy(localContactsUiState = ContactsUiState.Loading)
            }
            try {
                val contacts = getLocalContactsUseCase.invoke()
                Log.d("ContactsViewModel", "Loaded ${contacts.size} local contacts")
                _uiState.update {
                    it.copy(localContactsUiState = ContactsUiState.Success(contacts))
                }
            } catch (e: Exception) {
                Log.e("ContactsViewModel", "Error loading local contacts", e)
                _uiState.update {
                    it.copy(localContactsUiState = ContactsUiState.Error(e.message ?: "Unknown error"))
                }
            }
        }
    }

    fun loadRemoteContacts() {
        viewModelScope.launch {
            Log.d("ContactsViewModel", "loadRemoteContacts called")
            _uiState.update {
                it.copy(remoteContactsUiState = ContactsUiState.Loading)
            }
            try {
                val contacts = getRemoteContactsUseCase.invoke()
                Log.d("ContactsViewModel", "Loaded ${contacts.size} remote contacts")
                _uiState.update {
                    it.copy(remoteContactsUiState = ContactsUiState.Success(contacts))
                }
            } catch (e: Exception) {
                Log.e("ContactsViewModel", "Error loading remote contacts", e)
                _uiState.update {
                    it.copy(remoteContactsUiState = ContactsUiState.Error(e.message ?: "Unknown error"))
                }
            }
        }
    }
}
