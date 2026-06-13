package com.app.contacts.ui.screen.contacts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.app.contacts.domain.model.ContactDomain
import com.app.contacts.domain.usecase.GetLocalContactsUseCase
import com.app.contacts.domain.usecase.GetRemoteContactsUseCase
import com.app.contacts.ui.util.PermissionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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

    private val _uiState = MutableStateFlow<ContactsScreenUiState>(ContactsScreenUiState(localContactsUiState = ContactsUiState.Idle, permissionStatus = PermissionStatus.IDLE))
    val uiState: StateFlow<ContactsScreenUiState> = _uiState.asStateFlow()
    var hasAskedBeforeContactPermission = false;

    val remoteContacts: Flow<PagingData<ContactDomain>> = getRemoteContactsUseCase(pageSize = 10)
        .cachedIn(viewModelScope)

    fun changeLocalContactsPermissionStatus(permissionStatus: PermissionStatus){
        _uiState.update {
            it.copy(permissionStatus = permissionStatus)
        }
    }

    fun loadLocalContacts() {
        viewModelScope.launch(Dispatchers.IO){
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
        // Paging handles loading, we don't need this manual load anymore
        // unless we want to trigger a refresh
    }
}
