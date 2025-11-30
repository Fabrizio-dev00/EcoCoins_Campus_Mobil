package com.ecocoins.campus.data.remote

import com.ecocoins.campus.data.model.ApiResponse
import com.ecocoins.campus.data.model.Logro
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface LogrosApiService {

    @GET("api/logros")
    suspend fun obtenerTodosLosLogros(): ApiResponse<List<Logro>>

    @GET("api/logros/usuario/{usuarioId}")
    suspend fun obtenerLogrosUsuario(
        @Path("usuarioId") usuarioId: String
    ): ApiResponse<Map<String, Any>>

    @POST("api/logros/usuario/{usuarioId}/verificar")
    suspend fun verificarLogros(
        @Path("usuarioId") usuarioId: String
    ): ApiResponse<Map<String, Any>>
}