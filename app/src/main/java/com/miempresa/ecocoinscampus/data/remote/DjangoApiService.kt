package com.miempresa.ecocoinscampus.data.remote

import com.miempresa.ecocoinscampus.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface DjangoApiService {

    // ===== AUTH =====
    @POST("api/usuarios/login/")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/usuarios/registrar/")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("api/usuarios/listar/")
    suspend fun getUsers(
        @Header("Authorization") token: String
    ): Response<List<User>>

    @GET("api/usuarios/{id}/")
    suspend fun getUserById(
        @Path("id") userId: String,
        @Header("Authorization") token: String
    ): Response<User>

    // ===== MATERIALES =====
    @GET("api/panel/materiales/")
    suspend fun getMateriales(
        @Header("Authorization") token: String
    ): Response<List<Material>>

    @GET("api/panel/materiales/usuario/{userId}/")
    suspend fun getMaterialesByUser(
        @Path("userId") userId: String,
        @Header("Authorization") token: String
    ): Response<List<Material>>

    @POST("api/panel/materiales/registrar/")
    suspend fun registerMaterial(
        @Header("Authorization") token: String,
        @Body request: RegisterMaterialRequest
    ): Response<MaterialResponse>

    // ===== RECOMPENSAS =====
    @GET("api/recompensas/")
    suspend fun getRecompensas(
        @Header("Authorization") token: String
    ): Response<List<Recompensa>>

    @POST("api/recompensas/canjear/")
    suspend fun canjearRecompensa(
        @Header("Authorization") token: String,
        @Body request: CanjearRecompensaRequest
    ): Response<AuthResponse>
}