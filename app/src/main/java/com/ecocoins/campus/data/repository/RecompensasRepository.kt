package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.model.Canje
import com.ecocoins.campus.data.model.CanjeRequest
import com.ecocoins.campus.data.model.Recompensa
import com.ecocoins.campus.data.remote.ApiService
import com.ecocoins.campus.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RecompensasRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getRecompensasDisponibles(): Flow<Resource<List<Recompensa>>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getRecompensasDisponibles()

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener recompensas: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun getRecompensaById(recompensaId: Long): Flow<Resource<Recompensa>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getRecompensaById(recompensaId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener recompensa: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun canjearRecompensa(
        usuarioId: String,
        recompensaId: Long
    ): Flow<Resource<Canje>> = flow {
        try {
            emit(Resource.Loading())

            val request = CanjeRequest(usuarioId, recompensaId.toString())
            val response = apiService.canjearRecompensa(request)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al canjear recompensa: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun getCanjesUsuario(usuarioId: Long): Flow<Resource<List<Canje>>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getCanjesUsuario(usuarioId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener canjes: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun getHistorialCanjes(
        usuarioId: String,
        page: Int = 0,
        size: Int = 20
    ): Flow<Resource<List<Canje>>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getHistorialCanjes(usuarioId, page, size)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener historial: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }
}