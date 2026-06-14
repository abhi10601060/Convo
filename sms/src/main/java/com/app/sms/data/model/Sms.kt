package com.app.sms.data.model

import com.app.sms.domain.model.SmsDomain

data class Sms(
    val id: String,
    val address: String, 
    val senderName: String? = null,
    val body: String,    
    val date: Long,  
    val read: Boolean
)

fun Sms.toDomain(): SmsDomain {
    return SmsDomain(
        id = id,
        address = address,
        senderName = senderName,
        body = body,
        date = date,
        read = read
    )
}
