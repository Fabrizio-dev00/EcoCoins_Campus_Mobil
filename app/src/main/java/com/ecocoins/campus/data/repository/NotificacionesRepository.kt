package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.model.Notificacion
import com.ecocoins.campus.data.remote.ApiService
import com.ecocoins.campus.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NotificacionesRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getNotificaciones(usuarioId: Long): Flow<Resource<List<Notificacion>>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getNotificaciones(usuarioId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener notificaciones: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun getNotificacionesNoLeidas(usuarioId: Long): Flow<Resource<List<Notificacion>>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getNotificacionesNoLeidas(usuarioId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener notificaciones: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun marcarNotificacionLeida(notificacionId: Long): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.marcarNotificacionLeida(notificacionId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!.mensaje))
            } else {
                emit(Resource.Error("Error al marcar notificación: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun marcarTodasLeidas(usuarioId: Long): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.marcarTodasLeidas(usuarioId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!.mensaje))
            } else {
                emit(Resource.Error("Error al marcar notificaciones: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }
}