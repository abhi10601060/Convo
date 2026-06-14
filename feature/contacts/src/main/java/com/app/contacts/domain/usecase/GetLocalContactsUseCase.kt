package com.app.contacts.domain.usecase

import com.app.contacts.data.model.toDomain
import com.app.contacts.domain.contract.ContactsRepo
import javax.inject.Inject

class GetLocalContactsUseCase @Inject constructor(private val contactsRepo: ContactsRepo) {

    suspend operator fun invoke() = contactsRepo.getLocalContacts().map { it.toDomain() }
}