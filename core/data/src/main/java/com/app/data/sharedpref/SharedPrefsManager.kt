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
}