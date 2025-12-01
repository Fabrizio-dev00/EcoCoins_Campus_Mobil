package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.User
import com.ecocoins.campus.data.remote.RetrofitClient
import com.ecocoins.campus.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val userPreferences: UserPreferences
) {
    private val apiService = RetrofitClient.apiService

    /**
     * Login con email y contraseña
     */
    suspend fun login(email: String, password: String): Result<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val credentials = mapOf(
                    "email" to email,
                    "password" to password
                )

                val response = apiService.login(credentials)

                if (response.success && response.data != null) {
                    Result.Success(response.data)
                } else {
                    Result.Error(response.message ?: "Error en el login")
                }
            } catch (e: Exception) {
                Result.Error(
                    message = e.message ?: "Error de conexión",
                    exception = e
                )
            }
        }
    }

    /**
     * Registro de nuevo usuario
     */
    suspend fun register(
        nombre: String,
        email: String,
        password: String
    ): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val userData = mapOf(
                    "nombre" to nombre,
                    "email" to email,
                    "password" to password
                )

                val response = apiService.register(userData)

                if (response.success && response.data != null) {
                    Result.Success(response.data)
                } else {
                    Result.Error(response.message ?: "Error en el registro")
                }
            } catch (e: Exception) {
                Result.Error(
                    message = e.message ?: "Error de conexión",
                    exception = e
                )
            }
        }
    }

    /**
     * Obtener perfil del usuario actual
     */
    suspend fun getUserProfile(): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                // Obtener usuario desde UserPreferences
                val user = userPreferences.getUser()

                if (user != null) {
                    // Si existe en preferencias, retornarlo
                    Result.Success(user)
                } else {
                    // Si no existe, obtener desde API
                    val userId = userPreferences.getUserId()

                    if (userId.isNullOrEmpty()) {
                        return@withContext Result.Error("Usuario no autenticado")
                    }

                    // Aquí deberías hacer una llamada al API para obtener el usuario
                    // Por ahora, retornamos error si no está en preferencias
                    Result.Error("No se pudo obtener el perfil del usuario")
                }
            } catch (e: Exception) {
                Result.Error(
                    message = e.message ?: "Error al obtener perfil",
                    exception = e
                )
            }
        }
    }

    /**
     * Cerrar sesión
     */
    suspend fun logout(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Limpiar datos locales
                userPreferences.clearUser()

                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(
                    message = e.message ?: "Error al cerrar sesión",
                    exception = e
                )
            }
        }
    }
}