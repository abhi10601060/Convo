package com.app.contacts.data.paging.remote_contacts

import android.net.Uri
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.app.contacts.data.model.Contact
import com.app.network.api.ContactsService

class RemoteContactsPagingSource(
    private val contactsService: ContactsService
) : PagingSource<Int, Contact>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Contact> {
        val currentKey = params.key ?: 0
        val pageSize = params.loadSize
        return try {
            val response = contactsService.getContacts(limit = pageSize, skip = currentKey)
            val contactList = response.users.map { user ->
                Contact(
                    id = user.id.toString(),
                    displayName = "${user.firstName} ${user.lastName}",
                    phoneNumber = user.phone,
                    photoUri = Uri.parse(user.image),
                    email = user.email
                )
            }
            
            LoadResult.Page(
                data = contactList,
                prevKey = if (currentKey == 0) null else currentKey.minus(pageSize),
                nextKey = if (contactList.size < pageSize) null else currentKey.plus(pageSize)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Contact>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(state.config.pageSize) ?: anchorPage?.nextKey?.minus(state.config.pageSize)
        }
    }
}
