package com.example.tawbaapp.Classes

import android.content.Context


class AppPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveDarkMode(isDark: Boolean) {
        prefs.edit().putBoolean("dark_mode", isDark).apply()
    }

    fun loadDarkMode(): Boolean {
        return prefs.getBoolean("dark_mode", false)
    }

    fun isFirstLaunch(): Boolean = prefs.getBoolean("is_first_launch", true)
    fun setFirstLaunchDone() {
        prefs.edit().putBoolean("is_first_launch", false).apply()
    }
}
