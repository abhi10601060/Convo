package com.app.sms.data.repo

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.app.sms.data.model.Sms
import com.app.sms.data.paging.SmsPagingSource
import com.app.sms.domain.contract.SmsRepo
import kotlinx.coroutines.flow.Flow

class SmsRepoImpl(
    private val context: Context
) : SmsRepo {

    override fun getSmsMessages(): Flow<PagingData<Sms>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SmsPagingSource(context, ::getContactName)
            }
        ).flow
    }

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
