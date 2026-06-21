package com.app.sms.data.paging

import android.content.Context
import android.provider.Telephony
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.app.sms.data.model.Sms
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SmsPagingSource(
    private val context: Context,
    private val getContactName: (Context, String) -> String?
) : PagingSource<Int, Sms>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Sms> {
        val position = params.key ?: 0
        val pageSize = params.loadSize

        return withContext(Dispatchers.IO) {
            try {
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
                    "${Telephony.Sms.DATE} DESC LIMIT $pageSize OFFSET $position"
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

                LoadResult.Page(
                    data = smsList,
                    prevKey = if (position == 0) null else position - pageSize,
                    nextKey = if (smsList.size < pageSize) null else position + pageSize
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Sms>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(state.config.pageSize) ?: anchorPage?.nextKey?.minus(state.config.pageSize)
        }
    }
}
