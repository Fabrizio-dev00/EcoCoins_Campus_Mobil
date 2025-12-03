package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.model.FAQ
import com.ecocoins.campus.data.model.Ticket
import com.ecocoins.campus.data.remote.ApiService
import com.ecocoins.campus.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SoporteRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getFAQs(): Flow<Resource<List<FAQ>>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getFAQs()

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener FAQs: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun getFAQsPorCategoria(categoria: String): Flow<Resource<List<FAQ>>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getFAQsPorCategoria(categoria)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener FAQs: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun marcarFAQUtil(faqId: String): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.marcarFAQUtil(faqId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!.mensaje))
            } else {
                emit(Resource.Error("Error al marcar FAQ: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun getTicketsUsuario(usuarioId: String): Flow<Resource<List<Ticket>>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getTicketsUsuario(usuarioId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener tickets: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun getTicketById(ticketId: Long): Flow<Resource<Ticket>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getTicketById(ticketId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener ticket: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun crearTicket(ticket: Ticket): Flow<Resource<Ticket>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.crearTicket(ticket)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al crear ticket: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun responderTicket(ticketId: Long, respuesta: String): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.responderTicket(ticketId, respuesta)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!.mensaje))
            } else {
                emit(Resource.Error("Error al responder ticket: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun cerrarTicket(ticketId: Long): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.cerrarTicket(ticketId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!.mensaje))
            } else {
                emit(Resource.Error("Error al cerrar ticket: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }
}