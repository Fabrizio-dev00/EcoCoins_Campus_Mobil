package com.ecocoins.campus.data.remote

import com.ecocoins.campus.data.model.*
import retrofit2.http.*

interface EducacionApiService {

    @GET("api/educacion/contenidos")
    suspend fun obtenerContenidos(
        @Query("categoria") categoria: String? = null,
        @Query("tipo") tipo: String? = null
    ): ApiResponse<List<ContenidoEducativo>>

    @GET("api/educacion/contenidos/{id}")
    suspend fun obtenerContenido(
        @Path("id") contenidoId: String
    ): ApiResponse<ContenidoEducativo>

    @GET("api/educacion/progreso/{usuarioId}")
    suspend fun obtenerProgreso(
        @Path("usuarioId") usuarioId: String
    ): ApiResponse<ProgresoEducativo>

    @POST("api/educacion/completar")
    suspend fun completarContenido(
        @Body data: Map<String, String>
    ): ApiResponse<Map<String, Any>>

    @GET("api/educacion/quiz/{quizId}")
    suspend fun obtenerQuiz(
        @Path("quizId") quizId: String
    ): ApiResponse<Quiz>

    @POST("api/educacion/quiz/enviar")
    suspend fun enviarQuiz(
        @Body data: Map<String, Any>
    ): ApiResponse<ResultadoQuiz>

    @GET("api/educacion/categorias")
    suspend fun obtenerCategorias(): ApiResponse<List<CategoriaEducativa>>
}