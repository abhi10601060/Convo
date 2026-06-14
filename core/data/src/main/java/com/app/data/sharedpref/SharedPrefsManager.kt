package com.app.data.sharedpref

import android.content.Context
import androidx.core.content.edit

class SharedPrefsManager(
    context: Context
) {
    private val prefs = context.getSharedPreferences(
        "app_prefs", Context.MODE_PRIVATE
    )

    var lastRoute: String
        get() = prefs.getString("last_route", "") ?: ""
        set(value) = prefs.edit { putString("last_route", value) }

    var isDarkTheme: Boolean?
        get() = if (prefs.contains("is_dark_theme")) prefs.getBoolean("is_dark_theme", false) else null
        set(value) = prefs.edit { 
            if (value != null) putBoolean("is_dark_theme", value) 
            else remove("is_dark_theme")
        }
}