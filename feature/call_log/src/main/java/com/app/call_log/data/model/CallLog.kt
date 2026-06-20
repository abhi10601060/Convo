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
    val hours = duration / 3600
    val minutes = (duration % 3600) / 60
    val seconds = duration % 60
    
    return buildString {
        if (hours > 0) append("${hours}h ")
        if (minutes > 0 || hours > 0) append("${minutes}m ")
        append("${seconds}s")
    }.trim()
}
