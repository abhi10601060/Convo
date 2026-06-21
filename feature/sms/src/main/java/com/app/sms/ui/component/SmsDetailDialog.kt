package com.app.sms.ui.component

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.app.sms.domain.model.SmsDomain
import com.app.ui.theme.ConvoTheme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SmsDetailDialog(
    sms: SmsDomain,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 650.dp),
        title = {
            Column {
                Text(
                    text = "From: ${sms.senderName ?: sms.address}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 2.dp)
            }
        },
        text = {
            Column {
                Box(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = sms.body,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 2.dp
                )

                Text(
                    text = "Received: ${formatFullDate(sms.date)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                content = {
                    Text("Reply")
                },
                onClick = {
                    context.startActivity(Intent(Intent.ACTION_SENDTO, "smsto:${sms.address}".toUri()))
                    onDismiss()
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

private fun formatFullDate(timestamp: Long): String {
    val date = Date(timestamp)
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(date)
}

@Preview
@Composable
private fun DetailedSMSPrev() {
    ConvoTheme {
        SmsDetailDialog(
            sms = SmsDomain.dummy
        ){

        }
    }
}
