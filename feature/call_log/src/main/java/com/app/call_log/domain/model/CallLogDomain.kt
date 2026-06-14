package com.app.call_log.domain.model

data class CallLogDomain(
    val id: String,
    val name: String?,
    val number: String,
    val type: Int, // Incoming, Outgoing, Missed
    val date: Long,
    val duration: Long,
    val formattedDuration: String
)
