package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.model.PosicionUsuario
import com.ecocoins.campus.data.model.RankingItem
import com.ecocoins.campus.data.remote.ApiService
import com.ecocoins.campus.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RankingRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getRankingGlobal(page: Int = 0, size: Int = 50): Flow<Resource<List<RankingItem>>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getRankingGlobal(page, size)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener ranking: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun getRankingSemanal(page: Int = 0, size: Int = 50): Flow<Resource<List<RankingItem>>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getRankingSemanal(page, size)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener ranking semanal: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun getRankingMensual(page: Int = 0, size: Int = 50): Flow<Resource<List<RankingItem>>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getRankingMensual(page, size)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener ranking mensual: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun getPosicionUsuario(usuarioId: Long): Flow<Resource<PosicionUsuario>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getPosicionUsuario(usuarioId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener posición: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }
}