package com.app.network.api

import com.app.network.model.RemoteContactsDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface ContactsService {
    @GET("users")
    suspend fun getContacts(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): RemoteContactsDTO
}
