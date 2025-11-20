package com.miempresa.ecocoinscampus.data.repository

import com.miempresa.ecocoinscampus.data.local.UserPreferences
import com.miempresa.ecocoinscampus.data.model.*
import com.miempresa.ecocoinscampus.data.remote.ApiService
import com.miempresa.ecocoinscampus.utils.Result
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class RecompensasRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {

    /**
     * Obtener todas las recompensas disponibles
     */
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

    /**
     * Canjear una recompensa
     */
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

                    // Actualizar EcoCoins localmente
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

    /**
     * Obtener canjes del usuario
     */
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