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
        val contactsMap = mutableMapOf<String, Contact>()
        val contentResolver: ContentResolver = context.contentResolver

        Log.d("ContactRepoImpl", "Querying local contacts (Phone & Email)...")

        // Query for Phones and Emails using Data table for efficiency
        val cursor = contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            arrayOf(
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.Data.MIMETYPE,
                ContactsContract.Data.DATA1, // Number or Email
                ContactsContract.Data.PHOTO_THUMBNAIL_URI
            ),
            "${ContactsContract.Data.MIMETYPE} IN (?, ?)",
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
            ),
            ContactsContract.Data.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            val idIndex = it.getColumnIndex(ContactsContract.Data.CONTACT_ID)
            val nameIndex = it.getColumnIndex(ContactsContract.Data.DISPLAY_NAME)
            val mimeIndex = it.getColumnIndex(ContactsContract.Data.MIMETYPE)
            val dataIndex = it.getColumnIndex(ContactsContract.Data.DATA1)
            val photoIndex = it.getColumnIndex(ContactsContract.Data.PHOTO_THUMBNAIL_URI)

            while (it.moveToNext()) {
                val id = it.getString(idIndex) ?: continue
                val name = it.getString(nameIndex) ?: "Unknown"
                val mimeType = it.getString(mimeIndex)
                val dataValue = it.getString(dataIndex)
                val photoUriStr = it.getString(photoIndex)

                val existingContact = contactsMap[id] ?: Contact(
                    id = id,
                    displayName = name,
                    phoneNumber = null,
                    photoUri = photoUriStr?.let { Uri.parse(it) },
                    email = null
                )

                val updatedContact = when (mimeType) {
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE -> {
                        existingContact.copy(phoneNumber = dataValue)
                    }
                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE -> {
                        existingContact.copy(email = dataValue)
                    }
                    else -> existingContact
                }
                
                contactsMap[id] = updatedContact
            }
        }
        contactsMap.values.toList()
    }

    override suspend fun getRemoteContacts(limit: Int, skip: Int): List<Contact> = withContext(Dispatchers.IO) {
        Log.d("ContactRepoImpl", "Fetching remote contacts (limit=$limit, skip=$skip)...")
        val response = contactsService.getContacts(limit, skip)
        response.users.map { user ->
            Contact(
                id = user.id.toString(),
                displayName = "${user.firstName} ${user.lastName}",
                phoneNumber = user.phone,
                photoUri = Uri.parse(user.image),
                email = user.email
            )
        }
    }

    override fun getRemoteContactsPaged(pageSize: Int): Flow<PagingData<Contact>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false,
                prefetchDistance = 2,
                initialLoadSize = 10
            ),
            pagingSourceFactory = { RemoteContactsPagingSource(contactsService) }
        ).flow
    }
}
