package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.User
import com.ecocoins.campus.data.remote.ApiService
import com.ecocoins.campus.utils.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val userPreferences: UserPreferences,
    private val apiService: ApiService  // ⭐ AGREGAR ESTE PARÁMETRO
) {

    suspend fun login(correo: String, contrasenia: String): Result<User> {
        val result = firebaseAuthRepository.loginConEmail(correo, contrasenia)

        return if (result.isSuccess) {
            val user = result.getOrNull()!!
            saveUserSession(user)
            Result.Success(user)
        } else {
            val error = result.exceptionOrNull()
            Result.Error(
                message = when {
                    error?.message?.contains("verify") == true ->
                        "Por favor verifica tu email antes de iniciar sesión"
                    error?.message?.contains("password is invalid") == true ||
                            error?.message?.contains("no user record") == true ->
                        "Email o contraseña incorrectos"
                    error?.message?.contains("network error") == true ->
                        "Error de conexión. Verifica tu internet"
                    else -> error?.message ?: "Error al iniciar sesión"
                }
            )
        }
    }

    suspend fun register(
        nombre: String,
        correo: String,
        contrasenia: String,
        carrera: String
    ): Result<User> {
        val result = firebaseAuthRepository.registrarConEmail(
            email = correo,
            password = contrasenia,
            nombre = nombre,
            carrera = carrera
        )

        return if (result.isSuccess) {
            val user = result.getOrNull()!!
            saveUserSession(user)
            Result.Success(user)
        } else {
            val error = result.exceptionOrNull()
            Result.Error(
                message = when {
                    error?.message?.contains("email address is already in use") == true ->
                        "Este email ya está registrado"
                    error?.message?.contains("network error") == true ->
                        "Error de conexión. Verifica tu internet"
                    error?.message?.contains("weak password") == true ->
                        "La contraseña es muy débil"
                    else -> error?.message ?: "Error al registrar usuario"
                }
            )
        }
    }

    suspend fun getUserProfile(): Result<User> {
        return try {
            // Obtener token de Firebase
            val token = firebaseAuthRepository.obtenerToken()
                ?: return Result.Error("No hay sesión activa")

            // Llamar al endpoint /api/auth/perfil del backend
            val response = apiService.obtenerPerfil(
                authHeader = "Bearer $token"
            )

            if (response.success && response.data != null) {
                val user = response.data

                // Actualizar datos en DataStore
                saveUserSession(user)

                Result.Success(user)
            } else {
                Result.Error(response.message ?: "Error al obtener perfil")
            }
        } catch (e: Exception) {
            Result.Error("Error: ${e.localizedMessage}", e)
        }
    }

    private suspend fun saveUserSession(user: User) {
        val token = firebaseAuthRepository.obtenerToken() ?: ""
        userPreferences.saveUserSession(
            token = token,
            userId = user.id,
            userName = user.nombre,
            email = user.correo,
            ecoCoins = user.ecoCoins.toDouble()
        )
    }

    suspend fun logout() {
        firebaseAuthRepository.cerrarSesion()
        userPreferences.clearSession()
    }

    fun isLoggedIn(): Flow<Boolean> = userPreferences.isLoggedIn

    suspend fun getToken(): String? = firebaseAuthRepository.obtenerToken()
}