package com.miempresa.ecocoinscampus.data.repository

import com.miempresa.ecocoinscampus.data.local.UserPreferences
import com.miempresa.ecocoinscampus.data.model.*
import com.miempresa.ecocoinscampus.data.remote.DjangoApiService
import com.miempresa.ecocoinscampus.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val djangoApi: DjangoApiService,
    private val userPreferences: UserPreferences
) {

    /**
     * Iniciar sesión con email y contraseña
     */
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = djangoApi.login(LoginRequest(email, password))

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                val user = authResponse.usuario
                val token = authResponse.token ?: ""

                // Guardar sesión en DataStore
                userPreferences.saveUserSession(
                    token = token,
                    userId = user.id,
                    userName = user.nombre,
                    email = user.email,
                    ecoCoins = user.ecoCoins
                )

                Result.Success(user)
            } else {
                Result.Error("Error al iniciar sesión: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error de conexión: ${e.localizedMessage}", e)
        }
    }

    /**
     * Registrar nuevo usuario
     */
    suspend fun register(
        nombre: String,
        email: String,
        password: String,
        carrera: String
    ): Result<User> {
        return try {
            val request = RegisterRequest(
                nombre = nombre,
                email = email,
                password = password,
                carrera = carrera
            )

            val response = djangoApi.register(request)

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                val user = authResponse.usuario
                val token = authResponse.token ?: ""

                // Guardar sesión automáticamente
                userPreferences.saveUserSession(
                    token = token,
                    userId = user.id,
                    userName = user.nombre,
                    email = user.email,
                    ecoCoins = user.ecoCoins
                )

                Result.Success(user)
            } else {
                Result.Error("Error al registrarse: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error de conexión: ${e.localizedMessage}", e)
        }
    }

    /**
     * Obtener perfil de usuario actualizado
     */
    suspend fun getUserProfile(): Result<User> {
        return try {
            val token = userPreferences.authToken.first() ?: ""
            val userId = userPreferences.userId.first() ?: ""

            if (token.isEmpty() || userId.isEmpty()) {
                return Result.Error("No hay sesión activa")
            }

            val response = djangoApi.getUserById(userId, "Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!

                // Actualizar ecoCoins en local
                userPreferences.updateEcoCoins(user.ecoCoins)

                Result.Success(user)
            } else {
                Result.Error("Error al obtener perfil: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error de conexión: ${e.localizedMessage}", e)
        }
    }

    /**
     * Cerrar sesión
     */
    suspend fun logout() {
        userPreferences.clearSession()
    }

    /**
     * Verificar si hay sesión activa
     */
    fun isLoggedIn(): Flow<Boolean> = userPreferences.isLoggedIn

    /**
     * Obtener token guardado
     */
    suspend fun getToken(): String? = userPreferences.authToken.first()
}