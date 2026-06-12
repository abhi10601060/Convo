package com.app.contacts.domain.contract

import com.app.contacts.data.model.Contact

interface ContactsRepo {

    suspend fun getLocalContacts() : List<Contact>
}