package com.ecocoins.campus.data.model

import com.google.gson.annotations.SerializedName

// ========== RANKING (FASE 2) ==========

data class RankingItem(
    @SerializedName("posicion") val posicion: Int,
    @SerializedName("usuarioId") val usuarioId: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("ecoCoinsGanados") val ecoCoinsGanados: Int,
    @SerializedName("kgReciclados") val kgReciclados: Double,
    @SerializedName("totalReciclajes") val totalReciclajes: Int,
    @SerializedName("porcentaje") val porcentaje: Double,
    @SerializedName("nivel") val nivel: String?
)

data class PosicionUsuario(
    @SerializedName("posicion") val posicion: Int,
    @SerializedName("totalUsuarios") val totalUsuarios: Int,
    @SerializedName("ecoCoinsGanados") val ecoCoinsGanados: Int,
    @SerializedName("kgReciclados") val kgReciclados: Double,
    @SerializedName("totalReciclajes") val totalReciclajes: Int,
    @SerializedName("porcentaje") val porcentaje: Double
)

// ========== LOGROS (FASE 2) ==========

data class Logro(
    @SerializedName("id") val id: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("icono") val icono: String,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("rareza") val rareza: String,
    @SerializedName("recompensaEcoCoins") val recompensaEcoCoins: Int,
    @SerializedName("meta") val meta: Int,
    @SerializedName("progreso") val progreso: Int? = null,
    @SerializedName("desbloqueado") val desbloqueado: Boolean? = null,
    @SerializedName("porcentajeCompletado") val porcentajeCompletado: Int? = null
)

data class LogrosResumen(
    @SerializedName("totalLogros") val totalLogros: Int,
    @SerializedName("logrosDesbloqueados") val logrosDesbloqueados: Int,
    @SerializedName("porcentajeCompletado") val porcentajeCompletado: Int,
    @SerializedName("ecoCoinsGanados") val ecoCoinsGanados: Int
)

// ========== ESTADÍSTICAS DETALLADAS (FASE 2) ==========

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
    @SerializedName("ecoCoinsActuales") val ecoCoinsActuales: Int,
    @SerializedName("ecoCoinsGanados") val ecoCoinsGanados: Int,
    @SerializedName("nivel") val nivel: String,
    @SerializedName("promedioKgPorReciclaje") val promedioKgPorReciclaje: Double
)

data class MaterialStats(
    @SerializedName("material") val material: String,
    @SerializedName("cantidad") val cantidad: Int,
    @SerializedName("kgTotales") val kgTotales: Double,
    @SerializedName("ecoCoins") val ecoCoins: Int,
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

// ========== NOTIFICACIONES (FASE 3) ==========

data class Notificacion(
    @SerializedName("id") val id: String,
    @SerializedName("usuarioId") val usuarioId: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("mensaje") val mensaje: String,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("leida") val leida: Boolean,
    @SerializedName("fecha") val fecha: String
)

// ========== REFERIDOS (FASE 3) ==========

data class ReferidosInfo(
    @SerializedName("codigoReferido") val codigoReferido: String,
    @SerializedName("totalReferidos") val totalReferidos: Int,
    @SerializedName("ecoCoinsGanados") val ecoCoinsGanados: Int,
    @SerializedName("referidos") val referidos: List<ReferidoItem>
)

data class ReferidoItem(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("fechaRegistro") val fechaRegistro: String,
    @SerializedName("recompensaObtenida") val recompensaObtenida: Int
)

data class CodigoReferidoResponse(
    @SerializedName("codigo") val codigo: String,
    @SerializedName("mensaje") val mensaje: String
)

// ========== MAPA (FASE 3) ==========

data class PuntoReciclaje(
    @SerializedName("id") val id: String,
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

// ========== EDUCACIÓN (FASE 4) ==========

data class ContenidoEducativo(
    @SerializedName("id") val id: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("dificultad") val dificultad: String,
    @SerializedName("duracionMinutos") val duracionMinutos: Int,
    @SerializedName("imagenUrl") val imagenUrl: String,
    @SerializedName("puntosClave") val puntosClave: List<String>,
    @SerializedName("recompensaEcoCoins") val recompensaEcoCoins: Int
)

data class ProgresoEducativo(
    @SerializedName("totalContenidos") val totalContenidos: Int,
    @SerializedName("contenidosCompletados") val contenidosCompletados: Int,
    @SerializedName("progresoPorcentaje") val progresoPorcentaje: Int,
    @SerializedName("ecoCoinsGanados") val ecoCoinsGanados: Int
)

data class Quiz(
    @SerializedName("id") val id: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("preguntas") val preguntas: List<Pregunta>,
    @SerializedName("totalPreguntas") val totalPreguntas: Int,
    @SerializedName("tiempoLimiteMinutos") val tiempoLimiteMinutos: Int,
    @SerializedName("recompensaEcoCoins") val recompensaEcoCoins: Int
)

data class Pregunta(
    @SerializedName("id") val id: Int,
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
    @SerializedName("ecoCoinsGanados") val ecoCoinsGanados: Int,
    @SerializedName("nuevoBalance") val nuevoBalance: Int,
    @SerializedName("certificado") val certificado: String?
)

data class CategoriaEducativa(
    @SerializedName("id") val id: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("icono") val icono: String
)

// ========== SOPORTE (FASE 4) ==========

data class FAQ(
    @SerializedName("id") val id: String,
    @SerializedName("pregunta") val pregunta: String,
    @SerializedName("respuesta") val respuesta: String,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("util") val util: Int
)

data class Ticket(
    @SerializedName("id") val id: String,
    @SerializedName("asunto") val asunto: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("prioridad") val prioridad: String,
    @SerializedName("estado") val estado: String,
    @SerializedName("fechaCreacion") val fechaCreacion: String,
    @SerializedName("fechaActualizacion") val fechaActualizacion: String,
    @SerializedName("usuarioId") val usuarioId: String,
    @SerializedName("usuarioNombre") val usuarioNombre: String,
    @SerializedName("respuestas") val respuestas: List<RespuestaTicket>
)

data class RespuestaTicket(
    @SerializedName("id") val id: String,
    @SerializedName("mensaje") val mensaje: String,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("esAdmin") val esAdmin: Boolean,
    @SerializedName("nombreUsuario") val nombreUsuario: String
)