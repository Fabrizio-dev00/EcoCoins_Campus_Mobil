package com.miempresa.ecocoinscampus.data.repository

import com.miempresa.ecocoinscampus.data.local.UserPreferences
import com.miempresa.ecocoinscampus.data.model.*
import com.miempresa.ecocoinscampus.data.remote.ApiService
import com.miempresa.ecocoinscampus.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {

    /**
     * Iniciar sesión
     */
    suspend fun login(correo: String, contrasenia: String): Result<LoginResponse> {
        return try {
            // 🔍 LOG PARA DEBUG
            android.util.Log.d("AuthRepository", "Intentando login con: $correo")

            val response = apiService.login(
                LoginRequest(
                    correo = correo.trim().lowercase(),
                    contrasenia = contrasenia
                )
            )

            // 🔍 LOG PARA DEBUG
            android.util.Log.d("AuthRepository", "Response code: ${response.code()}")
            android.util.Log.d("AuthRepository", "Response body: ${response.body()}")

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success && apiResponse.data != null) {
                    val loginData = apiResponse.data

                    // Guardar sesión en DataStore
                    userPreferences.saveUserSession(
                        token = loginData.token,
                        userId = loginData.id,
                        userName = loginData.nombre,
                        email = loginData.correo,
                        ecoCoins = loginData.ecoCoins.toDouble()
                    )

                    Result.Success(loginData)
                } else {
                    Result.Error(apiResponse.message ?: "Error al iniciar sesión")
                }
            } else {
                Result.Error("Error: ${response.code()} - ${response.message()}")
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
        correo: String,
        contrasenia: String,
        carrera: String,
        telefono: String? = null
    ): Result<LoginResponse> {
        return try {
            val response = apiService.register(
                RegisterRequest(
                    nombre = nombre.trim(),
                    correo = correo.trim().lowercase(),
                    contrasenia = contrasenia,
                    carrera = carrera.trim(),
                    telefono = telefono?.trim()
                )
            )

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success && apiResponse.data != null) {
                    val loginData = apiResponse.data

                    // Guardar sesión automáticamente
                    userPreferences.saveUserSession(
                        token = loginData.token,
                        userId = loginData.id,
                        userName = loginData.nombre,
                        email = loginData.correo,
                        ecoCoins = loginData.ecoCoins.toDouble()
                    )

                    Result.Success(loginData)
                } else {
                    Result.Error(apiResponse.message ?: "Error al registrarse")
                }
            } else {
                Result.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error de conexión: ${e.localizedMessage}", e)
        }
    }

    /**
     * Obtener perfil actualizado del usuario
     */
    suspend fun getUserProfile(): Result<User> {
        return try {
            val token = userPreferences.authToken.first() ?: ""
            val userId = userPreferences.userId.first() ?: ""

            if (token.isEmpty() || userId.isEmpty()) {
                return Result.Error("No hay sesión activa")
            }

            val response = apiService.getUsuarioById(userId, "Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success && apiResponse.data != null) {
                    val user = apiResponse.data

                    // Actualizar EcoCoins localmente
                    userPreferences.updateEcoCoins(user.ecoCoins.toDouble())

                    Result.Success(user)
                } else {
                    Result.Error(apiResponse.message ?: "Error al obtener perfil")
                }
            } else {
                Result.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error de conexión: ${e.localizedMessage}", e)
        }
    }

    /**
     * Validar token
     */
    suspend fun validateToken(token: String): Result<Boolean> {
        return try {
            val response = apiService.validateToken(token)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success && apiResponse.data == true) {
                    Result.Success(true)
                } else {
                    Result.Success(false)
                }
            } else {
                Result.Success(false)
            }
        } catch (e: Exception) {
            Result.Success(false)
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