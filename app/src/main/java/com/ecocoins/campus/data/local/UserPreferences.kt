package com.ecocoins.campus.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val ECO_COINS_KEY = doublePreferencesKey("eco_coins")
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
    }

    suspend fun saveUserSession(
        token: String,
        userId: String,
        userName: String,
        email: String,
        ecoCoins: Double
    ) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[USER_ID_KEY] = userId
            prefs[USER_NAME_KEY] = userName
            prefs[USER_EMAIL_KEY] = email
            prefs[ECO_COINS_KEY] = ecoCoins
            prefs[IS_LOGGED_IN_KEY] = true
        }
    }

    val authToken: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[TOKEN_KEY]
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_LOGGED_IN_KEY] ?: false
    }

    val userId: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USER_ID_KEY]
    }

    val userName: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USER_NAME_KEY]
    }

    val ecoCoins: Flow<Double> = context.dataStore.data.map { prefs ->
        prefs[ECO_COINS_KEY] ?: 0.0
    }

    suspend fun updateEcoCoins(newAmount: Double) {
        context.dataStore.edit { prefs ->
            prefs[ECO_COINS_KEY] = newAmount
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}