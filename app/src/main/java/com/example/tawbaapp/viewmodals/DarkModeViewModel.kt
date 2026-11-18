package com.example.tawbaapp.viewmodals

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.tawbaapp.classes.AppPreferences


class DarkModeViewModel(application: Application) : AndroidViewModel(application) {
    private val appPreferences = AppPreferences(application)

    private val _isDark = MutableStateFlow(appPreferences.loadDarkMode())
    val isDark: StateFlow<Boolean> = _isDark

    fun toggleTheme() {
        _isDark.value = !_isDark.value
        appPreferences.saveDarkMode(_isDark.value)
    }

}
