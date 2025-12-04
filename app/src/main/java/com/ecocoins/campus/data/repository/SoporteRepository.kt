package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.model.FAQ
import com.ecocoins.campus.data.model.MessageResponse
import com.ecocoins.campus.data.model.Ticket
import com.ecocoins.campus.data.remote.ApiService
import com.ecocoins.campus.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SoporteRepository @Inject constructor(
    private val apiService: ApiService
) {
    fun getFAQs(): Flow<Resource<List<FAQ>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getFAQs()
            if (response.isSuccessful) {
                emit(Resource.Success(response.body() ?: emptyList()))
            } else {
                emit(Resource.Error("Error al obtener FAQs: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.message}"))
        }
    }

    fun getFAQsPorCategoria(categoria: String): Flow<Resource<List<FAQ>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getFAQsPorCategoria(categoria)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body() ?: emptyList()))
            } else {
                emit(Resource.Error("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.message}"))
        }
    }

    fun marcarFAQUtil(faqId: String): Flow<Resource<MessageResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.marcarFAQUtil(faqId)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body() ?: MessageResponse("OK")))
            } else {
                emit(Resource.Error("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.message}"))
        }
    }

    fun getTicketsUsuario(usuarioId: String): Flow<Resource<List<Ticket>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getTicketsUsuario(usuarioId)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body() ?: emptyList()))
            } else {
                emit(Resource.Error("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.message}"))
        }
    }

    fun getTicketById(ticketId: Long): Flow<Resource<Ticket>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getTicketById(ticketId)
            if (response.isSuccessful) {
                val ticket = response.body()
                if (ticket != null) {
                    emit(Resource.Success(ticket))
                } else {
                    emit(Resource.Error("Ticket no encontrado"))
                }
            } else {
                emit(Resource.Error("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.message}"))
        }
    }

    fun crearTicket(ticket: Ticket): Flow<Resource<Ticket>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.crearTicket(ticket)
            if (response.isSuccessful) {
                val createdTicket = response.body()
                if (createdTicket != null) {
                    emit(Resource.Success(createdTicket))
                } else {
                    emit(Resource.Error("Error al crear ticket"))
                }
            } else {
                emit(Resource.Error("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.message}"))
        }
    }

    fun responderTicket(ticketId: Long, respuesta: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.responderTicket(ticketId, respuesta)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()?.mensaje ?: "Respuesta enviada"))
            } else {
                emit(Resource.Error("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.message}"))
        }
    }

    fun cerrarTicket(ticketId: Long): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.cerrarTicket(ticketId)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()?.mensaje ?: "Ticket cerrado"))
            } else {
                emit(Resource.Error("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.message}"))
        }
    }
}
