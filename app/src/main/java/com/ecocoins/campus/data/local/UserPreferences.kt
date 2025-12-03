package com.ecocoins.campus.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_TOKEN = stringPreferencesKey("user_token")
        val USER_ECO_COINS = longPreferencesKey("user_eco_coins")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val FIREBASE_UID = stringPreferencesKey("firebase_uid")
    }

    /**
     * Guardar datos del usuario
     */
    suspend fun saveUserData(
        userId: String,
        name: String,
        email: String,
        token: String? = null,
        ecoCoins: Int = 0,
        firebaseUid: String? = null
    ) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = userId
            preferences[USER_NAME] = name
            preferences[USER_EMAIL] = email
            token?.let { preferences[USER_TOKEN] = it }
            preferences[USER_ECO_COINS] = ecoCoins.toLong()
            preferences[IS_LOGGED_IN] = true
            firebaseUid?.let { preferences[FIREBASE_UID] = it }
        }
    }

    /**
     * Actualizar EcoCoins
     */
    suspend fun updateEcoCoins(ecoCoins: Long) {
        dataStore.edit { preferences ->
            preferences[USER_ECO_COINS] = ecoCoins
        }
    }

    /**
     * Limpiar datos del usuario (logout)
     */
    suspend fun clearUserData() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * Flows para observar datos
     */
    val userId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_ID]
    }

    val userName: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_NAME]
    }

    val userEmail: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_EMAIL]
    }

    val userToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_TOKEN]
    }

    val userEcoCoins: Flow<Long> = dataStore.data.map { preferences ->
        preferences[USER_ECO_COINS] ?: 0L
    }

    val isLoggedIn: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] == true
    }

    val firebaseUid: Flow<String?> = dataStore.data.map { preferences ->
        preferences[FIREBASE_UID]
    }
}
