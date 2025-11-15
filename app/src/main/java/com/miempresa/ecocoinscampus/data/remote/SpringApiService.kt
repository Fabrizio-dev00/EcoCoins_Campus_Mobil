package com.miempresa.ecocoinscampus.data.remote

import com.miempresa.ecocoinscampus.data.model.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SpringApiService {

    @GET("api/estadisticas")
    suspend fun getEstadisticasGenerales(
        @Header("Authorization") token: String
    ): Response<EstadisticasGenerales>

    @GET("api/estadisticas/ecoins-por-usuario")
    suspend fun getRankingUsuarios(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int = 10
    ): Response<List<UserRanking>>

    @GET("api/estadisticas/ecoins-por-material")
    suspend fun getMaterialesMasReciclados(
        @Header("Authorization") token: String
    ): Response<List<MaterialStat>>

    @GET("api/estadisticas/usuario/{userId}")
    suspend fun getEstadisticasUsuario(
        @Header("Authorization") token: String,
        @Query("userId") userId: String
    ): Response<Map<String, Any>>
}