package com.app.sms.domain.use_case

import com.app.sms.data.model.toDomain
import com.app.sms.domain.contract.SmsRepo
import com.app.sms.domain.model.SmsDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSmsMessagesUseCase @Inject constructor(
    private val smsRepo: SmsRepo
) {
    operator fun invoke(): Flow<List<SmsDomain>> {
        return smsRepo.getSmsMessages().map { list ->
            list.map { it.toDomain() }
        }
    }
}
