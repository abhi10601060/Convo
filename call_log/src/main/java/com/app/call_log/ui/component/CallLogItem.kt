package com.app.call_log.ui.component

import android.provider.CallLog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallMissed
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.call_log.domain.model.CallLogDomain
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CallLogItem(
    callLog: CallLogDomain,
    onCallClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCallClick() }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(
                    if (callLog.name.isNullOrBlank()) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            if (callLog.name.isNullOrBlank()) {
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = callLog.name.firstOrNull()?.toString()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = if (!callLog.name.isNullOrBlank()) callLog.name else callLog.number,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val (icon, tint) = when (callLog.type) {
                    CallLog.Calls.INCOMING_TYPE -> Icons.AutoMirrored.Filled.CallReceived to MaterialTheme.colorScheme.onSurfaceVariant
                    CallLog.Calls.OUTGOING_TYPE -> Icons.AutoMirrored.Filled.CallMade to MaterialTheme.colorScheme.onSurfaceVariant
                    CallLog.Calls.MISSED_TYPE -> Icons.AutoMirrored.Filled.CallMissed to MaterialTheme.colorScheme.error
                    else -> Icons.AutoMirrored.Filled.CallReceived to MaterialTheme.colorScheme.onSurfaceVariant
                }
                
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = tint
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                val typeText = when (callLog.type) {
                    CallLog.Calls.INCOMING_TYPE -> "Mobile" // Matching design's label style
                    CallLog.Calls.OUTGOING_TYPE -> "Mobile"
                    CallLog.Calls.MISSED_TYPE -> "Missed"
                    else -> "Mobile"
                }
                
                Text(
                    text = "$typeText • ${formatTime(callLog.date)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (callLog.type == CallLog.Calls.MISSED_TYPE) MaterialTheme.colorScheme.error 
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
        
        Icon(
            imageVector = Icons.Outlined.Phone,
            contentDescription = "Call",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            modifier = Modifier
                .size(24.dp)
                .clickable { onCallClick() }
        )
    }
}

private fun formatTime(timestamp: Long): String {
    val date = Date(timestamp)
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    return sdf.format(date)
}
