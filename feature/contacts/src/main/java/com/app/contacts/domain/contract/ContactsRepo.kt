package com.app.contacts.domain.contract

import androidx.paging.PagingData
import com.app.contacts.data.model.Contact
import kotlinx.coroutines.flow.Flow

interface ContactsRepo {
    suspend fun getLocalContacts(): List<Contact>
    suspend fun getRemoteContacts(limit: Int, skip: Int): List<Contact>
    fun getRemoteContactsPaged(pageSize: Int): Flow<PagingData<Contact>>
}
