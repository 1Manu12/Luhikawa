package com.example.luhikawa.ui.HomeComponents

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_theme_prefs")

enum class AppThemeColor(val hex: String) {
    BEIGE("0xFFC7AF93"),
    BLUE("0xFF2196F3"),
    GREEN("0xFF4CAF50"),
    PURPLE("0xFF9C27B0")
}

class UserThemeManager(private val context: Context) {

    suspend fun saveUserTheme(userId: String, theme: AppThemeColor) {
        val key = stringPreferencesKey("theme_user_$userId")
        context.dataStore.edit { preferences ->
            preferences[key] = theme.name
        }
    }

    fun getUserTheme(userId: String): Flow<AppThemeColor> {
        val key = stringPreferencesKey("theme_user_$userId")
        return context.dataStore.data.map { preferences ->
            val themeName = preferences[key] ?: AppThemeColor.BEIGE.name
            try {
                AppThemeColor.valueOf(themeName)
            } catch (e: Exception) {
                AppThemeColor.BEIGE
            }
        }
    }
}