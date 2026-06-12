package com.app.contacts.data.repo

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import com.app.contacts.data.model.Contact
import com.app.contacts.domain.contract.ContactsRepo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

class ContactRepoImpl(
     private val context: Context
) : ContactsRepo{
    override suspend fun getLocalContacts(): List<Contact> = withContext(Dispatchers.IO) {
        val contacts = mutableListOf<Contact>()
        val contentResolver: ContentResolver = context.contentResolver

        Log.d("ContactRepoImpl", "Querying contacts...")

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        Log.d("ContactRepoImpl", "Cursor is null: ${cursor == null}")

        cursor?.use {
            Log.d("ContactRepoImpl", "Cursor count: ${it.count}")

            val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val photoIndex =
                it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI)

            while (it.moveToNext()) {
                val id = if (idIndex != -1) it.getString(idIndex) else ""
                val name = if (nameIndex != -1) it.getString(nameIndex) else "Unknown"
                val number = if (numberIndex != -1) it.getString(numberIndex) else null
                val photoUriStr = if (photoIndex != -1) it.getString(photoIndex) else null
                val photoUri = if (photoUriStr != null) Uri.parse(photoUriStr) else null

                contacts.add(Contact(id, name, number, photoUri))
            }
        }
        val result = contacts.distinctBy { it.phoneNumber }
        Log.d("ContactRepoImpl", "Returning ${result.size} distinct contacts")
        result
    }
}