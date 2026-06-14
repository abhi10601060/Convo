package com.app.call_log.ui.screen

import com.app.call_log.domain.model.CallLogDomain
import com.app.call_log.ui.util.PermissionStatus

data class CallLogScreenUiState(
    val callLogUiState: CallLogUiState = CallLogUiState.Idle,
    val permissionStatus: PermissionStatus = PermissionStatus.IDLE,
)

sealed interface CallLogUiState {
    object Idle : CallLogUiState
    object Loading : CallLogUiState
    data class Success(val callLogs: List<CallLogDomain>) : CallLogUiState
    data class Error(val message: String) : CallLogUiState
}
