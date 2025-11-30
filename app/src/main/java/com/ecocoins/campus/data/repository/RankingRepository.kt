package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.model.PosicionUsuario
import com.ecocoins.campus.data.model.RankingItem
import com.ecocoins.campus.data.model.Resource
import com.ecocoins.campus.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RankingRepository {

    private val rankingService = RetrofitClient.rankingService

    suspend fun obtenerRanking(periodo: String): Resource<List<RankingItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = rankingService.obtenerRanking(periodo)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al obtener ranking")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun obtenerPosicionUsuario(usuarioId: String): Resource<PosicionUsuario> {
        return withContext(Dispatchers.IO) {
            try {
                val response = rankingService.obtenerPosicionUsuario(usuarioId)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al obtener posición")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun obtenerPodio(periodo: String): Resource<List<RankingItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = rankingService.obtenerPodio(periodo)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al obtener podio")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }
}