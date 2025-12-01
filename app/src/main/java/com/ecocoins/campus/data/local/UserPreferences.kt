package com.ecocoins.campus.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.ecocoins.campus.data.model.User
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Extension para crear DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ecocoins_prefs")

class UserPreferences(private val context: Context) {

    private val dataStore = context.dataStore
    private val gson = Gson()

    companion object {
        private val KEY_USER = stringPreferencesKey("user")
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val KEY_FIREBASE_UID = stringPreferencesKey("firebase_uid")
    }

    // ===== FLOWS (REACTIVOS) =====

    val userId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[KEY_USER_ID]
    }

    val firebaseUid: Flow<String?> = dataStore.data.map { preferences ->
        preferences[KEY_FIREBASE_UID]
    }

    val isLoggedIn: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_IS_LOGGED_IN] ?: false
    }

    val user: Flow<User?> = dataStore.data.map { preferences ->
        val userJson = preferences[KEY_USER]
        userJson?.let {
            try {
                gson.fromJson(it, User::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

    // ===== MÉTODOS SUSPENDIDOS =====

    suspend fun saveUser(user: User) {
        dataStore.edit { preferences ->
            preferences[KEY_USER] = gson.toJson(user)
            preferences[KEY_USER_ID] = user.id
            preferences[KEY_IS_LOGGED_IN] = true
        }
    }

    suspend fun saveFirebaseUid(uid: String) {
        dataStore.edit { preferences ->
            preferences[KEY_FIREBASE_UID] = uid
        }
    }

    suspend fun clearUser() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_USER)
            preferences.remove(KEY_USER_ID)
            preferences.remove(KEY_FIREBASE_UID)
            preferences[KEY_IS_LOGGED_IN] = false
        }
    }

    // ===== MÉTODOS SÍNCRONOS (para compatibilidad temporal) =====

    suspend fun getUserId(): String? {
        return userId.first()
    }

    suspend fun getUser(): User? {
        return user.first()
    }

    suspend fun isUserLoggedIn(): Boolean {
        return isLoggedIn.first()
    }

}