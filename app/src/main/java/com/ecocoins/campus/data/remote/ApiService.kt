package com.ecocoins.campus.data.remote

import androidx.camera.core.ImageProcessor.Response
import com.ecocoins.campus.data.model.ApiResponse
import com.ecocoins.campus.data.model.Canje
import com.ecocoins.campus.data.model.Reciclaje
import com.ecocoins.campus.data.model.Recompensa
import com.ecocoins.campus.data.model.User
import com.ecocoins.campus.data.model.ValidarIARequest
import com.ecocoins.campus.data.model.ValidarIAResponse
import retrofit2.http.*

interface ApiService {

    // ========== AUTH ==========

    @POST("api/auth/login")
    suspend fun login(
        @Body credentials: Map<String, String>
    ): ApiResponse<Map<String, Any>>

    @POST("api/auth/register")
    suspend fun register(
        @Body userData: Map<String, String>
    ): ApiResponse<User>

    // ========== USUARIOS ==========

    @GET("api/usuarios/{id}")
    suspend fun obtenerUsuario(
        @Path("id") usuarioId: String
    ): ApiResponse<User>

    @PUT("api/usuarios/{id}")
    suspend fun actualizarUsuario(
        @Path("id") usuarioId: String,
        @Body userData: Map<String, Any>
    ): ApiResponse<User>

    // ========== RECICLAJES ==========

    @POST("api/reciclajes")
    suspend fun registrarReciclaje(
        @Body reciclajeData: Map<String, Any>
    ): ApiResponse<Reciclaje>

    @GET("api/reciclajes/usuario/{usuarioId}")
    suspend fun obtenerReciclajesUsuario(
        @Path("usuarioId") usuarioId: String
    ): ApiResponse<List<Reciclaje>>

    @POST("api/reciclajes/validar-ia")
    suspend fun validarConIA(
        @Body imageData: Map<String, String>
    ): ApiResponse<Map<String, Any>>

    // ========== RECOMPENSAS ==========

    @GET("api/recompensas")
    suspend fun obtenerRecompensas(): ApiResponse<List<Recompensa>>

    @GET("api/recompensas/{id}")
    suspend fun obtenerRecompensa(
        @Path("id") recompensaId: String
    ): ApiResponse<Recompensa>

    // ========== CANJES ==========

    @POST("api/canjes")
    suspend fun canjearRecompensa(
        @Body canjeData: Map<String, String>
    ): ApiResponse<Canje>

    @GET("api/canjes/usuario/{usuarioId}")
    suspend fun obtenerCanjesUsuario(
        @Path("usuarioId") usuarioId: String,
        @Query("estado") estado: String? = null
    ): ApiResponse<List<Canje>>

    @GET("api/canjes/{id}")
    suspend fun obtenerCanje(
        @Path("id") canjeId: String
    ): ApiResponse<Canje>


    @POST("api/validar-material-ia")
    suspend fun validarMaterialConIA(
        @Header("Authorization") token: String,
        @Body request: ValidarIARequest
    ): Response<ApiResponse<ValidarIAResponse>>
}
