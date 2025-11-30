package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.model.Notificacion
import com.ecocoins.campus.data.model.Resource
import com.ecocoins.campus.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificacionesRepository {

    private val notificacionesService = RetrofitClient.notificacionesService

    suspend fun obtenerNotificaciones(usuarioId: String): Resource<List<Notificacion>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = notificacionesService.obtenerNotificaciones(usuarioId)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al obtener notificaciones")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun obtenerNoLeidas(usuarioId: String): Resource<List<Notificacion>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = notificacionesService.obtenerNoLeidas(usuarioId)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun contarNoLeidas(usuarioId: String): Resource<Int> {
        return withContext(Dispatchers.IO) {
            try {
                val response = notificacionesService.contarNoLeidas(usuarioId)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun marcarComoLeida(notificacionId: String): Resource<Notificacion> {
        return withContext(Dispatchers.IO) {
            try {
                val response = notificacionesService.marcarComoLeida(notificacionId)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun marcarTodasComoLeidas(usuarioId: String): Resource<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = notificacionesService.marcarTodasComoLeidas(usuarioId)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }
}