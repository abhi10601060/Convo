package com.app.contacts.data.model

import android.net.Uri
import com.app.contacts.domain.model.ContactDomain

data class Contact(
    val id: String,
    val displayName: String,
    val phoneNumber: String?,
    val photoUri: Uri?,
    val email: String? = null
)

fun Contact.toDomain() : ContactDomain{
    return ContactDomain(
        id = this.id,
        displayName = this.displayName,
        phoneNumber = this.phoneNumber,
        photoUri = this.photoUri,
        email = this.email
    )
}

