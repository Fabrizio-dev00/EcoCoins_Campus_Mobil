package com.ecocoins.campus.presentation.soporte

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SoporteViewModel @Inject constructor(
    // TODO: Inyectar repositorio
) : ViewModel() {

    private val _uiState = MutableStateFlow(SoporteUiState())
    val uiState: StateFlow<SoporteUiState> = _uiState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                delay(500)

                // MOCK FAQs
                val mockFAQs = listOf(
                    FAQ(
                        id = "1",
                        pregunta = "¿Cómo gano EcoCoins?",
                        respuesta = "Ganas EcoCoins al reciclar materiales. Escanea el código QR del contenedor, registra tu material reciclado y automáticamente recibirás tus EcoCoins. La cantidad varía según el tipo y peso del material.",
                        categoria = CategoriaFAQ.ECOCOINS,
                        util = 45
                    ),
                    FAQ(
                        id = "2",
                        pregunta = "¿Qué materiales puedo reciclar?",
                        respuesta = "Aceptamos: Plástico (botellas PET), Papel y Cartón, Vidrio (botellas y frascos), Metal (latas de aluminio). Asegúrate de que los materiales estén limpios y secos.",
                        categoria = CategoriaFAQ.RECICLAJE,
                        util = 38
                    ),
                    FAQ(
                        id = "3",
                        pregunta = "¿Cómo canjeo mis EcoCoins?",
                        respuesta = "Ve a la sección 'Store' en la app, elige una recompensa y confirma el canje. Recibirás un código de canje que debes presentar al profesor para recibir tu recompensa.",
                        categoria = CategoriaFAQ.CANJES,
                        util = 52
                    ),
                    FAQ(
                        id = "4",
                        pregunta = "¿Cuánto tiempo tarda en aprobarse un canje?",
                        respuesta = "Los canjes son procesados por el profesor responsable. Normalmente toma entre 1-3 días hábiles. Recibirás una notificación cuando tu canje esté listo.",
                        categoria = CategoriaFAQ.CANJES,
                        util = 29
                    ),
                    FAQ(
                        id = "5",
                        pregunta = "¿Cómo cambio mi contraseña?",
                        respuesta = "Ve a Perfil > Configuración > Cambiar Contraseña. Ingresa tu contraseña actual y la nueva contraseña dos veces para confirmar.",
                        categoria = CategoriaFAQ.CUENTA,
                        util = 15
                    ),
                    FAQ(
                        id = "6",
                        pregunta = "¿Qué hago si el scanner no funciona?",
                        respuesta = "Verifica que tengas buena iluminación y que la cámara tenga permiso para funcionar. Si el problema persiste, intenta limpiar la lente de tu cámara o reinicia la app.",
                        categoria = CategoriaFAQ.GENERAL,
                        util = 33
                    )
                )

                // MOCK Tickets
                val mockTickets = listOf(
                    Ticket(
                        id = "T001",
                        asunto = "Problema al escanear código QR",
                        descripcion = "La aplicación no reconoce el código QR del contenedor de plástico en el campus. He intentado varias veces pero sigue sin funcionar.",
                        categoria = CategoriaTicket.PROBLEMA_TECNICO,
                        prioridad = PrioridadTicket.ALTA,
                        estado = EstadoTicket.EN_PROCESO,
                        fechaCreacion = "2024-11-25T10:30:00",
                        fechaActualizacion = "2024-11-26T14:20:00",
                        respuestas = listOf(
                            RespuestaTicket(
                                id = "R1",
                                mensaje = "Gracias por reportar esto. ¿Podrías indicarnos la ubicación exacta del contenedor?",
                                fecha = "2024-11-25T15:45:00",
                                esAdmin = true,
                                nombreUsuario = "Soporte EcoCoins"
                            ),
                            RespuestaTicket(
                                id = "R2",
                                mensaje = "Es el contenedor que está al lado de la biblioteca",
                                fecha = "2024-11-26T09:15:00",
                                esAdmin = false,
                                nombreUsuario = "Tú"
                            ),
                            RespuestaTicket(
                                id = "R3",
                                mensaje = "Perfecto. Hemos actualizado el código QR de ese contenedor. Por favor intenta nuevamente.",
                                fecha = "2024-11-26T14:20:00",
                                esAdmin = true,
                                nombreUsuario = "Soporte EcoCoins"
                            )
                        ),
                        usuarioId = "user123",
                        usuarioNombre = "Juan Pérez"
                    ),
                    Ticket(
                        id = "T002",
                        asunto = "No recibí mis EcoCoins",
                        descripcion = "Reciclé 2 botellas de plástico ayer pero no veo reflejados los EcoCoins en mi cuenta.",
                        categoria = CategoriaTicket.CONSULTA_ECOCOINS,
                        prioridad = PrioridadTicket.MEDIA,
                        estado = EstadoTicket.RESUELTO,
                        fechaCreacion = "2024-11-24T16:00:00",
                        fechaActualizacion = "2024-11-25T11:30:00",
                        respuestas = listOf(
                            RespuestaTicket(
                                id = "R4",
                                mensaje = "Hemos revisado tu cuenta y encontramos que hubo un retraso en el sistema. Ya hemos acreditado tus 50 EcoCoins. ¡Disculpa las molestias!",
                                fecha = "2024-11-25T11:30:00",
                                esAdmin = true,
                                nombreUsuario = "Soporte EcoCoins"
                            )
                        ),
                        usuarioId = "user123",
                        usuarioNombre = "Juan Pérez"
                    ),
                    Ticket(
                        id = "T003",
                        asunto = "Sugerencia: Agregar más puntos de reciclaje",
                        descripcion = "Sería genial tener más contenedores en la zona de cafetería ya que es donde más residuos se generan.",
                        categoria = CategoriaTicket.SUGERENCIA,
                        prioridad = PrioridadTicket.BAJA,
                        estado = EstadoTicket.ABIERTO,
                        fechaCreacion = "2024-11-27T09:00:00",
                        fechaActualizacion = "2024-11-27T09:00:00",
                        respuestas = emptyList(),
                        usuarioId = "user123",
                        usuarioNombre = "Juan Pérez"
                    )
                )

                _uiState.update {
                    it.copy(
                        faqs = mockFAQs,
                        tickets = mockTickets.sortedByDescending { it.fechaCreacion },
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Error al cargar datos: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun crearTicket(
        asunto: String,
        descripcion: String,
        categoria: CategoriaTicket,
        prioridad: PrioridadTicket
    ) {
        viewModelScope.launch {
            val nuevoTicket = Ticket(
                id = "T${System.currentTimeMillis()}",
                asunto = asunto,
                descripcion = descripcion,
                categoria = categoria,
                prioridad = prioridad,
                estado = EstadoTicket.ABIERTO,
                fechaCreacion = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(java.util.Date()),
                fechaActualizacion = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(java.util.Date()),
                respuestas = emptyList(),
                usuarioId = "user123",
                usuarioNombre = "Tú"
            )

            _uiState.update { state ->
                state.copy(
                    tickets = listOf(nuevoTicket) + state.tickets
                )
            }
        }
    }

    fun responderTicket(ticketId: String, mensaje: String) {
        viewModelScope.launch {
            _uiState.update { state ->
                val tickets = state.tickets.map { ticket ->
                    if (ticket.id == ticketId) {
                        val nuevaRespuesta = RespuestaTicket(
                            id = "R${System.currentTimeMillis()}",
                            mensaje = mensaje,
                            fecha = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(java.util.Date()),
                            esAdmin = false,
                            nombreUsuario = "Tú"
                        )
                        ticket.copy(
                            respuestas = ticket.respuestas + nuevaRespuesta,
                            fechaActualizacion = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(java.util.Date())
                        )
                    } else {
                        ticket
                    }
                }

                state.copy(tickets = tickets)
            }
        }
    }

    fun marcarFAQUtil(faqId: String) {
        viewModelScope.launch {
            _uiState.update { state ->
                val faqs = state.faqs.map { faq ->
                    if (faq.id == faqId) {
                        faq.copy(util = faq.util + 1)
                    } else {
                        faq
                    }
                }

                state.copy(faqs = faqs)
            }
        }
    }
}

data class SoporteUiState(
    val faqs: List<FAQ> = emptyList(),
    val tickets: List<Ticket> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)