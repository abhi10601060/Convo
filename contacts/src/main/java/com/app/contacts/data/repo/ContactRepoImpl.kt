package com.app.contacts.data.repo

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.app.contacts.data.model.Contact
import com.app.contacts.domain.contract.ContactsRepo
import com.app.contacts.paging.remote_contacts.RemoteContactsPagingSource
import com.app.network.api.ContactsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ContactRepoImpl(
    private val context: Context,
    private val contactsService: ContactsService
) : ContactsRepo {
    override suspend fun getLocalContacts(): List<Contact> = withContext(Dispatchers.IO) {
        val contacts = mutableListOf<Contact>()
        val contentResolver: ContentResolver = context.contentResolver

        Log.d("ContactRepoImpl", "Querying local contacts...")

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val photoIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI)

            while (it.moveToNext()) {
                val id = if (idIndex != -1) it.getString(idIndex) else ""
                val name = if (nameIndex != -1) it.getString(nameIndex) else "Unknown"
                val number = if (numberIndex != -1) it.getString(numberIndex) else null
                val photoUriStr = if (photoIndex != -1) it.getString(photoIndex) else null
                val photoUri = if (photoUriStr != null) Uri.parse(photoUriStr) else null

                contacts.add(Contact(id, name, number, photoUri))
            }
        }
        contacts.distinctBy { it.phoneNumber }
    }

    override suspend fun getRemoteContacts(limit: Int, skip: Int): List<Contact> = withContext(Dispatchers.IO) {
        Log.d("ContactRepoImpl", "Fetching remote contacts (limit=$limit, skip=$skip)...")
        val response = contactsService.getContacts(limit, skip)
        response.users.map { user ->
            Contact(
                id = user.id.toString(),
                displayName = "${user.firstName} ${user.lastName}",
                phoneNumber = user.phone,
                photoUri = Uri.parse(user.image)
            )
        }
    }

    override fun getRemoteContactsPaged(pageSize: Int): Flow<PagingData<Contact>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { RemoteContactsPagingSource(contactsService) }
        ).flow
    }
}
