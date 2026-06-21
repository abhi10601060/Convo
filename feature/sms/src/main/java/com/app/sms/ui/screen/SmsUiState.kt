package com.app.sms.ui.screen

import androidx.paging.PagingData
import com.app.sms.domain.model.SmsDomain
import com.app.ui.util.PermissionStatus
import kotlinx.coroutines.flow.Flow

data class SmsScreenUiState(
    val smsUiState: SMSUiState,
    val permissionStatus: PermissionStatus = PermissionStatus.IDLE,
)


sealed interface SMSUiState{
    object Idle : SMSUiState
    object Loading: SMSUiState
    data class Success(val smsPagingData: Flow<PagingData<SmsDomain>>) : SMSUiState
    data class Error(val message: String): SMSUiState
}
