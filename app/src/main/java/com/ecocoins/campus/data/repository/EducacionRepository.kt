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
    fun getContenidosEducativos(): Flow<Resource<List<ContenidoEducativo>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getContenidosEducativos()
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data != null) {
                    emit(Resource.Success(body.data))
                } else {
                    emit(Resource.Error(body?.message ?: "Error desconocido"))
                }
            } else {
                emit(Resource.Error("Error al obtener contenidos: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.message}"))
        }
    }

    fun getContenidosPorCategoria(categoria: String, tipo: String?): Flow<Resource<List<ContenidoEducativo>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getContenidosPorCategoria(categoria, tipo)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data != null) {
                    emit(Resource.Success(body.data))
                } else {
                    emit(Resource.Error(body?.message ?: "Error desconocido"))
                }
            } else {
                emit(Resource.Error("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.message}"))
        }
    }

    fun getContenidoById(contenidoId: String): Flow<Resource<ContenidoEducativo>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getContenidoById(contenidoId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data != null) {
                    emit(Resource.Success(body.data))
                } else {
                    emit(Resource.Error(body?.message ?: "Contenido no encontrado"))
                }
            } else {
                emit(Resource.Error("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.message}"))
        }
    }

    fun completarContenido(contenidoId: String, usuarioId: String): Flow<Resource<Map<String, Any>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.completarContenido(CompletarContenidoRequest(usuarioId, contenidoId))
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data != null) {
                    emit(Resource.Success(body.data))
                } else {
                    emit(Resource.Error(body?.message ?: "Error al completar"))
                }
            } else {
                emit(Resource.Error("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.message}"))
        }
    }

    fun getQuizById(quizId: String): Flow<Resource<Quiz>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getQuizById(quizId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data != null) {
                    emit(Resource.Success(body.data))
                } else {
                    emit(Resource.Error(body?.message ?: "Quiz no encontrado"))
                }
            } else {
                emit(Resource.Error("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.message}"))
        }
    }

    fun enviarQuiz(quizId: String, usuarioId: String, respuestas: List<Int>): Flow<Resource<ResultadoQuiz>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.enviarQuiz(EnviarQuizRequest(usuarioId, quizId, respuestas))
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data != null) {
                    emit(Resource.Success(body.data))
                } else {
                    emit(Resource.Error(body?.message ?: "Error al enviar quiz"))
                }
            } else {
                emit(Resource.Error("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.message}"))
        }
    }

    fun getProgresoEducativo(usuarioId: String): Flow<Resource<ProgresoEducativo>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getProgresoEducativo(usuarioId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data != null) {
                    emit(Resource.Success(body.data))
                } else {
                    emit(Resource.Error(body?.message ?: "Error al obtener progreso"))
                }
            } else {
                emit(Resource.Error("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.message}"))
        }
    }

    fun getCategoriasEducativas(): Flow<Resource<List<CategoriaEducativa>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getCategoriasEducativas()
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data != null) {
                    emit(Resource.Success(body.data))
                } else {
                    emit(Resource.Error(body?.message ?: "Error al obtener categorías"))
                }
            } else {
                emit(Resource.Error("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.message}"))
        }
    }
}
