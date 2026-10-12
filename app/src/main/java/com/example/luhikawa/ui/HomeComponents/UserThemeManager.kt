package com.example.luhikawa.ui.HomeComponents

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.luhikawa.ui.theme.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_theme_prefs")

class UserThemeManager(private val context: Context) {

    suspend fun saveUserTheme(userId: String, theme: AppTheme) {
        val key = stringPreferencesKey("theme_user_$userId")
        context.dataStore.edit { preferences ->
            preferences[key] = theme.name
        }
    }

    fun getUserTheme(userId: String): Flow<AppTheme> {
        val key = stringPreferencesKey("theme_user_$userId")
        return context.dataStore.data.map { preferences ->
            val themeName = preferences[key] ?: AppTheme.BEIGE.name
            try {
                AppTheme.valueOf(themeName)
            } catch (e: Exception) {
                AppTheme.BEIGE
            }
        }
    }
}