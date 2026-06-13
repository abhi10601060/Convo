package com.app.convo.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.convo.ui.component.ThemeToggleButton
import com.app.convo.ui.navigation.MainNavGraphRoute
import com.app.convo.ui.navigation.ScreenRoute
import com.app.convo.ui.navigation.mainNavGraph
import com.app.ui.theme.ConvoTheme
import kotlinx.coroutines.launch

enum class Screen(val title: String, val icon: ImageVector, val screenRoute: ScreenRoute) {
    Contacts("Contacts", Icons.Default.Contacts, ScreenRoute.Contacts),
    CallLog("Call Log", Icons.Default.Call, ScreenRoute.CallLogs),
    Messages("Messages", Icons.Default.Sms, ScreenRoute.Messages)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    var currentScreen by remember { mutableStateOf(Screen.Contacts) }

    // Sync currentScreen with navigation state
    LaunchedEffect(currentDestination) {
        Screen.entries.forEach { screen ->
            if (currentDestination?.route?.contains(screen.screenRoute::class.qualifiedName ?: "") == true) {
                currentScreen = screen
            }
        }
    }

    val navigateToScreen: (Screen) -> Unit = { screen ->
        navController.navigate(screen.screenRoute) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        // Circular Icon
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubble,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = "Convo",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    ThemeToggleButton(
                        isDarkTheme = isDarkTheme,
                        onToggle = onThemeToggle
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Screen.entries.forEach { screen ->
                    NavigationDrawerItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentScreen == screen,
                        onClick = {
                            navigateToScreen(screen)
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(currentScreen.title) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                NavHost(navController = navController, startDestination = MainNavGraphRoute){
                    mainNavGraph(navController = navController)
                }

                BackHandler(enabled = drawerState.isOpen) {
                    scope.launch { drawerState.close() }
                }
            }
        }
    }
}

@Preview
@Composable
private fun AppScreenPrev() {
    ConvoTheme {
        AppScreen(false) {

        }
    }
}