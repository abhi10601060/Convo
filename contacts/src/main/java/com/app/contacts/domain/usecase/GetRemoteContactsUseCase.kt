package com.app.contacts.domain.usecase

import com.app.contacts.data.model.toDomain
import com.app.contacts.domain.contract.ContactsRepo
import com.app.contacts.domain.model.ContactDomain
import javax.inject.Inject

class GetRemoteContactsUseCase @Inject constructor(
    private val repo: ContactsRepo
) {
    suspend operator fun invoke(limit: Int = 10, skip: Int = 0): List<ContactDomain> {
        return repo.getRemoteContacts(limit, skip).map { it.toDomain() }
    }
}
