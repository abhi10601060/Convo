package com.app.ui.components

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.app.ui.theme.ConvoTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactActionBottomSheet(
    phoneNumber: String?,
    email: String?,
    onDismissRequest: () -> Unit,
    title: String = "Action"
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    val hideAndDismiss = {
        scope.launch {
            sheetState.hide()
            onDismissRequest()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            phoneNumber?.let { number ->
                ListItem(
                    headlineContent = { Text("Call") },
                    leadingContent = { Icon(Icons.Default.Call, contentDescription = null) },
                    modifier = Modifier.clickable {
                        context.startActivity(Intent(Intent.ACTION_DIAL, "tel:$number".toUri()))
                        hideAndDismiss()
                    }
                )
                ListItem(
                    headlineContent = { Text("SMS") },
                    leadingContent = { Icon(Icons.Default.Sms, contentDescription = null) },
                    modifier = Modifier.clickable {
                        context.startActivity(Intent(Intent.ACTION_SENDTO, "smsto:$number".toUri()))
                        hideAndDismiss()
                    }
                )
            }

            email?.let { mail ->
                ListItem(
                    headlineContent = { Text("Email") },
                    leadingContent = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.clickable {
                        context.startActivity(Intent(Intent.ACTION_SENDTO, "mailto:$mail".toUri()))
                        hideAndDismiss()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactActionBottomSheetPreview() {
    ConvoTheme {
        ContactActionBottomSheet(
            phoneNumber = "1234567890",
            email = "test@example.com",
            onDismissRequest = {}
        )
    }
}
