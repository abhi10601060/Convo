package com.app.sms.domain.model

data class SmsDomain(
    val id: String,
    val address: String, // Sender number
    val senderName: String? = null,
    val body: String,    // Message
    val date: Long,      // Timestamp
    val read: Boolean
)
