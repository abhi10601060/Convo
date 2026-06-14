package com.app.contacts.domain.usecase

import androidx.paging.PagingData
import androidx.paging.map
import com.app.contacts.data.model.toDomain
import com.app.contacts.domain.contract.ContactsRepo
import com.app.contacts.domain.model.ContactDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetRemoteContactsUseCase @Inject constructor(
    private val repo: ContactsRepo
) {
    operator fun invoke(pageSize: Int = 10): Flow<PagingData<ContactDomain>> {
        return repo.getRemoteContactsPaged(pageSize).map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }
}
