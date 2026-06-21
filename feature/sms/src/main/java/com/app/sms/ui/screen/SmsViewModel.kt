package com.app.sms.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.app.sms.domain.use_case.GetSmsMessagesUseCase
import com.app.ui.util.PermissionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SmsViewModel @Inject constructor(
    private val getSmsMessagesUseCase: GetSmsMessagesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SmsScreenUiState(smsUiState = SMSUiState.Idle))
    val uiState: StateFlow<SmsScreenUiState> = _uiState.asStateFlow()

    var hasAskedBeforeSmsPermission by mutableStateOf(false)

    fun loadSmsMessages() {
        _uiState.update { it.copy(smsUiState = SMSUiState.Success(
            getSmsMessagesUseCase().cachedIn(viewModelScope)
        )) }
    }

    fun changePermissionStatus(status: PermissionStatus) {
        _uiState.update { it.copy(permissionStatus = status) }
    }
}
