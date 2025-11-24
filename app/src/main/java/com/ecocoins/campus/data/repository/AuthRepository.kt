package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.*
import com.ecocoins.campus.data.remote.ApiService
import com.ecocoins.campus.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {

    suspend fun login(correo: String, contrasenia: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(
                LoginRequest(
                    correo = correo.trim().lowercase(),
                    contrasenia = contrasenia
                )
            )

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success && apiResponse.data != null) {
                    val loginData = apiResponse.data

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

    suspend fun logout() {
        userPreferences.clearSession()
    }

    fun isLoggedIn(): Flow<Boolean> = userPreferences.isLoggedIn

    suspend fun getToken(): String? = userPreferences.authToken.first()
}