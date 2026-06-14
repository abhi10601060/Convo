package com.app.call_log.domain.use_case

import com.app.call_log.data.model.toDomain
import com.app.call_log.domain.contract.CallLogRepo
import com.app.call_log.domain.model.CallLogDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCallLogsUseCase @Inject constructor(
    private val callLogRepo: CallLogRepo
) {
    operator fun invoke(): Flow<List<CallLogDomain>> {
        return callLogRepo.getCallLogs().map { list -> list.map { it.toDomain() } }
    }
}
