package com.app.sms.domain.contract

import com.app.sms.data.model.Sms
import com.app.sms.domain.model.SmsDomain
import kotlinx.coroutines.flow.Flow

interface SmsRepo {
    fun getSmsMessages(): Flow<List<Sms>>
}
