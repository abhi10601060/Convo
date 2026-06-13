package com.app.convo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.app.contacts.ui.screen.contacts.ContactsScreen
import com.app.sms.ui.screen.SmsScreen
import com.app.ui.components.ConvoTabs
import com.app.ui.theme.ConvoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConvoTheme {
                var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
                Scaffold(
                    topBar = {
                        ConvoTabs(
                            titles = listOf("Contacts", "SMS"),
                            selectedIndex = selectedTabIndex,
                            onTabSelected = { selectedTabIndex = it }
                        )
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        if (selectedTabIndex == 0) {
                            ContactsScreen()
                        } else {
                            SmsScreen()
                        }
                    }
                }
            }
        }
    }
}
