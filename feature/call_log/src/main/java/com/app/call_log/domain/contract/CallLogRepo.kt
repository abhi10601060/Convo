package com.app.call_log.domain.contract

import com.app.call_log.data.model.CallLog
import com.app.call_log.domain.model.CallLogDomain
import kotlinx.coroutines.flow.Flow

interface CallLogRepo {
    fun getCallLogs(): Flow<List<CallLog>>
}
