package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.model.CodigoReferidoResponse
import com.ecocoins.campus.data.model.ReferidosInfo
import com.ecocoins.campus.data.remote.ApiService
import com.ecocoins.campus.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReferidosRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getReferidosInfo(usuarioId: Long): Flow<Resource<ReferidosInfo>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getReferidosInfo(usuarioId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener referidos: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun generarCodigoReferido(usuarioId: Long): Flow<Resource<CodigoReferidoResponse>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.generarCodigoReferido(usuarioId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al generar código: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun usarCodigoReferido(usuarioId: Long, codigo: String): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.usarCodigoReferido(usuarioId, codigo)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!.mensaje))
            } else {
                emit(Resource.Error("Error al usar código: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }
}