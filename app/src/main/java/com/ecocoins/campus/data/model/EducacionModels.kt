package com.ecocoins.campus.data.model

data class ContenidoEducativo(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val tipo: TipoContenido,
    val categoria: CategoriaEducativa,
    val dificultad: NivelDificultad,
    val duracionMinutos: Int,
    val imagenUrl: String? = null,
    val contenido: String,
    val puntosClave: List<String>,
    val recompensaEcoCoins: Int,
    val completado: Boolean = false,
    val fechaPublicacion: String
)

enum class TipoContenido {
    ARTICULO,
    VIDEO,
    INFOGRAFIA,
    QUIZ,
    GUIA
}

enum class CategoriaEducativa {
    RECICLAJE_BASICO,
    SEPARACION_RESIDUOS,
    IMPACTO_AMBIENTAL,
    ECONOMIA_CIRCULAR,
    CONSEJOS_PRACTICOS
}

enum class NivelDificultad {
    PRINCIPIANTE,
    INTERMEDIO,
    AVANZADO
}

data class Quiz(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val preguntas: List<PreguntaQuiz>,
    val recompensaEcoCoins: Int,
    val tiempoLimiteMinutos: Int?,
    val completado: Boolean = false,
    val mejorPuntaje: Int = 0
)

data class PreguntaQuiz(
    val id: String,
    val pregunta: String,
    val opciones: List<String>,
    val respuestaCorrecta: Int,
    val explicacion: String
)

data class ResultadoQuiz(
    val puntuacion: Int,
    val totalPreguntas: Int,
    val respuestasCorrectas: Int,
    val ecoCoinsGanados: Int,
    val certificado: Boolean = false
)