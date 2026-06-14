package com.app.sms.data.repo

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.provider.Telephony
import com.app.sms.data.model.Sms
import com.app.sms.domain.contract.SmsRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SmsRepoImpl(
    private val context: Context
) : SmsRepo {

    override fun getSmsMessages(): Flow<List<Sms>> = flow {
        val smsList = mutableListOf<Sms>()
        val foundContactNames = mutableMapOf<String, String?>()

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
                val address = it.getString(addressIndex) ?: "Unknown"
                val senderName = if (address != "Unknown") {
                    foundContactNames.getOrPut(address) {
                        getContactName(context, address)
                    }
                } else {
                    null
                }

                smsList.add(
                    Sms(
                        id = it.getString(idIndex),
                        address = address,
                        senderName = senderName,
                        body = it.getString(bodyIndex) ?: "",
                        date = it.getLong(dateIndex),
                        read = it.getInt(readIndex) == 1
                    )
                )
            }
        }
        emit(smsList)
    }.flowOn(Dispatchers.IO)

    private fun getContactName(context: Context, phoneNumber: String): String? {
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
        return try {
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getString(0)
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }
}
