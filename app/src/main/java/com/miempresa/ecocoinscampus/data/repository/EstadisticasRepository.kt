package com.miempresa.ecocoinscampus.data.repository

import com.miempresa.ecocoinscampus.data.local.UserPreferences
import com.miempresa.ecocoinscampus.data.model.*
import com.miempresa.ecocoinscampus.data.remote.ApiService
import com.miempresa.ecocoinscampus.utils.Result
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class EstadisticasRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {

    /**
     * Obtener estadísticas generales
     */
    suspend fun getEstadisticas(): Result<Estadisticas> {
        return try {
            val token = userPreferences.authToken.first() ?: ""

            if (token.isEmpty()) {
                return Result.Error("No hay sesión activa")
            }

            val response = apiService.getEstadisticas("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success && apiResponse.data != null) {
                    Result.Success(apiResponse.data)
                } else {
                    Result.Error(apiResponse.message ?: "Error al obtener estadísticas")
                }
            } else {
                Result.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error de conexión: ${e.localizedMessage}", e)
        }
    }

    /**
     * Obtener estadísticas del usuario actual
     */
    suspend fun getEstadisticasUsuario(): Result<EstadisticasUsuario> {
        return try {
            val token = userPreferences.authToken.first() ?: ""
            val userId = userPreferences.userId.first() ?: ""

            if (token.isEmpty()) {
                return Result.Error("No hay sesión activa")
            }

            val response = apiService.getEstadisticasUsuario(userId, "Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success && apiResponse.data != null) {
                    Result.Success(apiResponse.data)
                } else {
                    Result.Error(apiResponse.message ?: "Error al obtener estadísticas del usuario")
                }
            } else {
                Result.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error de conexión: ${e.localizedMessage}", e)
        }
    }
}