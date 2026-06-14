package com.app.sms.domain.model

import android.icu.util.Calendar

data class SmsDomain(
    val id: String,
    val address: String, // Sender number
    val senderName: String? = null,
    val body: String,    // Message
    val date: Long,      // Timestamp
    val read: Boolean
){
    companion object{
        val dummy = SmsDomain(
            id = "1",
            address = "2345677890",
            senderName = "Abhishek Velekar",
            body = "This is a large Body",
            date = Calendar.getInstance().time.time,
            read = true
        )
    }
}
