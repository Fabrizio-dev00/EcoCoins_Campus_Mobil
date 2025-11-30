package com.ecocoins.campus.data.remote

import com.ecocoins.campus.data.model.ApiResponse
import com.ecocoins.campus.data.model.PosicionUsuario
import com.ecocoins.campus.data.model.RankingItem
import retrofit2.http.GET
import retrofit2.http.Path

interface RankingApiService {

    @GET("api/ranking/{periodo}")
    suspend fun obtenerRanking(
        @Path("periodo") periodo: String // SEMANAL, MENSUAL, HISTORICO
    ): ApiResponse<List<RankingItem>>

    @GET("api/ranking/usuario/{usuarioId}/posicion")
    suspend fun obtenerPosicionUsuario(
        @Path("usuarioId") usuarioId: String
    ): ApiResponse<PosicionUsuario>

    @GET("api/ranking/podio/{periodo}")
    suspend fun obtenerPodio(
        @Path("periodo") periodo: String
    ): ApiResponse<List<RankingItem>>
}