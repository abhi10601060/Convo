package com.app.sms.domain.contract

import com.app.sms.domain.model.SmsDomain
import kotlinx.coroutines.flow.Flow

interface SmsRepo {
    fun getSmsMessages(): Flow<List<SmsDomain>>
}
