package com.miempresa.ecocoinscampus.data.repository

import com.miempresa.ecocoinscampus.data.local.UserPreferences
import com.miempresa.ecocoinscampus.data.model.*
import com.miempresa.ecocoinscampus.data.remote.DjangoApiService
import com.miempresa.ecocoinscampus.utils.Result
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class RecompensasRepository @Inject constructor(
    private val djangoApi: DjangoApiService,
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

            val response = djangoApi.getRecompensas("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Error al obtener recompensas: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error de conexión: ${e.localizedMessage}", e)
        }
    }

    /**
     * Canjear una recompensa
     */
    suspend fun canjearRecompensa(recompensaId: String): Result<User> {
        return try {
            val token = userPreferences.authToken.first() ?: ""
            val userId = userPreferences.userId.first() ?: ""

            if (token.isEmpty()) {
                return Result.Error("No hay sesión activa")
            }

            val request = CanjearRecompensaRequest(
                usuario_id = userId,
                recompensa_id = recompensaId
            )

            val response = djangoApi.canjearRecompensa("Bearer $token", request)

            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!.usuario

                // Actualizar ecoCoins localmente
                userPreferences.updateEcoCoins(user.ecoCoins)

                Result.Success(user)
            } else {
                Result.Error("Error al canjear recompensa: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error de conexión: ${e.localizedMessage}", e)
        }
    }
}