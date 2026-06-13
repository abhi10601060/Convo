package com.app.call_log.data.repo

import android.content.Context
import android.provider.CallLog
import com.app.call_log.data.model.CallLog as CallLogData
import com.app.call_log.data.model.toDomain
import com.app.call_log.domain.contract.CallLogRepo
import com.app.call_log.domain.model.CallLogDomain
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.UUID
import javax.inject.Inject

class CallLogRepoImpl (
    private val context: Context
) : CallLogRepo {

    override fun getCallLogs(): Flow<List<CallLogData>> = flow {
        val callLogs = mutableListOf<CallLogData>()
        val cursor = context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            arrayOf(
                CallLog.Calls._ID,
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION
            ),
            null,
            null,
            "${CallLog.Calls.DATE} DESC"
        )

        cursor?.use {
            val idIndex = it.getColumnIndex(CallLog.Calls._ID)
            val nameIndex = it.getColumnIndex(CallLog.Calls.CACHED_NAME)
            val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
            val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)
            val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
            val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)

            while (it.moveToNext()) {
                val number = it.getString(numberIndex)
                if (!number.isNullOrBlank()) {
                    callLogs.add(
                        CallLogData(
                            id = it.getString(idIndex) ?: UUID.randomUUID().toString(),
                            name = it.getString(nameIndex),
                            number = number,
                            type = it.getInt(typeIndex),
                            date = it.getLong(dateIndex),
                            duration = it.getLong(durationIndex)
                        )
                    )
                }
            }
        }
        emit(callLogs)
    }.flowOn(Dispatchers.IO)
}
