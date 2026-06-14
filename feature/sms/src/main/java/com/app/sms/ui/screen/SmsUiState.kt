package com.app.sms.ui.screen

import com.app.sms.domain.model.SmsDomain
import com.app.sms.ui.util.PermissionStatus

data class SmsScreenUiState(
    val smsUiState: SMSUiState,
    val permissionStatus: PermissionStatus = PermissionStatus.IDLE,
)


sealed interface SMSUiState{
    object Idle : SMSUiState
    object Loading: SMSUiState
    data class Success(val smsList: List<SmsDomain>) : SMSUiState
    data class Error(val message: String): SMSUiState
}