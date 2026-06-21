package com.app.sms.domain.contract

import androidx.paging.PagingData
import com.app.sms.data.model.Sms
import kotlinx.coroutines.flow.Flow

interface SmsRepo {
    fun getSmsMessages(): Flow<PagingData<Sms>>
}
