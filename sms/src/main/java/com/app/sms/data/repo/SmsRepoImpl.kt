package com.app.sms.data.repo

import android.content.Context
import android.provider.Telephony
import com.app.sms.data.model.Sms
import com.app.sms.domain.contract.SmsRepo
import com.app.sms.domain.model.SmsDomain
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SmsRepoImpl(
    private val context: Context
) : SmsRepo {

    override fun getSmsMessages(): Flow<List<Sms>> = flow {
        val smsList = mutableListOf<Sms>()
        val cursor = context.contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            arrayOf(
                Telephony.Sms._ID,
                Telephony.Sms.ADDRESS,
                Telephony.Sms.BODY,
                Telephony.Sms.DATE,
                Telephony.Sms.READ
            ),
            null,
            null,
            "${Telephony.Sms.DATE} DESC"
        )

        cursor?.use {
            val idIndex = it.getColumnIndex(Telephony.Sms._ID)
            val addressIndex = it.getColumnIndex(Telephony.Sms.ADDRESS)
            val bodyIndex = it.getColumnIndex(Telephony.Sms.BODY)
            val dateIndex = it.getColumnIndex(Telephony.Sms.DATE)
            val readIndex = it.getColumnIndex(Telephony.Sms.READ)

            while (it.moveToNext()) {
                smsList.add(
                    Sms(
                        id = it.getString(idIndex),
                        address = it.getString(addressIndex) ?: "Unknown",
                        body = it.getString(bodyIndex) ?: "",
                        date = it.getLong(dateIndex),
                        read = it.getInt(readIndex) == 1
                    )
                )
            }
        }
        emit(smsList)
    }.flowOn(Dispatchers.IO)
}
