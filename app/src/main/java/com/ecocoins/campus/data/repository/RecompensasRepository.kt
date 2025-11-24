package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.*
import com.ecocoins.campus.data.remote.ApiService
import com.ecocoins.campus.utils.Result
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class RecompensasRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {

    suspend fun getRecompensas(): Result<List<Recompensa>> {
        return try {
            val token = userPreferences.authToken.first() ?: ""

            if (token.isEmpty()) {
                return Result.Error("No hay sesión activa")
            }

            val response = apiService.getRecompensas("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success && apiResponse.data != null) {
                    Result.Success(apiResponse.data)
                } else {
                    Result.Error(apiResponse.message ?: "Error al obtener recompensas")
                }
            } else {
                Result.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error de conexión: ${e.localizedMessage}", e)
        }
    }

    suspend fun canjearRecompensa(
        recompensaId: String,
        direccionEntrega: String? = null,
        telefonoContacto: String? = null,
        observaciones: String? = null
    ): Result<CanjeResponse> {
        return try {
            val token = userPreferences.authToken.first() ?: ""
            val userId = userPreferences.userId.first() ?: ""

            if (token.isEmpty()) {
                return Result.Error("No hay sesión activa")
            }

            val request = CanjeRequest(
                usuarioId = userId,
                recompensaId = recompensaId,
                direccionEntrega = direccionEntrega,
                telefonoContacto = telefonoContacto,
                observaciones = observaciones
            )

            val response = apiService.canjearRecompensa("Bearer $token", request)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success && apiResponse.data != null) {
                    val canjeResponse = apiResponse.data
                    userPreferences.updateEcoCoins(canjeResponse.nuevoBalance.toDouble())
                    Result.Success(canjeResponse)
                } else {
                    Result.Error(apiResponse.message ?: "Error al canjear recompensa")
                }
            } else {
                Result.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error de conexión: ${e.localizedMessage}", e)
        }
    }

    suspend fun getMisCanjes(): Result<List<Canje>> {
        return try {
            val token = userPreferences.authToken.first() ?: ""
            val userId = userPreferences.userId.first() ?: ""

            if (token.isEmpty()) {
                return Result.Error("No hay sesión activa")
            }

            val response = apiService.getCanjesByUsuario(userId, "Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success && apiResponse.data != null) {
                    Result.Success(apiResponse.data)
                } else {
                    Result.Error(apiResponse.message ?: "Error al obtener canjes")
                }
            } else {
                Result.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error de conexión: ${e.localizedMessage}", e)
        }
    }
}