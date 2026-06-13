package com.app.convo.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.app.call_log.ui.screen.CallLogScreen
import com.app.contacts.ui.screen.contacts.ContactsScreen
import com.app.sms.ui.screen.SmsScreen
import kotlinx.serialization.Serializable

fun NavGraphBuilder.mainNavGraph(navController: NavController){
    navigation<MainNavGraphRoute>(
        startDestination = ScreenRoute.Contacts
    ){
        composable<ScreenRoute.Contacts> {
            ContactsScreen()
        }

        composable<ScreenRoute.CallLogs> {
            CallLogScreen()
        }

        composable<ScreenRoute.Messages> {
            SmsScreen()
        }
    }
}

@Serializable
object MainNavGraphRoute

@Serializable
sealed class ScreenRoute(val title: String){
    @Serializable object Contacts: ScreenRoute("Contacts")
    @Serializable object CallLogs: ScreenRoute("Call Logs")
    @Serializable object Messages: ScreenRoute("Messages")
}