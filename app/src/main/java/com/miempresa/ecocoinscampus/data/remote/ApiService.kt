package com.miempresa.ecocoinscampus.data.remote

import com.miempresa.ecocoinscampus.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ===================================
    // 🔐 AUTENTICACIÓN
    // ===================================

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<LoginResponse>>

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<LoginResponse>>

    @GET("auth/validate")
    suspend fun validateToken(
        @Query("token") token: String
    ): Response<ApiResponse<Boolean>>

    // ===================================
    // 👥 USUARIOS
    // ===================================

    @GET("api/usuarios")
    suspend fun getUsuarios(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<User>>>

    @GET("api/usuarios/{id}")
    suspend fun getUsuarioById(
        @Path("id") userId: String,
        @Header("Authorization") token: String
    ): Response<ApiResponse<User>>

    // ===================================
    // ♻️ RECICLAJES
    // ===================================

    @GET("api/reciclajes")
    suspend fun getReciclajes(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<Reciclaje>>>

    @GET("api/reciclajes/usuario/{usuarioId}")
    suspend fun getReciclajesByUsuario(
        @Path("usuarioId") usuarioId: String,
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<Reciclaje>>>

    @POST("api/reciclajes")
    suspend fun registrarReciclaje(
        @Header("Authorization") token: String,
        @Body request: ReciclajeRequest
    ): Response<ApiResponse<Reciclaje>>

    // ===================================
    // 📷 QR CODE
    // ===================================

    @GET("api/qr/validar/{codigo}")
    suspend fun validarQr(
        @Path("codigo") codigo: String,
        @Header("Authorization") token: String
    ): Response<ApiResponse<ContenedorInfo>>

    @POST("api/qr/registrar")
    suspend fun registrarConQr(
        @Header("Authorization") token: String,
        @Body request: ReciclajeQrRequest
    ): Response<ApiResponse<ReciclajeQrResponse>>

    @GET("api/qr/tarifas")
    suspend fun getTarifas(
        @Header("Authorization") token: String
    ): Response<ApiResponse<Map<String, Int>>>

    // ===================================
    // 🎁 RECOMPENSAS
    // ===================================

    @GET("api/recompensas")
    suspend fun getRecompensas(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<Recompensa>>>

    @GET("api/recompensas/{id}")
    suspend fun getRecompensaById(
        @Path("id") recompensaId: String,
        @Header("Authorization") token: String
    ): Response<ApiResponse<Recompensa>>

    @POST("api/recompensas")
    suspend fun crearRecompensa(
        @Header("Authorization") token: String,
        @Body recompensa: Recompensa
    ): Response<ApiResponse<Recompensa>>

    // ===================================
    // 💳 CANJES
    // ===================================

    @POST("api/canjes/canjear")
    suspend fun canjearRecompensa(
        @Header("Authorization") token: String,
        @Body request: CanjeRequest
    ): Response<ApiResponse<CanjeResponse>>

    @GET("api/canjes/usuario/{usuarioId}")
    suspend fun getCanjesByUsuario(
        @Path("usuarioId") usuarioId: String,
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<Canje>>>

    @GET("api/canjes/{id}")
    suspend fun getCanjeById(
        @Path("id") canjeId: String,
        @Header("Authorization") token: String
    ): Response<ApiResponse<Canje>>

    // ===================================
    // 📊 ESTADÍSTICAS
    // ===================================

    @GET("api/estadisticas")
    suspend fun getEstadisticas(
        @Header("Authorization") token: String
    ): Response<ApiResponse<Estadisticas>>

    @GET("api/estadisticas/usuario/{usuarioId}")
    suspend fun getEstadisticasUsuario(
        @Path("usuarioId") usuarioId: String,
        @Header("Authorization") token: String
    ): Response<ApiResponse<EstadisticasUsuario>>

    // ===================================
    // 📦 CONTENEDORES
    // ===================================

    @GET("api/contenedores")
    suspend fun getContenedores(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<Contenedor>>>

    @GET("api/contenedores/activos")
    suspend fun getContenedoresActivos(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<Contenedor>>>
}