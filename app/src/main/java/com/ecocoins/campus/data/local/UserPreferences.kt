package com.ecocoins.campus.data.local

import android.content.Context
import android.content.SharedPreferences
import com.ecocoins.campus.data.model.User
import com.google.gson.Gson

class UserPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("ecocoins_prefs", Context.MODE_PRIVATE)

    private val gson = Gson()

    companion object {
        private const val KEY_USER = "user"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_FIREBASE_UID = "firebase_uid"
    }

    fun saveUser(user: User) {
        prefs.edit().apply {
            putString(KEY_USER, gson.toJson(user))
            putString(KEY_USER_ID, user.id)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getUser(): User? {
        val userJson = prefs.getString(KEY_USER, null) ?: return null
        return try {
            gson.fromJson(userJson, User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    fun saveFirebaseUid(uid: String) {
        prefs.edit().putString(KEY_FIREBASE_UID, uid).apply()
    }

    fun getFirebaseUid(): String? {
        return prefs.getString(KEY_FIREBASE_UID, null)
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun clearUser() {
        prefs.edit().apply {
            remove(KEY_USER)
            remove(KEY_USER_ID)
            remove(KEY_FIREBASE_UID)
            putBoolean(KEY_IS_LOGGED_IN, false)
            apply()
        }
    }
}
