package com.app.convo.ui.screen

import androidx.lifecycle.ViewModel
import com.app.data.sharedpref.SharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AppScreenViewModel @Inject constructor(
    private val sharedPrefsManager: SharedPrefsManager
): ViewModel() {

    private var _currentScreen = MutableStateFlow(getScreenFromStringRoute(sharedPrefsManager.lastRoute))
    val currentScreen = _currentScreen.asStateFlow()

    fun updateCurrentScreen(currentScreen: Screen){
        _currentScreen.update { currentScreen }
        sharedPrefsManager.lastRoute = currentScreen.name
    }

    private fun getScreenFromStringRoute(stringRoute: String) : Screen{
        return try {
            Screen.valueOf(stringRoute)
        } catch (e : Exception){
            Screen.Contacts
        }
    }
}