package com.example.luhikawa.data

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import com.example.luhikawa.MyApp

private val Context.dataStore by preferencesDataStore(name = "simple_double_prefs")

class storage(private val context: Context) {

    companion object {
        private val BIAS_KEY = doublePreferencesKey("bias")
    }

    fun save(value: Double) {
        runBlocking {
            context.dataStore.edit { preferences ->
                preferences[BIAS_KEY] = value
            }
        }
    }

    fun read(defaultValue: Double = 0.0): Double {
        return runBlocking {
            val preferences = context.dataStore.data.first()
            preferences[BIAS_KEY] ?: defaultValue
        }
    }
}