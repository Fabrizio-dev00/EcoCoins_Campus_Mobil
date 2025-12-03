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
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    emit(Resource.Success(apiResponse.data))
                } else {
                    emit(Resource.Error(apiResponse.message ?: "Error desconocido"))
                }
            } else {
                emit(Resource.Error("Error al obtener contenidos: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage ?: "Error desconocido"}"))
        }
    }

    suspend fun getContenidosPorCategoria(categoria: String?, tipo: String? = null): Flow<Resource<List<ContenidoEducativo>>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getContenidosPorCategoria(categoria.toString(), tipo)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    emit(Resource.Success(apiResponse.data))
                } else {
                    emit(Resource.Error(apiResponse.message ?: "Error desconocido"))
                }
            } else {
                emit(Resource.Error("Error al obtener contenidos: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage ?: "Error desconocido"}"))
        }
    }

    suspend fun getContenidoById(contenidoId: String): Flow<Resource<ContenidoEducativo>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getContenidoById(contenidoId)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    emit(Resource.Success(apiResponse.data))
                } else {
                    emit(Resource.Error(apiResponse.message ?: "Error desconocido"))
                }
            } else {
                emit(Resource.Error("Error al obtener contenido: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage ?: "Error desconocido"}"))
        }
    }

    suspend fun completarContenido(contenidoId: String, usuarioId: String): Flow<Resource<Map<String, Any>>> = flow {
        try {
            emit(Resource.Loading())

            val request = CompletarContenidoRequest(usuarioId, contenidoId)
            val response = apiService.completarContenido(request)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    emit(Resource.Success(apiResponse.data))
                } else {
                    emit(Resource.Error(apiResponse.message ?: "Error desconocido"))
                }
            } else {
                emit(Resource.Error("Error al completar contenido: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage ?: "Error desconocido"}"))
        }
    }

    suspend fun getProgresoEducativo(usuarioId: String): Flow<Resource<ProgresoEducativo>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getProgresoEducativo(usuarioId)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    emit(Resource.Success(apiResponse.data))
                } else {
                    emit(Resource.Error(apiResponse.message ?: "Error desconocido"))
                }
            } else {
                emit(Resource.Error("Error al obtener progreso: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage ?: "Error desconocido"}"))
        }
    }

    suspend fun getCategoriasEducativas(): Flow<Resource<List<CategoriaEducativa>>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getCategoriasEducativas()

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    emit(Resource.Success(apiResponse.data))
                } else {
                    emit(Resource.Error(apiResponse.message ?: "Error desconocido"))
                }
            } else {
                emit(Resource.Error("Error al obtener categorías: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage ?: "Error desconocido"}"))
        }
    }

    suspend fun getQuizById(quizId: String): Flow<Resource<Quiz>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getQuizById(quizId)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    emit(Resource.Success(apiResponse.data))
                } else {
                    emit(Resource.Error(apiResponse.message ?: "Error desconocido"))
                }
            } else {
                emit(Resource.Error("Error al obtener quiz: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage ?: "Error desconocido"}"))
        }
    }

    suspend fun enviarQuiz(
        quizId: String,
        usuarioId: String,
        respuestas: List<Int>
    ): Flow<Resource<ResultadoQuiz>> = flow {
        try {
            emit(Resource.Loading())

            val request = EnviarQuizRequest(usuarioId, quizId, respuestas)
            val response = apiService.enviarQuiz(request)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    emit(Resource.Success(apiResponse.data))
                } else {
                    emit(Resource.Error(apiResponse.message ?: "Error desconocido"))
                }
            } else {
                emit(Resource.Error("Error al enviar quiz: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage ?: "Error desconocido"}"))
        }
    }
}
