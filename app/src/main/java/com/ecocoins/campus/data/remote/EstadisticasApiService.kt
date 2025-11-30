package com.ecocoins.campus.data.remote

import com.ecocoins.campus.data.model.ApiResponse
import com.ecocoins.campus.data.model.EstadisticasDetalladas
import retrofit2.http.GET
import retrofit2.http.Path

interface EstadisticasApiService {

    @GET("api/estadisticas/detalladas/{usuarioId}")
    suspend fun obtenerEstadisticasCompletas(
        @Path("usuarioId") usuarioId: String
    ): ApiResponse<EstadisticasDetalladas>

    @GET("api/estadisticas/detalladas/{usuarioId}/resumen")
    suspend fun obtenerResumen(
        @Path("usuarioId") usuarioId: String
    ): ApiResponse<Map<String, Any>>

    @GET("api/estadisticas/detalladas/{usuarioId}/materiales")
    suspend fun obtenerDistribucionMateriales(
        @Path("usuarioId") usuarioId: String
    ): ApiResponse<List<Map<String, Any>>>

    @GET("api/estadisticas/detalladas/{usuarioId}/impacto")
    suspend fun obtenerImpactoAmbiental(
        @Path("usuarioId") usuarioId: String
    ): ApiResponse<Map<String, Any>>
}