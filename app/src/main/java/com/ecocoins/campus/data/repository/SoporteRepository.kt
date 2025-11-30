package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.model.FAQ
import com.ecocoins.campus.data.model.Resource
import com.ecocoins.campus.data.model.Ticket
import com.ecocoins.campus.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SoporteRepository {

    private val soporteService = RetrofitClient.soporteService

    // ========== FAQs ==========

    suspend fun obtenerFAQs(categoria: String? = null): Resource<List<FAQ>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = soporteService.obtenerFAQs(categoria)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al obtener FAQs")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun obtenerFAQ(faqId: String): Resource<FAQ> {
        return withContext(Dispatchers.IO) {
            try {
                val response = soporteService.obtenerFAQ(faqId)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al obtener FAQ")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun marcarFAQUtil(faqId: String): Resource<FAQ> {
        return withContext(Dispatchers.IO) {
            try {
                val response = soporteService.marcarFAQUtil(faqId)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    // ========== TICKETS ==========

    suspend fun obtenerTickets(
        usuarioId: String,
        estado: String? = null
    ): Resource<List<Ticket>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = soporteService.obtenerTickets(usuarioId, estado)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al obtener tickets")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun obtenerTicket(ticketId: String): Resource<Ticket> {
        return withContext(Dispatchers.IO) {
            try {
                val response = soporteService.obtenerTicket(ticketId)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al obtener ticket")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun crearTicket(
        usuarioId: String,
        asunto: String,
        descripcion: String,
        categoria: String,
        prioridad: String
    ): Resource<Ticket> {
        return withContext(Dispatchers.IO) {
            try {
                val ticketData = mapOf(
                    "usuarioId" to usuarioId,
                    "asunto" to asunto,
                    "descripcion" to descripcion,
                    "categoria" to categoria,
                    "prioridad" to prioridad
                )

                val response = soporteService.crearTicket(ticketData)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al crear ticket")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun responderTicket(
        ticketId: String,
        usuarioId: String,
        mensaje: String
    ): Resource<Ticket> {
        return withContext(Dispatchers.IO) {
            try {
                val respuesta = mapOf(
                    "usuarioId" to usuarioId,
                    "mensaje" to mensaje
                )

                val response = soporteService.responderTicket(ticketId, respuesta)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al responder ticket")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }
}