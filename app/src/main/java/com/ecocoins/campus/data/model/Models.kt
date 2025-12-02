package com.ecocoins.campus.data.model

import com.google.gson.annotations.SerializedName

// ============================================================================
// AUTH & USER MODELS
// ============================================================================

data class User(
    @SerializedName("id") val id: Long,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("email") val email: String,
    @SerializedName("carrera") val carrera: String,
    @SerializedName("ecoCoins") val ecoCoins: Long,
    @SerializedName("nivel") val nivel: Int,
    @SerializedName("totalReciclajes") val totalReciclajes: Int,
    @SerializedName("kgTotalesReciclados") val kgTotalesReciclados: Double,
    @SerializedName("fechaRegistro") val fechaRegistro: String
)

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("carrera") val carrera: String
)

data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("usuario") val usuario: User
)

// ============================================================================
// RECICLAJE MODELS
// ============================================================================

data class Reciclaje(
    @SerializedName("id") val id: Long,
    @SerializedName("usuarioId") val usuarioId: Long,
    @SerializedName("materialTipo") val materialTipo: String,
    @SerializedName("cantidad") val cantidad: Double,
    @SerializedName("peso") val peso: Double,
    @SerializedName("ecoCoinsGanados") val ecoCoinsGanados: Long,
    @SerializedName("estado") val estado: String,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("ubicacion") val ubicacion: String?,
    @SerializedName("fotoUrl") val fotoUrl: String?,
    @SerializedName("codigoQR") val codigoQR: String?
)

data class ReciclajeRequest(
    @SerializedName("usuarioId") val usuarioId: Long,
    @SerializedName("materialTipo") val materialTipo: String,
    @SerializedName("cantidad") val cantidad: Double,
    @SerializedName("peso") val peso: Double,
    @SerializedName("ubicacion") val ubicacion: String?,
    @SerializedName("codigoQR") val codigoQR: String?
)

// ============================================================================
// RECOMPENSAS MODELS
// ============================================================================

data class Recompensa(
    @SerializedName("id") val id: Long,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("costoEcoCoins") val costoEcoCoins: Long,
    @SerializedName("stock") val stock: Int,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("imagenUrl") val imagenUrl: String?,
    @SerializedName("disponible") val disponible: Boolean
)

data class Canje(
    @SerializedName("id") val id: Long,
    @SerializedName("usuarioId") val usuarioId: Long,
    @SerializedName("recompensaId") val recompensaId: Long,
    @SerializedName("recompensaNombre") val recompensaNombre: String,
    @SerializedName("costoEcoCoins") val costoEcoCoins: Long,
    @SerializedName("estado") val estado: String,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("codigoCanje") val codigoCanje: String?
)

data class CanjeRequest(
    @SerializedName("usuarioId") val usuarioId: Long,
    @SerializedName("recompensaId") val recompensaId: Long
)

// ============================================================================
// FASE 2: RANKING & LOGROS
// ============================================================================

data class RankingItem(
    @SerializedName("posicion") val posicion: Int,
    @SerializedName("usuarioId") val usuarioId: Long,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("ecoCoinsGanados") val ecoCoinsGanados: Long,
    @SerializedName("kgReciclados") val kgReciclados: Double,
    @SerializedName("totalReciclajes") val totalReciclajes: Int,
    @SerializedName("porcentaje") val porcentaje: Double,
    @SerializedName("nivel") val nivel: Int
)

data class PosicionUsuario(
    @SerializedName("posicion") val posicion: Int,
    @SerializedName("totalUsuarios") val totalUsuarios: Int,
    @SerializedName("ecoCoinsGanados") val ecoCoinsGanados: Long,
    @SerializedName("kgReciclados") val kgReciclados: Double,
    @SerializedName("totalReciclajes") val totalReciclajes: Int,
    @SerializedName("porcentaje") val porcentaje: Double
)

data class Logro(
    @SerializedName("id") val id: Long,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("icono") val icono: String,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("rareza") val rareza: String,
    @SerializedName("recompensaEcoCoins") val recompensaEcoCoins: Long,
    @SerializedName("meta") val meta: Int,
    @SerializedName("progreso") val progreso: Int,
    @SerializedName("desbloqueado") val desbloqueado: Boolean,
    @SerializedName("porcentajeCompletado") val porcentajeCompletado: Double
)

data class LogrosResumen(
    @SerializedName("totalLogros") val totalLogros: Int,
    @SerializedName("logrosDesbloqueados") val logrosDesbloqueados: Int,
    @SerializedName("porcentajeCompletado") val porcentajeCompletado: Double,
    @SerializedName("ecoCoinsGanados") val ecoCoinsGanados: Long
)

data class EstadisticasDetalladas(
    @SerializedName("resumenGeneral") val resumenGeneral: ResumenGeneral,
    @SerializedName("distribucionMateriales") val distribucionMateriales: List<MaterialStats>,
    @SerializedName("tendenciaSemanal") val tendenciaSemanal: List<TendenciaDia>,
    @SerializedName("impactoAmbiental") val impactoAmbiental: ImpactoAmbiental,
    @SerializedName("comparativas") val comparativas: Comparativas,
    @SerializedName("rachas") val rachas: Rachas
)

data class ResumenGeneral(
    @SerializedName("totalReciclajes") val totalReciclajes: Int,
    @SerializedName("totalKgReciclados") val totalKgReciclados: Double,
    @SerializedName("ecoCoinsActuales") val ecoCoinsActuales: Long,
    @SerializedName("ecoCoinsGanados") val ecoCoinsGanados: Long,
    @SerializedName("nivel") val nivel: Int,
    @SerializedName("promedioKgPorReciclaje") val promedioKgPorReciclaje: Double
)

data class MaterialStats(
    @SerializedName("material") val material: String,
    @SerializedName("cantidad") val cantidad: Int,
    @SerializedName("kgTotales") val kgTotales: Double,
    @SerializedName("ecoCoins") val ecoCoins: Long,
    @SerializedName("porcentaje") val porcentaje: Double
)

data class TendenciaDia(
    @SerializedName("dia") val dia: String,
    @SerializedName("reciclajes") val reciclajes: Int
)

data class ImpactoAmbiental(
    @SerializedName("co2Evitado") val co2Evitado: Double,
    @SerializedName("equivalencias") val equivalencias: Equivalencias
)

data class Equivalencias(
    @SerializedName("arboles") val arboles: Int,
    @SerializedName("energia") val energia: Double,
    @SerializedName("agua") val agua: Double
)

data class Comparativas(
    @SerializedName("promedioGeneral") val promedioGeneral: Double,
    @SerializedName("tuPosicion") val tuPosicion: Int,
    @SerializedName("porcentajeSuperior") val porcentajeSuperior: Double
)

data class Rachas(
    @SerializedName("rachaActual") val rachaActual: Int,
    @SerializedName("mejorRacha") val mejorRacha: Int
)

// ============================================================================
// FASE 3: NOTIFICACIONES, REFERIDOS, MAPA
// ============================================================================

data class Notificacion(
    @SerializedName("id") val id: Long,
    @SerializedName("usuarioId") val usuarioId: Long,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("mensaje") val mensaje: String,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("leida") val leida: Boolean,
    @SerializedName("fecha") val fecha: String
)

data class ReferidosInfo(
    @SerializedName("codigoReferido") val codigoReferido: String,
    @SerializedName("totalReferidos") val totalReferidos: Int,
    @SerializedName("ecoCoinsGanados") val ecoCoinsGanados: Long,
    @SerializedName("referidos") val referidos: List<ReferidoItem>
)

data class ReferidoItem(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("fechaRegistro") val fechaRegistro: String,
    @SerializedName("recompensaObtenida") val recompensaObtenida: Long
)

data class CodigoReferidoResponse(
    @SerializedName("codigo") val codigo: String,
    @SerializedName("mensaje") val mensaje: String
)

data class PuntoReciclaje(
    @SerializedName("id") val id: Long,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("direccion") val direccion: String,
    @SerializedName("latitud") val latitud: Double,
    @SerializedName("longitud") val longitud: Double,
    @SerializedName("materialesAceptados") val materialesAceptados: List<String>,
    @SerializedName("horario") val horario: String,
    @SerializedName("capacidadActual") val capacidadActual: Int,
    @SerializedName("estado") val estado: String,
    @SerializedName("distanciaKm") val distanciaKm: Double?
)

// ============================================================================
// FASE 4: EDUCACIÓN & SOPORTE
// ============================================================================

data class ContenidoEducativo(
    @SerializedName("id") val id: Long,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("dificultad") val dificultad: String,
    @SerializedName("duracionMinutos") val duracionMinutos: Int,
    @SerializedName("imagenUrl") val imagenUrl: String?,
    @SerializedName("puntosClave") val puntosClave: List<String>,
    @SerializedName("recompensaEcoCoins") val recompensaEcoCoins: Long
)

data class ProgresoEducativo(
    @SerializedName("totalContenidos") val totalContenidos: Int,
    @SerializedName("contenidosCompletados") val contenidosCompletados: Int,
    @SerializedName("progresoPorcentaje") val progresoPorcentaje: Double,
    @SerializedName("ecoCoinsGanados") val ecoCoinsGanados: Long
)

data class Quiz(
    @SerializedName("id") val id: Long,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("preguntas") val preguntas: List<Pregunta>,
    @SerializedName("totalPreguntas") val totalPreguntas: Int,
    @SerializedName("tiempoLimiteMinutos") val tiempoLimiteMinutos: Int,
    @SerializedName("recompensaEcoCoins") val recompensaEcoCoins: Long
)

data class Pregunta(
    @SerializedName("id") val id: Long,
    @SerializedName("pregunta") val pregunta: String,
    @SerializedName("opciones") val opciones: List<String>,
    @SerializedName("respuestaCorrecta") val respuestaCorrecta: Int,
    @SerializedName("explicacion") val explicacion: String
)

data class ResultadoQuiz(
    @SerializedName("puntuacion") val puntuacion: Int,
    @SerializedName("totalPreguntas") val totalPreguntas: Int,
    @SerializedName("respuestasCorrectas") val respuestasCorrectas: Int,
    @SerializedName("aprobado") val aprobado: Boolean,
    @SerializedName("ecoCoinsGanados") val ecoCoinsGanados: Long,
    @SerializedName("nuevoBalance") val nuevoBalance: Long,
    @SerializedName("certificado") val certificado: String?
)

data class CategoriaEducativa(
    @SerializedName("id") val id: Long,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("icono") val icono: String
)

data class FAQ(
    @SerializedName("id") val id: Long,
    @SerializedName("pregunta") val pregunta: String,
    @SerializedName("respuesta") val respuesta: String,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("util") val util: Int
)

data class Ticket(
    @SerializedName("id") val id: Long,
    @SerializedName("asunto") val asunto: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("prioridad") val prioridad: String,
    @SerializedName("estado") val estado: String,
    @SerializedName("fechaCreacion") val fechaCreacion: String,
    @SerializedName("fechaActualizacion") val fechaActualizacion: String?,
    @SerializedName("usuarioId") val usuarioId: Long,
    @SerializedName("usuarioNombre") val usuarioNombre: String,
    @SerializedName("respuestas") val respuestas: List<RespuestaTicket>
)

data class RespuestaTicket(
    @SerializedName("id") val id: Long,
    @SerializedName("mensaje") val mensaje: String,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("esAdmin") val esAdmin: Boolean,
    @SerializedName("nombreUsuario") val nombreUsuario: String
)

// ============================================================================
// GENERIC RESPONSE MODELS
// ============================================================================

data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: T?
)

data class MessageResponse(
    @SerializedName("mensaje") val mensaje: String
)