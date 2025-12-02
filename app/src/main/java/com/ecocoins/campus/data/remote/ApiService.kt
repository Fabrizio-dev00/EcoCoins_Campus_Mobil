package com.ecocoins.campus.data.remote

import com.ecocoins.campus.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ============================================================================
    // AUTH ENDPOINTS
    // ============================================================================

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("api/auth/perfil/{usuarioId}")
    suspend fun getPerfil(@Path("usuarioId") usuarioId: Long): Response<User>

    @PUT("api/auth/perfil/{usuarioId}")
    suspend fun updatePerfil(
        @Path("usuarioId") usuarioId: Long,
        @Body user: User
    ): Response<User>

    // ============================================================================
    // RECICLAJE ENDPOINTS
    // ============================================================================

    @POST("api/reciclajes/registrar")
    suspend fun registrarReciclaje(@Body request: ReciclajeRequest): Response<Reciclaje>

    @GET("api/reciclajes/usuario/{usuarioId}")
    suspend fun getReciclajesUsuario(
        @Path("usuarioId") usuarioId: Long
    ): Response<List<Reciclaje>>

    @GET("api/reciclajes/usuario/{usuarioId}/historial")
    suspend fun getHistorialReciclajes(
        @Path("usuarioId") usuarioId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<List<Reciclaje>>

    @GET("api/reciclajes/{reciclajeId}")
    suspend fun getReciclajeById(@Path("reciclajeId") reciclajeId: Long): Response<Reciclaje>

    @PUT("api/reciclajes/{reciclajeId}/validar")
    suspend fun validarReciclaje(
        @Path("reciclajeId") reciclajeId: Long
    ): Response<MessageResponse>

    // ============================================================================
    // RECOMPENSAS ENDPOINTS
    // ============================================================================

    @GET("api/recompensas/disponibles")
    suspend fun getRecompensasDisponibles(): Response<List<Recompensa>>

    @GET("api/recompensas/{recompensaId}")
    suspend fun getRecompensaById(@Path("recompensaId") recompensaId: Long): Response<Recompensa>

    @POST("api/recompensas/canjear")
    suspend fun canjearRecompensa(@Body request: CanjeRequest): Response<Canje>

    @GET("api/recompensas/canjes/usuario/{usuarioId}")
    suspend fun getCanjesUsuario(
        @Path("usuarioId") usuarioId: Long
    ): Response<List<Canje>>

    @GET("api/recompensas/canjes/usuario/{usuarioId}/historial")
    suspend fun getHistorialCanjes(
        @Path("usuarioId") usuarioId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<List<Canje>>

    @GET("api/recompensas/categorias")
    suspend fun getCategoriasRecompensas(): Response<List<String>>

    // ============================================================================
    // RANKING ENDPOINTS - FASE 2
    // ============================================================================

    @GET("api/ranking/global")
    suspend fun getRankingGlobal(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50
    ): Response<List<RankingItem>>

    @GET("api/ranking/semanal")
    suspend fun getRankingSemanal(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50
    ): Response<List<RankingItem>>

    @GET("api/ranking/mensual")
    suspend fun getRankingMensual(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50
    ): Response<List<RankingItem>>

    @GET("api/ranking/usuario/{usuarioId}/posicion")
    suspend fun getPosicionUsuario(@Path("usuarioId") usuarioId: Long): Response<PosicionUsuario>

    // ============================================================================
    // LOGROS ENDPOINTS - FASE 2
    // ============================================================================

    @GET("api/logros/usuario/{usuarioId}")
    suspend fun getLogrosUsuario(@Path("usuarioId") usuarioId: Long): Response<List<Logro>>

    @GET("api/logros/usuario/{usuarioId}/resumen")
    suspend fun getResumenLogros(@Path("usuarioId") usuarioId: Long): Response<LogrosResumen>

    @POST("api/logros/verificar/{usuarioId}")
    suspend fun verificarLogros(@Path("usuarioId") usuarioId: Long): Response<List<Logro>>

    // ============================================================================
    // ESTADÍSTICAS ENDPOINTS - FASE 2
    // ============================================================================

    @GET("api/estadisticas/usuario/{usuarioId}")
    suspend fun getEstadisticasUsuario(
        @Path("usuarioId") usuarioId: Long
    ): Response<EstadisticasDetalladas>

    @GET("api/estadisticas/usuario/{usuarioId}/resumen")
    suspend fun getResumenEstadisticas(
        @Path("usuarioId") usuarioId: Long
    ): Response<ResumenGeneral>

    @GET("api/estadisticas/usuario/{usuarioId}/materiales")
    suspend fun getDistribucionMateriales(
        @Path("usuarioId") usuarioId: Long
    ): Response<List<MaterialStats>>

    @GET("api/estadisticas/usuario/{usuarioId}/tendencia")
    suspend fun getTendenciaSemanal(
        @Path("usuarioId") usuarioId: Long
    ): Response<List<TendenciaDia>>

    @GET("api/estadisticas/usuario/{usuarioId}/impacto")
    suspend fun getImpactoAmbiental(
        @Path("usuarioId") usuarioId: Long
    ): Response<ImpactoAmbiental>

    // ============================================================================
    // NOTIFICACIONES ENDPOINTS - FASE 3
    // ============================================================================

    @GET("api/notificaciones/usuario/{usuarioId}")
    suspend fun getNotificaciones(@Path("usuarioId") usuarioId: Long): Response<List<Notificacion>>

    @GET("api/notificaciones/usuario/{usuarioId}/no-leidas")
    suspend fun getNotificacionesNoLeidas(
        @Path("usuarioId") usuarioId: Long
    ): Response<List<Notificacion>>

    @PUT("api/notificaciones/{notificacionId}/marcar-leida")
    suspend fun marcarNotificacionLeida(
        @Path("notificacionId") notificacionId: Long
    ): Response<MessageResponse>

    @PUT("api/notificaciones/usuario/{usuarioId}/marcar-todas-leidas")
    suspend fun marcarTodasLeidas(@Path("usuarioId") usuarioId: Long): Response<MessageResponse>

    // ============================================================================
    // REFERIDOS ENDPOINTS - FASE 3
    // ============================================================================

    @GET("api/referidos/usuario/{usuarioId}")
    suspend fun getReferidosInfo(@Path("usuarioId") usuarioId: Long): Response<ReferidosInfo>

    @POST("api/referidos/generar-codigo/{usuarioId}")
    suspend fun generarCodigoReferido(
        @Path("usuarioId") usuarioId: Long
    ): Response<CodigoReferidoResponse>

    @POST("api/referidos/usar-codigo/{usuarioId}")
    suspend fun usarCodigoReferido(
        @Path("usuarioId") usuarioId: Long,
        @Query("codigo") codigo: String
    ): Response<MessageResponse>

    // ============================================================================
    // MAPA ENDPOINTS - FASE 3
    // ============================================================================

    @GET("api/mapa/puntos")
    suspend fun getPuntosReciclaje(): Response<List<PuntoReciclaje>>

    @GET("api/mapa/puntos/cercanos")
    suspend fun getPuntosCercanos(
        @Query("latitud") latitud: Double,
        @Query("longitud") longitud: Double,
        @Query("radio") radio: Double = 5.0
    ): Response<List<PuntoReciclaje>>

    @GET("api/mapa/punto/{puntoId}")
    suspend fun getPuntoById(@Path("puntoId") puntoId: Long): Response<PuntoReciclaje>

    // ============================================================================
    // EDUCACIÓN ENDPOINTS - FASE 4
    // ============================================================================

    @GET("api/educacion/contenidos")
    suspend fun getContenidosEducativos(): Response<List<ContenidoEducativo>>

    @GET("api/educacion/contenidos/categoria/{categoria}")
    suspend fun getContenidosPorCategoria(
        @Path("categoria") categoria: String
    ): Response<List<ContenidoEducativo>>

    @GET("api/educacion/contenido/{contenidoId}")
    suspend fun getContenidoById(@Path("contenidoId") contenidoId: Long): Response<ContenidoEducativo>

    @POST("api/educacion/contenido/{contenidoId}/completar/{usuarioId}")
    suspend fun completarContenido(
        @Path("contenidoId") contenidoId: Long,
        @Path("usuarioId") usuarioId: Long
    ): Response<MessageResponse>

    @GET("api/educacion/usuario/{usuarioId}/progreso")
    suspend fun getProgresoEducativo(
        @Path("usuarioId") usuarioId: Long
    ): Response<ProgresoEducativo>

    @GET("api/educacion/categorias")
    suspend fun getCategoriasEducativas(): Response<List<CategoriaEducativa>>

    @GET("api/educacion/quizzes")
    suspend fun getQuizzes(): Response<List<Quiz>>

    @GET("api/educacion/quiz/{quizId}")
    suspend fun getQuizById(@Path("quizId") quizId: Long): Response<Quiz>

    @POST("api/educacion/quiz/{quizId}/completar/{usuarioId}")
    suspend fun completarQuiz(
        @Path("quizId") quizId: Long,
        @Path("usuarioId") usuarioId: Long,
        @Body respuestas: Map<Long, Int>
    ): Response<ResultadoQuiz>

    // ============================================================================
    // SOPORTE ENDPOINTS - FASE 4
    // ============================================================================

    @GET("api/soporte/faqs")
    suspend fun getFAQs(): Response<List<FAQ>>

    @GET("api/soporte/faqs/categoria/{categoria}")
    suspend fun getFAQsPorCategoria(@Path("categoria") categoria: String): Response<List<FAQ>>

    @POST("api/soporte/faq/{faqId}/util")
    suspend fun marcarFAQUtil(@Path("faqId") faqId: Long): Response<MessageResponse>

    @GET("api/soporte/tickets/usuario/{usuarioId}")
    suspend fun getTicketsUsuario(@Path("usuarioId") usuarioId: Long): Response<List<Ticket>>

    @GET("api/soporte/ticket/{ticketId}")
    suspend fun getTicketById(@Path("ticketId") ticketId: Long): Response<Ticket>

    @POST("api/soporte/ticket/crear")
    suspend fun crearTicket(@Body ticket: Ticket): Response<Ticket>

    @POST("api/soporte/ticket/{ticketId}/responder")
    suspend fun responderTicket(
        @Path("ticketId") ticketId: Long,
        @Body respuesta: RespuestaTicket
    ): Response<MessageResponse>

    @PUT("api/soporte/ticket/{ticketId}/cerrar")
    suspend fun cerrarTicket(@Path("ticketId") ticketId: Long): Response<MessageResponse>
}