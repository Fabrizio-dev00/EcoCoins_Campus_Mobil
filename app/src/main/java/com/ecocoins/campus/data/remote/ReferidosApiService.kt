package com.ecocoins.campus.data.remote

import com.ecocoins.campus.data.model.ApiResponse
import com.ecocoins.campus.data.model.CodigoReferidoResponse
import com.ecocoins.campus.data.model.ReferidosInfo
import retrofit2.http.*

interface ReferidosApiService {

    @GET("api/referidos/usuario/{usuarioId}")
    suspend fun obtenerReferidos(
        @Path("usuarioId") usuarioId: String
    ): ApiResponse<ReferidosInfo>

    @POST("api/referidos/generar-codigo")
    suspend fun generarCodigo(
        @Body userData: Map<String, String>
    ): ApiResponse<CodigoReferidoResponse>

    @POST("api/referidos/registrar")
    suspend fun registrarReferido(
        @Body referidoData: Map<String, String>
    ): ApiResponse<Map<String, Any>>

    @GET("api/referidos/validar/{codigo}")
    suspend fun validarCodigo(
        @Path("codigo") codigo: String
    ): ApiResponse<Map<String, Any>>
}