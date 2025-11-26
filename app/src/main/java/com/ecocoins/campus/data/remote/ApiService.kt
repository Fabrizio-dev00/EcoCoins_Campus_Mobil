package com.ecocoins.campus.data.remote

import com.ecocoins.campus.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ========================================
    // 🔥 ENDPOINTS DE AUTENTICACIÓN FIREBASE
    // ========================================

    /**
     * Sincroniza usuario de Firebase con MongoDB
     */
    @POST("api/auth/sync")
    suspend fun sincronizarUsuario(
        @Header("Authorization") authHeader: String,
        @Body request: Map<String, String>
    ): Response<ApiResponse<User>>  // ⭐ CAMBIO: Ahora retorna Response<>

    /**
     * Obtiene el perfil del usuario autenticado
     */
    @GET("api/auth/perfil")
    suspend fun obtenerPerfil(
        @Header("Authorization") authHeader: String
    ): ApiResponse<User>

    /**
     * Health check del servicio
     */
    @GET("api/auth/health")
    suspend fun healthCheck(): ApiResponse<String>

    // ========================================
    // ENDPOINTS DE RECICLAJES
    // ========================================

    /**
     * Obtiene los reciclajes de un usuario
     */
    @GET("api/reciclajes/usuario/{usuarioId}")
    suspend fun getReciclajesByUsuario(
        @Path("usuarioId") usuarioId: String,
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<Reciclaje>>>

    /**
     * Registra un nuevo reciclaje
     */
    @POST("api/reciclajes")
    suspend fun registrarReciclaje(
        @Header("Authorization") token: String,
        @Body request: ReciclajeRequest
    ): Response<ApiResponse<Reciclaje>>

    // ========================================
    // ENDPOINTS DE RECOMPENSAS
    // ========================================

    /**
     * Obtiene todas las recompensas disponibles
     */
    @GET("api/recompensas")
    suspend fun getRecompensas(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<Recompensa>>>

    /**
     * Canjea una recompensa
     */
    @POST("api/canjes/canjear")
    suspend fun canjearRecompensa(
        @Header("Authorization") token: String,
        @Body request: CanjeRequest
    ): Response<ApiResponse<CanjeResponse>>

    /**
     * Obtiene los canjes de un usuario
     */
    @GET("api/canjes/usuario/{usuarioId}")
    suspend fun getCanjesByUsuario(
        @Path("usuarioId") usuarioId: String,
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<Canje>>>
}