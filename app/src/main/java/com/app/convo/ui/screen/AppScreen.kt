package com.app.convo.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.app.convo.ui.navigation.MainNavGraphRoute
import com.app.convo.ui.navigation.ScreenRoute
import com.app.convo.ui.navigation.mainNavGraph
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

@Composable
fun ThemeToggleButton(
    isDarkTheme: Boolean,
    onToggle: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isDarkTheme) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current,
                onClick = onToggle
            ),
        contentAlignment = Alignment.Center
    ) {
        Crossfade(
            targetState = isDarkTheme,
            animationSpec = tween(durationMillis = 500),
            label = "themeIcon"
        ) { isDark ->
            Icon(
                imageVector = if (isDark) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                contentDescription = "Toggle Theme",
                modifier = Modifier
                    .size(24.dp)
                    .rotate(rotation),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
