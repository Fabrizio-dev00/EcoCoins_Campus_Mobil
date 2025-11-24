package com.ecocoins.campus.data.remote

import com.ecocoins.campus.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // AUTH
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<LoginResponse>>

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<LoginResponse>>

    // USUARIOS
    @GET("api/usuarios/{id}")
    suspend fun getUsuarioById(
        @Path("id") userId: String,
        @Header("Authorization") token: String
    ): Response<ApiResponse<User>>

    // RECICLAJES
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

    // RECOMPENSAS
    @GET("api/recompensas")
    suspend fun getRecompensas(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<Recompensa>>>

    // CANJES
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
}