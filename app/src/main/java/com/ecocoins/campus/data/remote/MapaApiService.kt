package com.ecocoins.campus.data.remote

import com.ecocoins.campus.data.model.ApiResponse
import com.ecocoins.campus.data.model.PuntoReciclaje
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MapaApiService {

    @GET("api/mapa/puntos")
    suspend fun obtenerPuntos(
        @Query("tipo") tipo: String? = null,
        @Query("estado") estado: String? = null
    ): ApiResponse<List<PuntoReciclaje>>

    @GET("api/mapa/puntos/{id}")
    suspend fun obtenerPunto(
        @Path("id") puntoId: String
    ): ApiResponse<PuntoReciclaje>

    @GET("api/mapa/puntos/cercanos")
    suspend fun obtenerPuntosCercanos(
        @Query("latitud") latitud: Double,
        @Query("longitud") longitud: Double,
        @Query("radioKm") radioKm: Double = 5.0
    ): ApiResponse<List<PuntoReciclaje>>

    @GET("api/mapa/puntos/filtrar")
    suspend fun filtrarPorMaterial(
        @Query("material") material: String
    ): ApiResponse<List<PuntoReciclaje>>
}