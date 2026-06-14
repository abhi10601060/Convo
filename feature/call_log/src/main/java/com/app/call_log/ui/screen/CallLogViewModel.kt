package com.app.call_log.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.call_log.domain.use_case.GetCallLogsUseCase
import com.app.ui.util.PermissionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CallLogViewModel @Inject constructor(
    private val getCallLogsUseCase: GetCallLogsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CallLogScreenUiState())
    val uiState: StateFlow<CallLogScreenUiState> = _uiState.asStateFlow()

    var hasAskedBeforeCallLogPermission by mutableStateOf(false)

    fun loadCallLogs() {
        viewModelScope.launch {
            _uiState.update { it.copy(callLogUiState = CallLogUiState.Loading) }
            getCallLogsUseCase().collect { logs ->
                _uiState.update { it.copy(callLogUiState = CallLogUiState.Success(logs)) }
            }
        }
    }

    fun changePermissionStatus(status: PermissionStatus) {
        _uiState.update { it.copy(permissionStatus = status) }
    }
}
