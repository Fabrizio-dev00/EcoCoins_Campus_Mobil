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
    ): Response<ApiResponse<User>>

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
    // ⭐ NUEVO: VALIDACIÓN CON IA GEMINI
    // ========================================

    /**
     * Valida un material con IA Gemini y registra el reciclaje
     */
    @POST("api/reciclajes/validar-ia")
    suspend fun validarMaterialConIA(
        @Header("Authorization") token: String,
        @Body request: ValidarIARequest
    ): Response<ApiResponse<ValidarIAResponse>>

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

    // ========================================
    // 🏪 ENDPOINTS DE PROFESORES Y TIENDA
    // ========================================

    /**
     * Obtiene todos los profesores activos con sus recompensas
     */
    @GET("api/profesores/activos")
    suspend fun getProfesoresActivos(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<Profesor>>>

    /**
     * Obtiene un profesor específico por ID
     */
    @GET("api/profesores/{profesorId}")
    suspend fun getProfesorById(
        @Path("profesorId") profesorId: String,
        @Header("Authorization") token: String
    ): Response<ApiResponse<Profesor>>

    /**
     * Canjea una recompensa de un profesor
     */
    @POST("api/profesores/canjear")
    suspend fun canjearRecompensaProfesor(
        @Header("Authorization") token: String,
        @Body request: CanjearRecompensaProfesorRequest
    ): Response<ApiResponse<CanjearRecompensaProfesorResponse>>

    /**
     * Obtiene el historial de canjes con profesores del usuario
     */
    @GET("api/profesores/historial/{usuarioId}")
    suspend fun getHistorialCanjesProfesores(
        @Path("usuarioId") usuarioId: String,
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<CanjearRecompensaProfesorResponse>>>
}