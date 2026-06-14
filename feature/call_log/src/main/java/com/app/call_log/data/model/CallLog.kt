package com.app.call_log.data.model

import com.app.call_log.domain.model.CallLogDomain

data class CallLog(
    val id: String,
    val name: String?,
    val number: String,
    val type: Int,
    val date: Long,
    val duration: Long
)

fun CallLog.toDomain(): CallLogDomain {
    return CallLogDomain(
        id = id,
        name = name,
        number = number,
        type = type,
        date = date,
        duration = duration,
        formattedDuration = formatDuration(duration)
    )
}

private fun formatDuration(duration: Long): String {
    val minutes = duration / 60
    val seconds = duration % 60
    return if (minutes > 0) "${minutes}m ${seconds}s" else "${seconds}s"
}
