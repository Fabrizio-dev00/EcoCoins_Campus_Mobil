package com.ecocoins.campus.data.remote

import com.ecocoins.campus.data.model.ApiResponse
import com.ecocoins.campus.data.model.Notificacion
import retrofit2.http.*

interface NotificacionesApiService {

    @GET("api/notificaciones/usuario/{usuarioId}")
    suspend fun obtenerNotificaciones(
        @Path("usuarioId") usuarioId: String
    ): ApiResponse<List<Notificacion>>

    @GET("api/notificaciones/usuario/{usuarioId}/no-leidas")
    suspend fun obtenerNoLeidas(
        @Path("usuarioId") usuarioId: String
    ): ApiResponse<List<Notificacion>>

    @GET("api/notificaciones/usuario/{usuarioId}/contar")
    suspend fun contarNoLeidas(
        @Path("usuarioId") usuarioId: String
    ): ApiResponse<Int>

    @PUT("api/notificaciones/{notificacionId}/leer")
    suspend fun marcarComoLeida(
        @Path("notificacionId") notificacionId: String
    ): ApiResponse<Notificacion>

    @PUT("api/notificaciones/usuario/{usuarioId}/leer-todas")
    suspend fun marcarTodasComoLeidas(
        @Path("usuarioId") usuarioId: String
    ): ApiResponse<String>
}