package com.miempresa.ecocoinscampus.data.repository

import com.miempresa.ecocoinscampus.data.local.UserPreferences
import com.miempresa.ecocoinscampus.data.model.*
import com.miempresa.ecocoinscampus.data.remote.SpringApiService
import com.miempresa.ecocoinscampus.utils.Result
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class EstadisticasRepository @Inject constructor(
    private val springApi: SpringApiService,
    private val userPreferences: UserPreferences
) {

    /**
     * Obtener estadísticas generales del campus
     */
    suspend fun getEstadisticasGenerales(): Result<EstadisticasGenerales> {
        return try {
            val token = userPreferences.authToken.first() ?: ""

            if (token.isEmpty()) {
                return Result.Error("No hay sesión activa")
            }

            val response = springApi.getEstadisticasGenerales("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Error al obtener estadísticas: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error de conexión: ${e.localizedMessage}", e)
        }
    }

    /**
     * Obtener ranking de usuarios con más ecoCoins
     */
    suspend fun getRankingUsuarios(limit: Int = 10): Result<List<UserRanking>> {
        return try {
            val token = userPreferences.authToken.first() ?: ""

            if (token.isEmpty()) {
                return Result.Error("No hay sesión activa")
            }

            val response = springApi.getRankingUsuarios("Bearer $token", limit)

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Error al obtener ranking: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error de conexión: ${e.localizedMessage}", e)
        }
    }

    /**
     * Obtener materiales más reciclados
     */
    suspend fun getMaterialesMasReciclados(): Result<List<MaterialStat>> {
        return try {
            val token = userPreferences.authToken.first() ?: ""

            if (token.isEmpty()) {
                return Result.Error("No hay sesión activa")
            }

            val response = springApi.getMaterialesMasReciclados("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Error al obtener materiales: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error de conexión: ${e.localizedMessage}", e)
        }
    }
}