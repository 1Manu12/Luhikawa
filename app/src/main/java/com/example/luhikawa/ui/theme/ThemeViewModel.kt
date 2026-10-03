package com.example.luhikawa.ui.theme

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "user_themes")

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val _selectedTheme = MutableStateFlow(AppTheme.BEIGE)
    val selectedTheme: StateFlow<AppTheme> = _selectedTheme.asStateFlow()

    private var currentUserId: String = "guest"

    fun setUser(userId: String) {
        if (userId.isBlank()) return
        currentUserId = userId

        viewModelScope.launch {
            val userThemeKey = stringPreferencesKey("theme_$currentUserId")
            getApplication<Application>().dataStore.data.collectLatest { prefs ->
                val savedThemeName = prefs[userThemeKey] ?: AppTheme.BEIGE.name
                _selectedTheme.value = try {
                    AppTheme.valueOf(savedThemeName)
                } catch (e: Exception) {
                    AppTheme.BEIGE
                }
            }
        }
    }

    fun setTheme(theme: AppTheme) {
        _selectedTheme.value = theme
        viewModelScope.launch {
            val userThemeKey = stringPreferencesKey("theme_$currentUserId")
            getApplication<Application>().dataStore.edit { prefs ->
                prefs[userThemeKey] = theme.name
            }
        }
    }
}