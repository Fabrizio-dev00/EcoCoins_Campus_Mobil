package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.model.*
import com.ecocoins.campus.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EducacionRepository {

    private val educacionService = RetrofitClient.educacionService

    suspend fun obtenerContenidos(
        categoria: String? = null,
        tipo: String? = null
    ): Resource<List<ContenidoEducativo>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = educacionService.obtenerContenidos(categoria, tipo)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al obtener contenidos")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun obtenerContenido(contenidoId: String): Resource<ContenidoEducativo> {
        return withContext(Dispatchers.IO) {
            try {
                val response = educacionService.obtenerContenido(contenidoId)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al obtener contenido")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun obtenerProgreso(usuarioId: String): Resource<ProgresoEducativo> {
        return withContext(Dispatchers.IO) {
            try {
                val response = educacionService.obtenerProgreso(usuarioId)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al obtener progreso")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun completarContenido(
        usuarioId: String,
        contenidoId: String
    ): Resource<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val data = mapOf(
                    "usuarioId" to usuarioId,
                    "contenidoId" to contenidoId
                )

                val response = educacionService.completarContenido(data)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al completar contenido")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun obtenerQuiz(quizId: String): Resource<Quiz> {
        return withContext(Dispatchers.IO) {
            try {
                val response = educacionService.obtenerQuiz(quizId)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al obtener quiz")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun enviarQuiz(
        usuarioId: String,
        quizId: String,
        respuestas: List<Int>
    ): Resource<ResultadoQuiz> {
        return withContext(Dispatchers.IO) {
            try {
                val data = mapOf<String, Any>(
                    "usuarioId" to usuarioId,
                    "quizId" to quizId,
                    "respuestas" to respuestas
                )

                val response = educacionService.enviarQuiz(data)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al enviar quiz")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun obtenerCategorias(): Resource<List<CategoriaEducativa>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = educacionService.obtenerCategorias()

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al obtener categorías")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }
}