package com.app.contacts.domain.model

import android.net.Uri

data class ContactDomain(
    val id: String,
    val displayName: String,
    val phoneNumber: String?,
    val photoUri: Uri?,
    val email: String? = null
){
    companion object{
        val dummy = ContactDomain(
            id = "1",
            displayName = "Abhi Velekar",
            phoneNumber = "7772228976",
            photoUri = null,
            email = "abhi@example.com"
        )
    }
}