package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.*
import com.ecocoins.campus.data.remote.ApiService
import com.ecocoins.campus.utils.Result
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ReciclajeRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {

    suspend fun getUserReciclajes(): Result<List<Reciclaje>> {
        return try {
            val token = userPreferences.authToken.first() ?: ""
            val userId = userPreferences.userId.first() ?: ""

            if (token.isEmpty()) {
                return Result.Error("No hay sesión activa")
            }

            val response = apiService.getReciclajesByUsuario(userId, "Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success && apiResponse.data != null) {
                    Result.Success(apiResponse.data)
                } else {
                    Result.Error(apiResponse.message ?: "Error al obtener reciclajes")
                }
            } else {
                Result.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error de conexión: ${e.localizedMessage}", e)
        }
    }

    suspend fun registrarReciclaje(
        tipoMaterial: String,
        pesoKg: Double,
        puntoRecoleccion: String
    ): Result<Reciclaje> {
        return try {
            val token = userPreferences.authToken.first() ?: ""
            val userId = userPreferences.userId.first() ?: ""

            if (token.isEmpty()) {
                return Result.Error("No hay sesión activa")
            }

            val request = ReciclajeRequest(
                usuarioId = userId,
                tipoMaterial = tipoMaterial,
                pesoKg = pesoKg,
                puntoRecoleccion = puntoRecoleccion
            )

            val response = apiService.registrarReciclaje("Bearer $token", request)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success && apiResponse.data != null) {
                    val reciclaje = apiResponse.data

                    val currentCoins = userPreferences.ecoCoins.first()
                    userPreferences.updateEcoCoins(currentCoins + reciclaje.ecoCoinsGanadas)

                    Result.Success(reciclaje)
                } else {
                    Result.Error(apiResponse.message ?: "Error al registrar reciclaje")
                }
            } else {
                Result.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error de conexión: ${e.localizedMessage}", e)
        }
    }

    fun getTiposMateriales(): List<String> = listOf(
        "Plástico",
        "Papel",
        "Vidrio",
        "Metal",
        "Cartón",
        "Electrónico",
        "Orgánico",
        "Pilas"
    )
}