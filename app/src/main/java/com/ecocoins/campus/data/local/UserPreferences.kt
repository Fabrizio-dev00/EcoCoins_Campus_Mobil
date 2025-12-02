package com.ecocoins.campus.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {

    companion object {
        private val USER_ID = longPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_TOKEN = stringPreferencesKey("user_token")
        private val ECO_COINS = longPreferencesKey("eco_coins")
        private val IS_LOGGED_IN = stringPreferencesKey("is_logged_in")
    }

    val userId: Flow<Long?> = context.dataStore.data.map { preferences ->
        preferences[USER_ID]
    }

    val userName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME]
    }

    val userEmail: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_EMAIL]
    }

    val userToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_TOKEN]
    }

    val ecoCoins: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[ECO_COINS] ?: 0L
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] == "true"
    }

    suspend fun saveUserData(
        userId: Long,
        name: String,
        email: String,
        token: String,
        ecoCoins: Long
    ) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = userId
            preferences[USER_NAME] = name
            preferences[USER_EMAIL] = email
            preferences[USER_TOKEN] = token
            preferences[ECO_COINS] = ecoCoins
            preferences[IS_LOGGED_IN] = "true"
        }
    }

    suspend fun updateEcoCoins(newBalance: Long) {
        context.dataStore.edit { preferences ->
            preferences[ECO_COINS] = newBalance
        }
    }

    suspend fun clearUserData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}