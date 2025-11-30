package com.ecocoins.campus.data.remote

import com.ecocoins.campus.data.model.ApiResponse
import com.ecocoins.campus.data.model.FAQ
import com.ecocoins.campus.data.model.Ticket
import retrofit2.http.*

interface SoporteApiService {

    // ========== FAQs ==========

    @GET("api/soporte/faqs")
    suspend fun obtenerFAQs(
        @Query("categoria") categoria: String? = null
    ): ApiResponse<List<FAQ>>

    @GET("api/soporte/faqs/{id}")
    suspend fun obtenerFAQ(
        @Path("id") faqId: String
    ): ApiResponse<FAQ>

    @POST("api/soporte/faqs/{id}/util")
    suspend fun marcarFAQUtil(
        @Path("id") faqId: String
    ): ApiResponse<FAQ>

    // ========== TICKETS ==========

    @GET("api/soporte/tickets/usuario/{usuarioId}")
    suspend fun obtenerTickets(
        @Path("usuarioId") usuarioId: String,
        @Query("estado") estado: String? = null
    ): ApiResponse<List<Ticket>>

    @GET("api/soporte/tickets/{id}")
    suspend fun obtenerTicket(
        @Path("id") ticketId: String
    ): ApiResponse<Ticket>

    @POST("api/soporte/tickets")
    suspend fun crearTicket(
        @Body ticketData: Map<String, String>
    ): ApiResponse<Ticket>

    @POST("api/soporte/tickets/{id}/responder")
    suspend fun responderTicket(
        @Path("id") ticketId: String,
        @Body respuesta: Map<String, String>
    ): ApiResponse<Ticket>

    @PUT("api/soporte/tickets/{id}/estado")
    suspend fun cambiarEstadoTicket(
        @Path("id") ticketId: String,
        @Body estado: Map<String, String>
    ): ApiResponse<Ticket>
}