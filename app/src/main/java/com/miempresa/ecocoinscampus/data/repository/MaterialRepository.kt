package com.miempresa.ecocoinscampus.data.repository

import com.miempresa.ecocoinscampus.data.local.UserPreferences
import com.miempresa.ecocoinscampus.data.model.*
import com.miempresa.ecocoinscampus.data.remote.DjangoApiService
import com.miempresa.ecocoinscampus.utils.Result
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class MaterialRepository @Inject constructor(
    private val djangoApi: DjangoApiService,
    private val userPreferences: UserPreferences
) {

    /**
     * Obtener todos los materiales reciclados por el usuario actual
     */
    suspend fun getUserMaterials(): Result<List<Material>> {
        return try {
            val token = userPreferences.authToken.first() ?: ""
            val userId = userPreferences.userId.first() ?: ""

            if (token.isEmpty()) {
                return Result.Error("No hay sesión activa")
            }

            val response = djangoApi.getMaterialesByUser(userId, "Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Error al obtener materiales: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error de conexión: ${e.localizedMessage}", e)
        }
    }

    /**
     * Registrar un nuevo material reciclado
     */
    suspend fun registerMaterial(
        tipo: String,
        cantidad: Double,
        puntoRecoleccion: String
    ): Result<Material> {
        return try {
            val token = userPreferences.authToken.first() ?: ""
            val userId = userPreferences.userId.first() ?: ""

            if (token.isEmpty()) {
                return Result.Error("No hay sesión activa")
            }

            val request = RegisterMaterialRequest(
                tipo = tipo,
                cantidad = cantidad,
                usuario_id = userId,
                punto_recoleccion = puntoRecoleccion
            )

            val response = djangoApi.registerMaterial("Bearer $token", request)

            if (response.isSuccessful && response.body() != null) {
                val material = response.body()!!.material

                // Actualizar ecoCoins localmente
                val currentCoins = userPreferences.ecoCoins.first()
                userPreferences.updateEcoCoins(currentCoins + material.ecocoinsGeneradas)

                Result.Success(material)
            } else {
                Result.Error("Error al registrar material: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error de conexión: ${e.localizedMessage}", e)
        }
    }

    /**
     * Tipos de materiales disponibles (hardcoded por ahora)
     */
    fun getTiposMateriales(): List<String> = listOf(
        "Plástico",
        "Papel",
        "Vidrio",
        "Metal",
        "Cartón",
        "Electrónico"
    )
}