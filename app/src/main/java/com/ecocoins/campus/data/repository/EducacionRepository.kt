package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.model.*
import com.ecocoins.campus.data.remote.ApiService
import com.ecocoins.campus.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EducacionRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getContenidosEducativos(): Flow<Resource<List<ContenidoEducativo>>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getContenidosEducativos()

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener contenidos: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun getContenidosPorCategoria(categoria: String): Flow<Resource<List<ContenidoEducativo>>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getContenidosPorCategoria(categoria)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener contenidos: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun getContenidoById(contenidoId: Long): Flow<Resource<ContenidoEducativo>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getContenidoById(contenidoId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener contenido: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun completarContenido(contenidoId: Long, usuarioId: Long): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.completarContenido(contenidoId, usuarioId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!.mensaje))
            } else {
                emit(Resource.Error("Error al completar contenido: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun getProgresoEducativo(usuarioId: Long): Flow<Resource<ProgresoEducativo>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getProgresoEducativo(usuarioId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener progreso: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun getCategoriasEducativas(): Flow<Resource<List<CategoriaEducativa>>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getCategoriasEducativas()

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener categorías: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun getQuizzes(): Flow<Resource<List<Quiz>>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getQuizzes()

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener quizzes: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun getQuizById(quizId: Long): Flow<Resource<Quiz>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getQuizById(quizId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener quiz: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun completarQuiz(
        quizId: Long,
        usuarioId: Long,
        respuestas: Map<Long, Int>
    ): Flow<Resource<ResultadoQuiz>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.completarQuiz(quizId, usuarioId, respuestas)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al completar quiz: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }
}